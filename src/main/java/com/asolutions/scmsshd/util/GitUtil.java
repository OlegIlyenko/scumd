package com.asolutions.scmsshd.util;

import com.asolutions.scmsshd.event.*;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.ReceiveCommand;
import org.eclipse.jgit.transport.ReceivePack;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.TreeFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.asolutions.scmsshd.util.StringUtil.produce;

/**
 * @author Oleg Ilyenko
 */
public class GitUtil {

    public static <C, F> List<Tuple<C, List<F>>> traversePush(
            ReceivePack rp, ReceiveCommand c, int filesProCommit,
            Function3<RevCommit, List<F>, Integer, C> commitFn, Function1<String, F> fileFn) {
        List<Tuple<C, List<F>>> result = new ArrayList<Tuple<C, List<F>>>();

        RevWalk w = new RevWalk(rp.getRepository());

        RevCommit newCommit = w.lookupCommit(c.getNewId());
        RevCommit oldCommit = w.lookupCommit(c.getOldId());

        try {
            w.sort(RevSort.TOPO);
            w.markStart(newCommit);

            if (!oldCommit.getId().equals(ObjectId.zeroId())) {
                w.markUninteresting(oldCommit);
            }

            RevCommit cc;
            while ((cc = w.next()) != null) {
                List<F> filesResult = new ArrayList<F>();
                int includedFiles = 0;
                int moreFiles = 0;
                List<AnyObjectId> forDiff = new ArrayList<AnyObjectId>();

                for (RevCommit p : cc.getParents()) {
                    forDiff.add(p.getTree());
                }

                forDiff.add(cc.getTree());

                TreeWalk tw = new TreeWalk(rp.getRepository());

                tw.setRecursive(true);
                tw.setFilter(TreeFilter.ANY_DIFF);
                tw.reset(forDiff.toArray(new AnyObjectId[forDiff.size()]));

                while (tw.next()) {
                    F f = fileFn.apply(tw.getPathString());

                    if (filesProCommit == -1 || includedFiles < filesProCommit) {
                        filesResult.add(f);
                        includedFiles++;
                    } else {
                        moreFiles++;
                    }
                }

                result.add(Tuple.valueOf(commitFn.apply(cc, filesResult, moreFiles), filesResult));
            }
        } catch (IOException e) {
			// Quick hack/fix 
			// FIXME: make good fix!
			e.printStackTrace();
            //throw new RuntimeException(e);
        }

        return result;
    }

    public static <T> List<CommitEvent> getCommits(List<Tuple<CommitEvent, T>> res) {
        List<CommitEvent> commits = new ArrayList<CommitEvent>();

        for (Tuple<CommitEvent, T> tc : res) {
            commits.add(tc.getA());
        }
        return commits;
    }

    public static String render(PushEvent event) {
        StringBuilder text = new StringBuilder("Push by user '");

        text.append(event.getUser()).append("' to repository '")
                .append(event.getRepositoryInfo().getRepositoryPath()).append("'\n\n");

        for (CommitEvent c : event.getCommits()) {
            text.append(render(c)).append("\n");
        }

        return text.toString();
    }

    public static String render(CommitEvent c) {
        StringBuilder text = new StringBuilder();

        String title = "Commit: " + c.getRevCommit().getId().getName() +
                " by " + render(c.getRevCommit().getAuthorIdent());

        text.append(title).append("\n");

        if (c.getRevCommit().getFullMessage() != null) {
            text.append(c.getRevCommit().getFullMessage().trim()).append("\n");
        }

        text.append(produce("=", title.length())).append("\n");

        for (FileChangeEvent f : c.getFileChanges()) {
            text.append(f.getPath()).append("\n");
        }

        if (c.getHasMoreFiles() > 0) {
            text.append("...... (").append(c.getHasMoreFiles()).append(" more files)\n");
        }

        return text.toString();
    }

    private static String render(PersonIdent pi) {
        return pi.getName() + (pi.getEmailAddress() != null ? " <" + pi.getEmailAddress() + ">" : "");
    }

    public static String render(AuthenticationSuccessEvent e) {
        return "(" + e.getRemoteAddress() + ") [" + e.getUserName() + "] successfully authenticated with " + e.getMethod();
    }

    public static String render(AuthenticationFailEvent e) {
        return "(" + e.getRemoteAddress() + ") [" +e.getUserName() + "] failed authentication with " + e.getMethod() + " because of: " + e.getReason();
    }

    public static String render(AuthorizationSuccessEvent e) {
        return "(" + e.getRemoteAddress() + ") [" + e.getUser().getName() + "] successfully authorized for the " +
                (e.getRepositoryInfo().isExists() ? "existing" : " non-existing") + " repository '" + e.getRepositoryInfo().getRepositoryPath() +
                "'. He has following privileges for it: " + e.getUserPrivileges() + " - " + e.getAuthorizationLevel() + " was granted";
    }

    public static String render(AuthorizationFailEvent e) {
        return "(" + e.getRemoteAddress() + ") [" + e.getUser().getName() + "] failed to authorize for the " +
                (e.getRepositoryInfo().isExists() ? "existing" : " non-existing") + " repository '" + e.getRepositoryInfo().getRepositoryPath() +
                "'. He has following privileges for it: " + e.getUserPrivileges() + ". Reason: " + e.getReason();
    }

    public static String render(RepositoryCreateEvent e) {
        return e.getUser() + " created repository '" + e.getRepositoryInfo().getRepositoryPath() +
                "' at the server with base path '" + e.getServer().getRepositoriesDir();
    }

    public static String render(PullEvent e) {
        return e.getUser() + " pulled changes from repository " + e.getRepositoryInfo().getRepositoryPath();
    }

}

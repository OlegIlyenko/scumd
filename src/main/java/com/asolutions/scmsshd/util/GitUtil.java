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

        if (c.getType() == ReceiveCommand.Type.CREATE || c.getType() == ReceiveCommand.Type.DELETE) {
            return result; // this is create or delete tag - not a commit
        }

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
            throw new RuntimeException(e);
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
        StringBuilder text = new StringBuilder(getPushTitle(event)).append("\n\n");

        for (CommitEvent c : event.getCommits()) {
            text.append(render(c, false)).append("\n");
        }

        return text.toString();
    }

    private static String getPushTitle(PushEvent e) {
        switch (e.getType()) {
            case Create:
                return "User '" + e.getUser() + "' created ref '" + (e.getRefName() != null ? e.getRefName() : "") +
                        "' in repository '" + e.getRepositoryInfo().getRepositoryPath() + "'";
            case Delete:
                return "User '" + e.getUser() + "' deleted ref '" + (e.getRefName() != null ? e.getRefName() : "") +
                        "' in repository '" + e.getRepositoryInfo().getRepositoryPath() + "'";
            case Update:
                return "Push by user '" + e.getUser() + "' to repository '" + e.getRepositoryInfo().getRepositoryPath() +
                        "'" + (e.getRefName() != null ? " @" + e.getRefName() : "");
            default:
                throw new IllegalArgumentException("Unsupported type: " + e.getType());
        }
    }

    public static String render(CommitEvent c) {
        return render(c, true);
    }

    public static String render(CommitEvent c, boolean showRef) {
        StringBuilder text = new StringBuilder();
        String prefix = produce(" ", 8);
        String title = "Commit: " + c.getRevCommit().getId().getName() +
                " by " + render(c.getRevCommit().getAuthorIdent()) +
                (c.getRefName() != null && showRef ? " (" + c.getRefName() + ")" : "");

        text.append(title).append("\n");

        if (c.getRevCommit().getFullMessage() != null) {
            text.append(prefix).append(c.getRevCommit().getFullMessage().replaceAll("([\r\n]+)", "$1" + prefix).trim()).append("\n");
        }

        text.append(prefix).append(produce("=", title.length() - 8)).append("\n");

        for (FileChangeEvent f : c.getFileChanges()) {
            text.append(prefix).append(f.getPath()).append("\n");
        }

        if (c.getHasMoreFiles() > 0) {
            text.append(prefix).append("...... (").append(c.getHasMoreFiles()).append(" more files)\n");
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

    public static String renderUserName(String userName) {
        return userName == null ? "anonymous" : userName;
    }

    public static RefEvent.Type convert(ReceiveCommand.Type type) {
        switch (type) {
            case UPDATE:
            case UPDATE_NONFASTFORWARD:
                return RefEvent.Type.Update;
            case CREATE:
                return RefEvent.Type.Create;
            case DELETE:
                return RefEvent.Type.Delete;
            default:
                throw new IllegalArgumentException("Unsupported Receive Command type");
        }
    }
}

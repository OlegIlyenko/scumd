package com.asolutions.scmsshd.commands.git;

import com.asolutions.scmsshd.InteractionContext;
import com.asolutions.scmsshd.authorizors.AuthorizationLevel;
import com.asolutions.scmsshd.commands.factories.GitSCMCommandFactory;
import com.asolutions.scmsshd.commands.handlers.CommandContext;
import com.asolutions.scmsshd.event.CancelEventException;
import com.asolutions.scmsshd.event.CommitEvent;
import com.asolutions.scmsshd.event.FileChangeEvent;
import com.asolutions.scmsshd.event.PushEvent;
import com.asolutions.scmsshd.event.impl.CommitEventImpl;
import com.asolutions.scmsshd.event.impl.FileChangeEventImpl;
import com.asolutions.scmsshd.event.impl.PushEventImpl;
import com.asolutions.scmsshd.exceptions.MustHaveWritePrivilagesToPushFailure;
import com.asolutions.scmsshd.util.Function1;
import com.asolutions.scmsshd.util.Function3;
import com.asolutions.scmsshd.util.GitUtil;
import com.asolutions.scmsshd.util.Tuple;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.PostReceiveHook;
import org.eclipse.jgit.transport.PreReceiveHook;
import org.eclipse.jgit.transport.ReceiveCommand;
import org.eclipse.jgit.transport.ReceivePack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.asolutions.scmsshd.event.listener.EventDispatcher.Stage.Post;
import static com.asolutions.scmsshd.event.listener.EventDispatcher.Stage.Pre;
import static com.asolutions.scmsshd.util.GitUtil.getCommits;

public class GitReceivePackSCMCommandHandler extends GitSCMCommandImpl {
	protected final Logger log = LoggerFactory.getLogger(getClass());

	private GitSCMRepositoryProvider repositoryProvider;
	private GitReceivePackProvider receivePackProvider;

    public GitReceivePackSCMCommandHandler(GitSCMRepositoryProvider repositoryProvider) {
        this(repositoryProvider, new GitReceivePackProvider());
    }

	public GitReceivePackSCMCommandHandler(
			GitSCMRepositoryProvider repoProvider,
			GitReceivePackProvider uploadPackProvider) {
		this.repositoryProvider = repoProvider;
		this.receivePackProvider = uploadPackProvider;
	}

	protected void runCommand(final CommandContext commandContext) throws IOException {
        final InteractionContext ctx = commandContext.getInteractionContext();

        if (commandContext.getAuthorizationLevel() == AuthorizationLevel.AUTH_LEVEL_READ_ONLY) {
            throw new MustHaveWritePrivilagesToPushFailure(
                    "Tried to push to " + commandContext.getFilteredCommand().getArgument());
        }

        try {
            File repoBase = new File(commandContext.getConfiguration().getProperty(GitSCMCommandFactory.REPOSITORY_BASE));
            Repository repo = repositoryProvider.provide(repoBase, commandContext);

            ReceivePack rp = receivePackProvider.provide(repo);

            final List<PushEvent> preRes = new ArrayList<PushEvent>();

            rp.setPreReceiveHook(new PreReceiveHook() {
                public void onPreReceive(final ReceivePack rp, Collection<ReceiveCommand> commands) {
                    for (ReceiveCommand command : commands) {
                        try {
                            List<Tuple<CommitEvent, List<FileChangeEvent>>> res = GitUtil.traversePush(
                                rp, command, commandContext.getInteractionContext().getFilesProEventLimit(),
                                    
                                new Function3<RevCommit, List<FileChangeEvent>, Integer, CommitEvent>() {
                                    public CommitEvent apply(RevCommit c, List<FileChangeEvent> files, Integer truncated) {
                                        CommitEvent e = new CommitEventImpl(ctx.getUser(), ctx.getServer(), ctx.getRepositoryInfo(),
                                                c, rp, files, truncated);
                                        ctx.getEventDispatcher().fireEvent(Pre, e);
                                        return e;
                                    }
                                },
                                    
                                new Function1<String, FileChangeEvent>() {
                                    public FileChangeEvent apply(String path) {
                                        FileChangeEvent e =
                                                new FileChangeEventImpl(ctx.getUser(), ctx.getServer(), ctx.getRepositoryInfo(), path);
                                        ctx.getEventDispatcher().fireEvent(Pre, e);
                                        return e;
                                    }
                                });

                            PushEvent pushEvent = new PushEventImpl(ctx.getUser(), ctx.getServer(), ctx.getRepositoryInfo(),
                                    rp, getCommits(res), command);
                            ctx.getEventDispatcher().fireEvent(Pre, pushEvent);

                            preRes.add(pushEvent);
                        } catch (CancelEventException e) {
                            log.info("Command was abborted by listener: \n" + e.getContextInfo());
                            command.setResult(ReceiveCommand.Result.REJECTED_OTHER_REASON);
                        }
                    }
                }
            });
            
            rp.setPostReceiveHook(new PostReceiveHook() {
                public void onPostReceive(final ReceivePack rp, Collection<ReceiveCommand> commands) {
                    for (PushEvent push : preRes) {
                        for (CommitEvent commit : push.getCommits()) {
                            for (FileChangeEvent fileChange : commit.getFileChanges()) {
                                ctx.getEventDispatcher().fireEvent(Post, fileChange);
                            }

                            ctx.getEventDispatcher().fireEvent(Post, commit);
                        }
                        ctx.getEventDispatcher().fireEvent(Post, push);
                    }
                }
            });

            rp.receive(commandContext.getInputStream(), commandContext.getOutputStream(), commandContext.getErrorStream());
        } catch (IOException e) {
            log.error("rp caught ioe: ", e);
            throw e;
        }
    }

}

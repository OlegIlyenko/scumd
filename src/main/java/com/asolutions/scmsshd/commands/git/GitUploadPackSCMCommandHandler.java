package com.asolutions.scmsshd.commands.git;

import com.asolutions.scmsshd.InteractionContext;
import com.asolutions.scmsshd.commands.factories.GitSCMCommandFactory;
import com.asolutions.scmsshd.commands.handlers.CommandContext;
import com.asolutions.scmsshd.event.CancelEventException;
import com.asolutions.scmsshd.event.impl.PullEventImpl;
import com.asolutions.scmsshd.event.listener.EventDispatcher;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.UploadPack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import static com.asolutions.scmsshd.event.listener.EventDispatcher.Stage.Post;
import static com.asolutions.scmsshd.event.listener.EventDispatcher.Stage.Pre;

public class GitUploadPackSCMCommandHandler extends GitSCMCommandImpl {
	protected final Logger log = LoggerFactory.getLogger(getClass());
	private GitSCMRepositoryProvider repositoryProvider;
	private GitUploadPackProvider uploadPackProvider;

    public GitUploadPackSCMCommandHandler(GitSCMRepositoryProvider repositoryProvider) {
		this(repositoryProvider, new GitUploadPackProvider());
	}

	public GitUploadPackSCMCommandHandler(
			GitSCMRepositoryProvider repositoryProvider,
			GitUploadPackProvider uploadPackProvider) {
		this.repositoryProvider = repositoryProvider;
		this.uploadPackProvider = uploadPackProvider;
	}

	@Override
	protected void runCommand(CommandContext commandContext) throws IOException {
        InteractionContext ctx = commandContext.getInteractionContext();
        EventDispatcher eventDispatcher = ctx.getEventDispatcher();

        log.info("Starting Upload Pack Of: " + commandContext.getFilteredCommand().getArgument());

        try {
            eventDispatcher.fireEvent(Pre, new PullEventImpl(ctx.getUser(), ctx.getServer(), ctx.getRepositoryInfo()));
        } catch (CancelEventException e) {
            log.info("Pull request cancelled by listener: " + e.getContextInfo());
            throw e;
        }

        String strRepoBase = commandContext.getConfiguration().getProperty(GitSCMCommandFactory.REPOSITORY_BASE);
		File repoBase = new File(strRepoBase);
		
		Repository repo = repositoryProvider.provide(repoBase, commandContext);
		
		UploadPack uploadPack = uploadPackProvider.provide(repo);

		uploadPack.upload(commandContext.getInputStream(), commandContext.getOutputStream(), commandContext.getErrorStream());

		log.info("Completing Upload Pack: " + commandContext.getFilteredCommand().getArgument());
        eventDispatcher.fireEvent(Post, new PullEventImpl(ctx.getUser(), ctx.getServer(), ctx.getRepositoryInfo()));
	}

}

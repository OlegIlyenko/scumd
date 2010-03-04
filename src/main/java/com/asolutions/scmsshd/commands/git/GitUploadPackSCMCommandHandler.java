package com.asolutions.scmsshd.commands.git;

import com.asolutions.scmsshd.authorizors.AuthorizationLevel;
import com.asolutions.scmsshd.commands.FilteredCommand;
import com.asolutions.scmsshd.commands.factories.GitSCMCommandFactory;
import org.apache.sshd.server.ExitCallback;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.UploadPack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

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
	protected void runCommand(FilteredCommand filteredCommand,
			InputStream inputStream, OutputStream outputStream,
			OutputStream errorStream, ExitCallback exitCallback,
			Properties config, AuthorizationLevel authorizationLevel)
			throws IOException {
		log.info("Starting Upload Pack Of: " + filteredCommand.getArgument());
		
		String strRepoBase = config.getProperty(GitSCMCommandFactory.REPOSITORY_BASE);
		File repoBase = new File(strRepoBase);
		
		Repository repo = repositoryProvider.provide(repoBase, filteredCommand.getArgument());
		
		UploadPack uploadPack = uploadPackProvider.provide(repo);
		uploadPack.upload(inputStream, outputStream, errorStream);
		log.info("Completing Upload Pack: " + filteredCommand.getArgument());
	}

}

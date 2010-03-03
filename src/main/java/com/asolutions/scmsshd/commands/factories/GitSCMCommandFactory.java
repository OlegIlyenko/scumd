package com.asolutions.scmsshd.commands.factories;

import com.asolutions.scmsshd.commands.FilteredCommand;
import com.asolutions.scmsshd.commands.git.GitSCMCommand;
import com.asolutions.scmsshd.commands.git.GitSCMRepositoryProvider;
import com.asolutions.scmsshd.converters.path.IPathToProjectNameConverter;
import com.asolutions.scmsshd.sshd.IProjectAuthorizer;
import org.apache.sshd.server.Command;

import java.util.Properties;

public class GitSCMCommandFactory implements ISCMCommandFactory {

	public static final String REPOSITORY_BASE = "repositoryBase";

    private GitSCMRepositoryProvider repositoryProvider;

    public GitSCMCommandFactory() {}

    public GitSCMCommandFactory(GitSCMRepositoryProvider repositoryProvider) {
        this.repositoryProvider = repositoryProvider;
    }

    public Command create(FilteredCommand filteredCommand,
			IProjectAuthorizer projectAuthenticator,
			IPathToProjectNameConverter pathToProjectNameConverter,
			Properties configuration) {
		return new GitSCMCommand(filteredCommand, projectAuthenticator, pathToProjectNameConverter, configuration, repositoryProvider);
	}

}

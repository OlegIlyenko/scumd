package com.asolutions.scmsshd.commands.git;

import com.asolutions.scmsshd.commands.FilteredCommand;
import com.asolutions.scmsshd.converters.path.PathToProjectNameConverter;
import com.asolutions.scmsshd.sshd.ProjectAuthorizer;

import java.util.Properties;

public class GitSCMCommand extends SCMCommand {

	public GitSCMCommand(FilteredCommand filteredCommand,
						 ProjectAuthorizer projectAuthorizer,
						 PathToProjectNameConverter pathToProjectNameConverter, Properties configuration) {
		this(filteredCommand, projectAuthorizer, pathToProjectNameConverter, configuration, null);
	}

    public GitSCMCommand(FilteredCommand filteredCommand,
						 ProjectAuthorizer projectAuthorizer,
						 PathToProjectNameConverter pathToProjectNameConverter,
                         Properties configuration,
                         GitSCMRepositoryProvider repositoryProvider) {
		setFilteredCommand(filteredCommand);
		setProjectAuthorizer(projectAuthorizer);
		setSCMCommandHandler(new GitSCMCommandHandler(repositoryProvider));
		setPathToProjectNameConverter(pathToProjectNameConverter);
		setConfiguration(configuration);
	}

}

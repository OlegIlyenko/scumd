package com.asolutions.scmsshd.commands.factories;

import com.asolutions.scmsshd.commands.FilteredCommand;
import com.asolutions.scmsshd.converters.path.PathToProjectNameConverter;
import com.asolutions.scmsshd.sshd.ProjectAuthorizer;
import org.apache.sshd.server.Command;

import java.util.Properties;

public interface SCMCommandFactory {

	Command create(FilteredCommand filteredCommand, ProjectAuthorizer mockProjAuth, PathToProjectNameConverter pathToProjectNameConverter, Properties confi);

}

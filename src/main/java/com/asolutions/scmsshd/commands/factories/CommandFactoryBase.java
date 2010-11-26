package com.asolutions.scmsshd.commands.factories;

import com.asolutions.scmsshd.commands.FilteredCommand;
import com.asolutions.scmsshd.commands.NoOpCommand;
import com.asolutions.scmsshd.commands.filters.BadCommandException;
import com.asolutions.scmsshd.commands.filters.BadCommandFilter;
import com.asolutions.scmsshd.converters.path.PathToProjectNameConverter;
import com.asolutions.scmsshd.sshd.ProjectAuthorizer;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.CommandFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class CommandFactoryBase implements CommandFactory {
	protected final Logger log = LoggerFactory.getLogger(getClass());
	private ProjectAuthorizer projectAuthorizer;
	private BadCommandFilter badCommandFilter;
	private SCMCommandFactory scmCommandFactory;
	private PathToProjectNameConverter pathToProjectNameConverter;
	private Properties configuration;

	public CommandFactoryBase() {
	}

	public Command createCommand(String command) {
		log.info("Creating command handler for {}", command);
		try {
			FilteredCommand fc = badCommandFilter.filterOrThrow(command);
			return scmCommandFactory.create(fc, projectAuthorizer, pathToProjectNameConverter, getConfiguration());
		} catch (BadCommandException e) {
			log.error("Got Bad Command Exception For Command: [" + command
					+ "]", e);
			return new NoOpCommand();
		}
	}

	public void setProjectAuthorizor(ProjectAuthorizer projectAuthorizer) {
		this.projectAuthorizer = projectAuthorizer;
	}

	public void setBadCommandFilter(BadCommandFilter badCommandFilter) {
		this.badCommandFilter = badCommandFilter;
	}

	public void setScmCommandFactory(SCMCommandFactory scmCommandFactory) {
		this.scmCommandFactory = scmCommandFactory;
	}

	public BadCommandFilter getBadCommandFilter() {
		return this.badCommandFilter;
	}

	public SCMCommandFactory getScmCommandFactory() {
		return this.scmCommandFactory;
	}

	public void setPathToProjectNameConverter(PathToProjectNameConverter pathToProjectNameConverter) {
		this.pathToProjectNameConverter = pathToProjectNameConverter;
	}
	
	public PathToProjectNameConverter getPathToProjectNameConverter() {
		return pathToProjectNameConverter;
	}

	public void setConfiguration(Properties configuration) {
		this.configuration = configuration;
	}
	
	public Properties getConfiguration() {
		return configuration;
	}

}
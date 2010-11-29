package com.asolutions.scmsshd.commands.git;

import com.asolutions.scmsshd.authorizors.AuthorizationLevel;
import com.asolutions.scmsshd.commands.FilteredCommand;
import com.asolutions.scmsshd.commands.handlers.CommandContext;
import com.asolutions.scmsshd.commands.handlers.SCMCommandHandler;
import com.asolutions.scmsshd.converters.path.PathToProjectNameConverter;
import com.asolutions.scmsshd.sshd.ProjectAuthorizer;
import com.asolutions.scmsshd.sshd.UnparsableProjectException;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.apache.sshd.server.SessionAware;
import org.apache.sshd.server.session.ServerSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class SCMCommand implements Command, SessionAware{
	protected final Logger log = LoggerFactory.getLogger(getClass());

	private InputStream inputStream;
	private OutputStream outputStream;
	private OutputStream errorStream;
	private ExitCallback exitCallback;
	private ServerSession session;
	private FilteredCommand filteredCommand;
	private ProjectAuthorizer projectAuthorizer;
	private PathToProjectNameConverter pathToProjectNameConverter;
	private SCMCommandHandler sCMCommandHandler;
	private Properties configuration;
	
	public SCMCommand() {
		super();
	}

	public ExitCallback getExitCallback() {
		return exitCallback;
	}

	public OutputStream getOutputStream() {
		return outputStream;
	}

	public InputStream getInputStream() {
		return inputStream;
	}
	
	public OutputStream getErrorStream() {
		return errorStream;
	}
	
	public ServerSession getSession() {
		return session;
	}
	
	public void setErrorStream(OutputStream err) {
		this.errorStream = err;
	}

	public void setExitCallback(ExitCallback callback) {
		this.exitCallback = callback;
	}
	
	public void setSession(ServerSession session) {
		this.session = session;
	}

	public void setInputStream(InputStream in) {
		this.inputStream = in;
	}

	public void setOutputStream(OutputStream out) {
		this.outputStream = out;
	}

	public String getUsername() {
		if (session != null){
			return session.getUsername();
		}
		else {
			return null;
		}
	}

	public void setFilteredCommand(FilteredCommand filteredCommand) {
		this.filteredCommand = filteredCommand;
	}

	public FilteredCommand getFilteredCommand() {
		return filteredCommand;
	}

	public void setProjectAuthorizer(ProjectAuthorizer projectAuthenticator) {
		this.projectAuthorizer = projectAuthenticator;
	}
	
	public ProjectAuthorizer getProjectAuthorizer() {
		return projectAuthorizer;
	}
	
	public void setPathToProjectNameConverter(PathToProjectNameConverter pathToProjectNameConverter) {
		this.pathToProjectNameConverter = pathToProjectNameConverter;
	}
	
	public PathToProjectNameConverter getPathToProjectNameConverter() {
		return pathToProjectNameConverter;
	}
	
	public void setSCMCommandHandler(SCMCommandHandler sCMCommandHandler) {
		this.sCMCommandHandler = sCMCommandHandler;
	}
	
	public SCMCommandHandler getSCMCommandHandler() {
		return sCMCommandHandler;
	}

	public void start() throws IOException {
		new Thread("Execute:" + System.currentTimeMillis()) {
			@Override
			public void run() {
				runImpl();
			}
		}.start();
	}
	
	protected void runImpl() {
		try {
			String argument = filteredCommand.getArgument();
			String project = pathToProjectNameConverter.convert(argument);
			String userName = getUsername();
            AuthorizationLevel result = projectAuthorizer.userIsAuthorizedForProject(userName, project, getSession());
			if (result != null) {
                sCMCommandHandler.execute(
                    new CommandContext(
                        filteredCommand,
                        getInputStream(),
                        getOutputStream(),
                        getErrorStream(),
                        getExitCallback(),
                        getConfiguration(),
                        getSession(),
                        result
                    )
                );
			} else {
                getExitCallback().onExit(1);
			}
		} catch (UnparsableProjectException e) {
			log.error("Error running impl" , e);
		}
	}

	public void setConfiguration(Properties config) {
		this.configuration = config;
	}
	
	public Properties getConfiguration() {
		return configuration;
	}

    @Override
    public void start(Environment environment) throws IOException {
        start();
    }

    @Override
    public void destroy() {
    }
}
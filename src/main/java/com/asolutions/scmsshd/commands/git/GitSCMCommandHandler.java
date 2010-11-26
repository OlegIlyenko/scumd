package com.asolutions.scmsshd.commands.git;

import com.asolutions.scmsshd.authorizors.AuthorizationLevel;
import com.asolutions.scmsshd.commands.FilteredCommand;
import com.asolutions.scmsshd.commands.handlers.SCMCommandHandler;
import org.apache.sshd.server.ExitCallback;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class GitSCMCommandHandler implements SCMCommandHandler {
	
	private SCMCommandHandler uploadPackHandler;
	private SCMCommandHandler receivePackHandler;

    public GitSCMCommandHandler(GitSCMRepositoryProvider repositoryProvider) {
		this(new GitUploadPackSCMCommandHandler(repositoryProvider), new GitReceivePackSCMCommandHandler(repositoryProvider));
	}

	public GitSCMCommandHandler(SCMCommandHandler uploadPackHandler,
			                    SCMCommandHandler receivePackHandler) {
		this.uploadPackHandler = uploadPackHandler;
		this.receivePackHandler = receivePackHandler;
	}

	public void execute(FilteredCommand filteredCommand,
			InputStream inputStream, OutputStream outputStream,
			OutputStream errorStream, ExitCallback exitCallback, 
			Properties configuration,
			AuthorizationLevel authorizationLevel) {
		SCMCommandHandler handler = null;
		if ("git-upload-pack".equals(filteredCommand.getCommand())){
			handler = uploadPackHandler;
		}
		else if ("git-receive-pack".equals(filteredCommand.getCommand()))
		{
			handler = receivePackHandler;
		}
		if (handler != null)
		{
			handler.execute(filteredCommand, inputStream, outputStream, errorStream, exitCallback, configuration, authorizationLevel);
		}
	}

}

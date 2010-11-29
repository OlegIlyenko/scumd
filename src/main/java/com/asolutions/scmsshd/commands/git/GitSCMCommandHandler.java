package com.asolutions.scmsshd.commands.git;

import com.asolutions.scmsshd.commands.handlers.CommandContext;
import com.asolutions.scmsshd.commands.handlers.SCMCommandHandler;

public class GitSCMCommandHandler implements SCMCommandHandler {
	
	private SCMCommandHandler uploadPackHandler;
	private SCMCommandHandler receivePackHandler;

    public GitSCMCommandHandler(GitSCMRepositoryProvider repositoryProvider) {
		this(new GitUploadPackSCMCommandHandler(repositoryProvider), new GitReceivePackSCMCommandHandler(repositoryProvider));
	}

	public GitSCMCommandHandler(SCMCommandHandler uploadPackHandler, SCMCommandHandler receivePackHandler) {
		this.uploadPackHandler = uploadPackHandler;
		this.receivePackHandler = receivePackHandler;
	}

	public void execute(CommandContext commandContext) {
		SCMCommandHandler handler = null;

		if ("git-upload-pack".equals(commandContext.getFilteredCommand().getCommand())){
			handler = uploadPackHandler;
		} else if ("git-receive-pack".equals(commandContext.getFilteredCommand().getCommand())) {
			handler = receivePackHandler;
		} if (handler != null) {
			handler.execute(commandContext);
		}
	}

}

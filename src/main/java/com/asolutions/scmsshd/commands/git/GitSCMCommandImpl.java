package com.asolutions.scmsshd.commands.git;

import com.asolutions.scmsshd.commands.handlers.CommandContext;
import com.asolutions.scmsshd.commands.handlers.SCMCommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public abstract class GitSCMCommandImpl implements SCMCommandHandler {
	protected final Logger log = LoggerFactory.getLogger(getClass());

	public GitSCMCommandImpl() {
		super();
	}

	public void execute(CommandContext commandContext) {
		try {
			try {
				runCommand(commandContext);
			} catch (IOException e) {
				log.error("Error Executing " + commandContext.getFilteredCommand(), e);
			}

            log.info("command completed normally");
		} finally {
			try {
				commandContext.getOutputStream().flush();
			} catch (IOException err) {
				log.error("Error Executing " + commandContext.getFilteredCommand(), err);
			}

            try {
				commandContext.getErrorStream().flush();
			} catch (IOException err) {
				log.error("Error Executing " + commandContext.getFilteredCommand(), err);
			}

            commandContext.getExitCallback().onExit(0);
		}

	}

	protected abstract void runCommand(CommandContext commandContext) throws IOException;

}
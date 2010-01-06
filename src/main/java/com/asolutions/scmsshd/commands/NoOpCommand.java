package com.asolutions.scmsshd.commands;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.sshd.server.Command;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoOpCommand implements Command {
	protected final Logger log = LoggerFactory.getLogger(getClass());
	private ExitCallback callback;

	public void setErrorStream(OutputStream err) {
	}

	public void setExitCallback(ExitCallback callback) {
		this.callback = callback;
	}

	public void setInputStream(InputStream in) {
	}

	public void setOutputStream(OutputStream out) {
	}

	public void start() throws IOException {
		log.info("Executing No Op Command");
		callback.onExit(-1);
	}

    @Override
    public void start(Environment environment) throws IOException {
        start();
    }

    @Override
    public void destroy() {
        log.info("Destroying No Op Command");
    }
}

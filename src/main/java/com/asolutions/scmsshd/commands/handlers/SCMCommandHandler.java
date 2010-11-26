package com.asolutions.scmsshd.commands.handlers;

import com.asolutions.scmsshd.authorizors.AuthorizationLevel;
import com.asolutions.scmsshd.commands.FilteredCommand;
import org.apache.sshd.server.ExitCallback;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public interface SCMCommandHandler {

	void execute(FilteredCommand filteredCommand, InputStream inputStream,
			OutputStream outputStream, OutputStream errorStream,
			ExitCallback exitCallback, Properties configuration, AuthorizationLevel authorizationLevel);

}

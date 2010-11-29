package com.asolutions.scmsshd.commands.handlers;

import com.asolutions.scmsshd.InteractionContext;
import com.asolutions.scmsshd.InteractionContextKey;
import com.asolutions.scmsshd.authorizors.AuthorizationLevel;
import com.asolutions.scmsshd.commands.FilteredCommand;
import org.apache.sshd.server.ExitCallback;
import org.apache.sshd.server.session.ServerSession;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * @author Oleg Ilyenko
 */
public class CommandContext {
    private final FilteredCommand filteredCommand;
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private final OutputStream errorStream;
    private final ExitCallback exitCallback;
    private final Properties configuration;
    private final ServerSession session;
    private final AuthorizationLevel authorizationLevel;

    public CommandContext(
            FilteredCommand filteredCommand,
            InputStream inputStream,
            OutputStream outputStream,
            OutputStream errorStream,
            ExitCallback exitCallback,
            Properties configuration,
            ServerSession session,
            AuthorizationLevel authorizationLevel) {
        this.filteredCommand = filteredCommand;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.errorStream = errorStream;
        this.exitCallback = exitCallback;
        this.configuration = configuration;
        this.session = session;
        this.authorizationLevel = authorizationLevel;
    }

    public FilteredCommand getFilteredCommand() {
        return filteredCommand;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public OutputStream getErrorStream() {
        return errorStream;
    }

    public ExitCallback getExitCallback() {
        return exitCallback;
    }

    public Properties getConfiguration() {
        return configuration;
    }

    public ServerSession getSession() {
        return session;
    }

    public AuthorizationLevel getAuthorizationLevel() {
        return authorizationLevel;
    }

    public InteractionContext getInteractionContext() {
        return session.getAttribute(InteractionContextKey.get());
    }
}

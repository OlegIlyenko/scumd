package com.asolutions.scmsshd.event.impl;

import com.asolutions.scmsshd.ConfigurableGitSshServer;
import com.asolutions.scmsshd.event.GitServerStopEvent;

/**
 * @author Oleg Ilyenko
 */
public class GitServerStopEventImpl implements GitServerStopEvent {
    private final ConfigurableGitSshServer server;

    public GitServerStopEventImpl(ConfigurableGitSshServer server) {
        this.server = server;
    }

    public ConfigurableGitSshServer getServer() {
        return server;
    }
}

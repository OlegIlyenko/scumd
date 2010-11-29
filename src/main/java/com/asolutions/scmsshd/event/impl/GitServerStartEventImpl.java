package com.asolutions.scmsshd.event.impl;

import com.asolutions.scmsshd.ConfigurableGitSshServer;
import com.asolutions.scmsshd.event.GitServerStartEvent;

/**
 * @author Oleg Ilyenko
 */
public class GitServerStartEventImpl implements GitServerStartEvent {

    private final ConfigurableGitSshServer server;

    public GitServerStartEventImpl(ConfigurableGitSshServer server) {
        this.server = server;
    }

    public ConfigurableGitSshServer getServer() {
        return server;
    }
}

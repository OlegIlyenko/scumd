package com.asolutions.scmsshd.event.impl;

import com.asolutions.scmsshd.ConfigurableGitSshServer;
import com.asolutions.scmsshd.event.GitServerEvent;
import com.asolutions.scmsshd.event.UserEvent;
import com.asolutions.scmsshd.model.security.User;

/**
 * @author Oleg Ilyenko
 */
public class BaseServerUserEvent implements GitServerEvent, UserEvent {

    private final User user;

    private final ConfigurableGitSshServer server;

    public BaseServerUserEvent(User user, ConfigurableGitSshServer server) {
        this.user = user;
        this.server = server;
    }

    public User getUser() {
        return user;
    }

    public ConfigurableGitSshServer getServer() {
        return server;
    }

}

package com.asolutions.scmsshd.event.impl;

import com.asolutions.scmsshd.ConfigurableGitSshServer;
import com.asolutions.scmsshd.event.AuthorizationEvent;
import com.asolutions.scmsshd.model.security.Privilege;
import com.asolutions.scmsshd.model.security.User;

import java.net.SocketAddress;
import java.util.Set;

/**
 * @author Oleg Ilyenko
 */
public class AuthorizationEventImpl extends BaseServerRepositoryUserEvent implements AuthorizationEvent {

    private Set<Privilege> userPrivileges;

    private SocketAddress remoteAddress;

    public AuthorizationEventImpl(User user, ConfigurableGitSshServer server, RepositoryInfo repositoryInfo, Set<Privilege> userPrivileges, SocketAddress remoteAddress) {
        super(user, server, repositoryInfo);
        this.userPrivileges = userPrivileges;
        this.remoteAddress = remoteAddress;
    }

    public SocketAddress getRemoteAddress() {
        return remoteAddress;
    }

    public Set<Privilege> getUserPrivileges() {
        return userPrivileges;
    }
}

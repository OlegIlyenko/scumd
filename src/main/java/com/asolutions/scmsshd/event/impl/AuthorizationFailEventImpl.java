package com.asolutions.scmsshd.event.impl;

import com.asolutions.scmsshd.ConfigurableGitSshServer;
import com.asolutions.scmsshd.event.AuthorizationFailEvent;
import com.asolutions.scmsshd.model.security.Privilege;
import com.asolutions.scmsshd.model.security.User;

import java.net.SocketAddress;
import java.util.Set;

/**
 * @author Oleg Ilyenko
 */
public class AuthorizationFailEventImpl extends AuthorizationEventImpl implements AuthorizationFailEvent {

    private String reason;

    public AuthorizationFailEventImpl(User user, ConfigurableGitSshServer server, RepositoryInfo repositoryInfo, Set<Privilege> userPrivileges, SocketAddress remoteAddress, String reason) {
        super(user, server, repositoryInfo, userPrivileges, remoteAddress);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}

package com.asolutions.scmsshd.event.impl;

import com.asolutions.scmsshd.ConfigurableGitSshServer;
import com.asolutions.scmsshd.authorizors.AuthorizationLevel;
import com.asolutions.scmsshd.event.AuthorizationSuccessEvent;
import com.asolutions.scmsshd.model.security.Privilege;
import com.asolutions.scmsshd.model.security.User;

import java.net.SocketAddress;
import java.util.Set;

/**
 * @author Oleg Ilyenko
 */
public class AuthorizationSuccessEventImpl extends AuthorizationEventImpl implements AuthorizationSuccessEvent {

    private final AuthorizationLevel authorizationLevel;

    public AuthorizationSuccessEventImpl(User user, ConfigurableGitSshServer server, RepositoryInfo repositoryInfo, Set<Privilege> userPrivileges, SocketAddress remoteAddress, AuthorizationLevel authorizationLevel) {
        super(user, server, repositoryInfo, userPrivileges, remoteAddress);
        this.authorizationLevel = authorizationLevel;
    }

    public AuthorizationLevel getAuthorizationLevel() {
        return authorizationLevel;
    }
}

package com.asolutions.scmsshd.event.impl;

import com.asolutions.scmsshd.ConfigurableGitSshServer;
import com.asolutions.scmsshd.event.AuthenticationEvent;
import com.asolutions.scmsshd.model.security.User;

import java.net.SocketAddress;

/**
 * @author Oleg Ilyenko
 */
public class AuthenticationEventImpl extends BaseServerUserEvent implements AuthenticationEvent {

    private String userName;

    private SocketAddress remoteAddress;

    private String method;

    public AuthenticationEventImpl(User user, ConfigurableGitSshServer server, String userName, SocketAddress remoteAddress, String method) {
        super(user, server);
        this.userName = userName;
        this.remoteAddress = remoteAddress;
        this.method = method;
    }

    public String getUserName() {
        return userName;
    }

    public String getMethod() {
        return method;
    }

    public SocketAddress getRemoteAddress() {
        return remoteAddress;
    }
}

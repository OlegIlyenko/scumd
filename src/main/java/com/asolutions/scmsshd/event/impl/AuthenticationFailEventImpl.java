package com.asolutions.scmsshd.event.impl;

import com.asolutions.scmsshd.ConfigurableGitSshServer;
import com.asolutions.scmsshd.event.AuthenticationFailEvent;
import com.asolutions.scmsshd.model.security.User;

import java.net.SocketAddress;

/**
 * @author Oleg Ilyenko
 */
public class AuthenticationFailEventImpl extends AuthenticationEventImpl implements AuthenticationFailEvent {

    private String reason;

    public AuthenticationFailEventImpl(User user, ConfigurableGitSshServer server, String userName, SocketAddress remoteAddress, String method, String reason) {
        super(user, server, userName, remoteAddress, method);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}

package com.asolutions.scmsshd.event.impl;

import com.asolutions.scmsshd.ConfigurableGitSshServer;
import com.asolutions.scmsshd.event.AuthenticationSuccessEvent;
import com.asolutions.scmsshd.model.security.User;

import java.net.SocketAddress;

/**
 * @author Oleg Ilyenko
 */
public class AuthenticationSuccessEventImpl extends AuthenticationEventImpl implements AuthenticationSuccessEvent {

    public AuthenticationSuccessEventImpl(User user, ConfigurableGitSshServer server, String userName, SocketAddress remoteAddress, String method) {
        super(user, server, userName, remoteAddress, method);
    }
    
}

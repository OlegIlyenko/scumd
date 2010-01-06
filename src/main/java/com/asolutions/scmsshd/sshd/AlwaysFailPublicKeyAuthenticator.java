package com.asolutions.scmsshd.sshd;

import java.security.PublicKey;

import org.apache.sshd.server.PublickeyAuthenticator;
import org.apache.sshd.server.session.ServerSession;

public class AlwaysFailPublicKeyAuthenticator implements PublickeyAuthenticator {

    @Override
    public boolean authenticate(String username, PublicKey key, ServerSession session) {
        return false;
    }
}

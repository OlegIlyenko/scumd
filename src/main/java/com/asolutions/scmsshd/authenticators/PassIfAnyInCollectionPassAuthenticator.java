package com.asolutions.scmsshd.authenticators;

import org.apache.sshd.server.session.ServerSession;

import java.util.ArrayList;

public class PassIfAnyInCollectionPassAuthenticator implements
        PasswordAuthenticator {

	private ArrayList<PasswordAuthenticator> authList = new ArrayList<PasswordAuthenticator>();

	public boolean authenticate(String username, String password, ServerSession session) {
		for (PasswordAuthenticator auth : authList) {
			final boolean authResult = auth.authenticate(username, password, session);
			if (authResult){
				return true;
			}
		}
		return false;
	}

	public void setAuthenticators(ArrayList authList) {
		this.authList = authList;
	}

}

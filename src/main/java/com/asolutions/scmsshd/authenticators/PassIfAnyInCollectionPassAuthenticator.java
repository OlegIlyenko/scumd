package com.asolutions.scmsshd.authenticators;

import java.util.ArrayList;

import org.apache.sshd.server.session.ServerSession;

public class PassIfAnyInCollectionPassAuthenticator implements
		IPasswordAuthenticator {

	private ArrayList<IPasswordAuthenticator> authList = new ArrayList<IPasswordAuthenticator>();

	public boolean authenticate(String username, String password, ServerSession session) {
		for (IPasswordAuthenticator auth : authList) {
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

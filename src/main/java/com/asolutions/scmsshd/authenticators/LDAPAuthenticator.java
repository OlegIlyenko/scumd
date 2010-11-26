package com.asolutions.scmsshd.authenticators;

import com.asolutions.scmsshd.ldap.LDAPAuthLookupProvider;
import org.apache.sshd.server.session.ServerSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.NamingException;
import javax.naming.directory.SearchResult;

public class LDAPAuthenticator implements PasswordAuthenticator {

	protected final Logger log = LoggerFactory.getLogger(getClass());
	private String url;
	private String userBase;
	private boolean promiscuous;
	private LDAPAuthLookupProvider provider;

	public LDAPAuthenticator(String url, String userBase, boolean promiscuous) {
		this(url, userBase, promiscuous, new JavaxNamingLDAPAuthLookupProvider());
	}

	public LDAPAuthenticator(String url, String userBase, boolean promiscuous,
			                 LDAPAuthLookupProvider provider) {
		this.url = url;
		this.userBase = userBase;
		this.promiscuous = promiscuous;
		this.provider = provider;
	}

	public boolean authenticate(String username, String password, ServerSession session) {
		username = "cn=" + username + "," + userBase;
		try {
            final SearchResult searchResult = provider.provide(url, username, password, promiscuous);
            return searchResult != null;
		} catch (NamingException e) {
			log.error("Error Authenticating", e);
			return false;
		}
	}

}

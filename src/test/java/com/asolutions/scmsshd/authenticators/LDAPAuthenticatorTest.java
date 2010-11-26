package com.asolutions.scmsshd.authenticators;

import com.asolutions.MockTestCase;
import com.asolutions.scmsshd.ldap.LDAPAuthLookupProvider;
import org.apache.sshd.server.session.ServerSession;
import org.jmock.Expectations;
import org.junit.Test;

import javax.naming.directory.SearchResult;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class LDAPAuthenticatorTest extends MockTestCase {
	
	private static final String USERBASE = "cn=Users,dn=server,dn=lan";
	private static final String URL = "ldaps://server.lan";
	private static final String PASSWORD = "password";
	private static final String USERNAME = "username";
	private static String USERDN = "cn=" + USERNAME + "," + USERBASE;

	@Test
	public void testAuthenticatePassesWithNoException() throws Exception {
		final LDAPAuthLookupProvider mockAuthLookupProvider = context.mock(LDAPAuthLookupProvider.class);
		final ServerSession mockServerSession = context.mock(ServerSession.class);
        final SearchResult mockSearchResult = context.mock(SearchResult.class);
		checking(new Expectations(){{
			oneOf(mockAuthLookupProvider).provide(URL, USERDN, PASSWORD, true);
			will(returnValue(mockSearchResult));
		}});
		
		LDAPAuthenticator auth = new LDAPAuthenticator(URL, USERBASE, true, mockAuthLookupProvider);
		
		assertTrue(auth.authenticate(USERNAME, PASSWORD, mockServerSession));
	}
	
	@Test
	public void testAuthenticateFailsNull() throws Exception {
		final LDAPAuthLookupProvider mockAuthLookupProvider = context.mock(LDAPAuthLookupProvider.class);
		final ServerSession mockServerSession = context.mock(ServerSession.class);
		
		checking(new Expectations(){{
			oneOf(mockAuthLookupProvider).provide(URL, USERDN, PASSWORD, true);
			will(returnValue(null));
		}});
		
		LDAPAuthenticator auth = new LDAPAuthenticator("ldaps://server.lan", USERBASE, true, mockAuthLookupProvider);
		
		assertFalse(auth.authenticate(USERNAME, PASSWORD, mockServerSession));
	}

}

package com.asolutions.scmsshd.authenticators;

import com.asolutions.MockTestCase;
import org.apache.sshd.server.session.ServerSession;
import org.jmock.Expectations;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;


public class PassIfAnyInCollectionPassAuthenticatorTest extends MockTestCase {
	
	private static final String PASSWORD = "password";
	private static final String USERNAME = "username";
	
	@Test
	public void testFailsWithNothingInChain() throws Exception {
		final ServerSession mockServerSession = context.mock(ServerSession.class);
		assertFalse(new PassIfAnyInCollectionPassAuthenticator().authenticate(USERNAME, PASSWORD, mockServerSession));
	}
	
	@Test
	public void testPassIfAnyPass() throws Exception {

		final PasswordAuthenticator failsAuth = context.mock(PasswordAuthenticator.class, "failsAuth");
		final PasswordAuthenticator passesAuth = context.mock(PasswordAuthenticator.class, "passesAuth");
		final ServerSession mockServerSession = context.mock(ServerSession.class);
		
		checking(new Expectations(){{
			allowing(failsAuth).authenticate(USERNAME, PASSWORD, mockServerSession);
			will(returnValue(false));
			allowing(passesAuth).authenticate(USERNAME, PASSWORD, mockServerSession);
			will(returnValue(true));
		}});
		
		PassIfAnyInCollectionPassAuthenticator auth = new PassIfAnyInCollectionPassAuthenticator();
		ArrayList authList = new ArrayList();
		authList.add(failsAuth);
		authList.add(passesAuth);
		authList.add(failsAuth);
		auth.setAuthenticators(authList);
		assertNotNull(auth.authenticate(USERNAME, PASSWORD, mockServerSession));
	}
	
	@Test
	public void testFailIfNonePass() throws Exception {
		final PasswordAuthenticator failsAuth = context.mock(PasswordAuthenticator.class, "failsAuth");
		final ServerSession mockServerSession = context.mock(ServerSession.class);
		
		checking(new Expectations(){{
			allowing(failsAuth).authenticate(USERNAME, PASSWORD, mockServerSession);
			will(returnValue(false));
		}});
		
		PassIfAnyInCollectionPassAuthenticator auth = new PassIfAnyInCollectionPassAuthenticator();
		ArrayList authList = new ArrayList();
		authList.add(failsAuth);
		authList.add(failsAuth);
		auth.setAuthenticators(authList);
		assertFalse(auth.authenticate(USERNAME, PASSWORD, mockServerSession));
	}

}

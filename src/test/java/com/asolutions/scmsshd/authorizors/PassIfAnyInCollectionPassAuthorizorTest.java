package com.asolutions.scmsshd.authorizors;

import com.asolutions.MockTestCase;
import com.asolutions.scmsshd.sshd.ProjectAuthorizer;
import org.jmock.Expectations;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PassIfAnyInCollectionPassAuthorizorTest extends MockTestCase {
	
	private static final String PROJECT = "project";
	private static final String USERNAME = "username";

	@Test
	public void testAuthingWithEmptyChainFails() throws Exception {
		assertNull(new PassIfAnyInCollectionPassAuthorizor().userIsAuthorizedForProject(USERNAME, PROJECT, null));
	}
	
	@Test
	public void testIfAnyPassItPasses() throws Exception {
		final ProjectAuthorizer failsAuth = context.mock(ProjectAuthorizer.class, "failsAuth");
		final ProjectAuthorizer passesAuth = context.mock(ProjectAuthorizer.class, "passesAuth");
		
		checking(new Expectations(){{
			allowing(failsAuth).userIsAuthorizedForProject(USERNAME, PROJECT, null);
			will(returnValue(null));
			allowing(passesAuth).userIsAuthorizedForProject(USERNAME, PROJECT, null);
			will(returnValue(AuthorizationLevel.AUTH_LEVEL_READ_ONLY));
		}});
		
		PassIfAnyInCollectionPassAuthorizor auth = new PassIfAnyInCollectionPassAuthorizor();
		ArrayList authList = new ArrayList();
		authList.add(failsAuth);
		authList.add(passesAuth);
		authList.add(failsAuth);
		auth.setProjectAuthorizers(authList);
		assertEquals(AuthorizationLevel.AUTH_LEVEL_READ_ONLY, auth.userIsAuthorizedForProject(USERNAME, PROJECT, null));
	}
	
	@Test
	public void testIfNonePassItFails() throws Exception {
		final ProjectAuthorizer failsAuth = context.mock(ProjectAuthorizer.class, "failsAuth");
		
		checking(new Expectations(){{
			allowing(failsAuth).userIsAuthorizedForProject(USERNAME, PROJECT, null);
			will(returnValue(null));
		}});
		
		PassIfAnyInCollectionPassAuthorizor auth = new PassIfAnyInCollectionPassAuthorizor();
		ArrayList authList = new ArrayList();
		authList.add(failsAuth);
		authList.add(failsAuth);
		auth.setProjectAuthorizers(authList);
		assertNull(auth.userIsAuthorizedForProject(USERNAME, PROJECT, null));
	}

}

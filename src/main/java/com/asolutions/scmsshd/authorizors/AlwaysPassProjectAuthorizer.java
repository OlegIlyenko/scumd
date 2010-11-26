package com.asolutions.scmsshd.authorizors;

import com.asolutions.scmsshd.sshd.ProjectAuthorizer;
import com.asolutions.scmsshd.sshd.UnparsableProjectException;

public class AlwaysPassProjectAuthorizer implements ProjectAuthorizer {

	public AuthorizationLevel userIsAuthorizedForProject(String username,
			String project) throws UnparsableProjectException {
		return AuthorizationLevel.AUTH_LEVEL_READ_WRITE;
	}

}

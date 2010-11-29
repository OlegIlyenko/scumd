package com.asolutions.scmsshd.authorizors;

import com.asolutions.scmsshd.sshd.ProjectAuthorizer;
import com.asolutions.scmsshd.sshd.UnparsableProjectException;
import org.apache.sshd.server.session.ServerSession;

/**
 * @deprecated Not used any more
 */
@Deprecated
public class AlwaysPassProjectAuthorizer implements ProjectAuthorizer {

	public AuthorizationLevel userIsAuthorizedForProject(String username, String project, ServerSession session)
            throws UnparsableProjectException {
		return AuthorizationLevel.AUTH_LEVEL_READ_WRITE;
	}

}

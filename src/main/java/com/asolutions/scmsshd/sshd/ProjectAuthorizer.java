package com.asolutions.scmsshd.sshd;

import com.asolutions.scmsshd.authorizors.AuthorizationLevel;

public interface ProjectAuthorizer {

	AuthorizationLevel userIsAuthorizedForProject(String username, String project) throws UnparsableProjectException;

}

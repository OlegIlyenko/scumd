package com.asolutions.scmsshd.sshd;

import com.asolutions.scmsshd.authorizors.AuthorizationLevel;
import org.apache.sshd.server.session.ServerSession;

public interface ProjectAuthorizer {

	AuthorizationLevel userIsAuthorizedForProject(String username, String project, ServerSession session) throws UnparsableProjectException;

}

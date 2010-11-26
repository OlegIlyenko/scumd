package com.asolutions.scmsshd.authorizors;

import com.asolutions.scmsshd.sshd.ProjectAuthorizer;
import com.asolutions.scmsshd.sshd.UnparsableProjectException;

import java.util.ArrayList;

public class PassIfAnyInCollectionPassAuthorizor implements ProjectAuthorizer {

	private ArrayList<ProjectAuthorizer> authList = new ArrayList<ProjectAuthorizer>();

	public AuthorizationLevel userIsAuthorizedForProject(String username, String project)
			throws UnparsableProjectException {
		for (ProjectAuthorizer auth : authList) {
			AuthorizationLevel result = auth.userIsAuthorizedForProject(username, project);
			if (result != null){
				return result;
			}
		}
		return null;
	}

	public void setProjectAuthorizers(ArrayList authList) {
		this.authList = authList;
	}

}

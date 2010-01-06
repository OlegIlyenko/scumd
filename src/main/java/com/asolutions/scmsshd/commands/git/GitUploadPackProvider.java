package com.asolutions.scmsshd.commands.git;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.UploadPack;

public class GitUploadPackProvider {

	public UploadPack provide(Repository repository) {
		return new UploadPack(repository);
	}

}

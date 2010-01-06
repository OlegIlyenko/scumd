package com.asolutions.scmsshd.commands.git;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.ReceivePack;

public class GitReceivePackProvider {

	public ReceivePack provide(Repository repository) {
		return new ReceivePack(repository);
	}

}

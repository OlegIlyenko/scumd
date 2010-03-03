package com.asolutions.scmsshd.commands.factories;

import com.asolutions.scmsshd.commands.filters.git.GitBadCommandFilter;
import com.asolutions.scmsshd.commands.git.GitSCMRepositoryProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GitCommandFactory extends CommandFactoryBase {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    public GitCommandFactory() {
        this(null);
    }

    public GitCommandFactory(GitSCMRepositoryProvider repositoryProvider) {
		setBadCommandFilter(new GitBadCommandFilter());
		setScmCommandFactory(new GitSCMCommandFactory(repositoryProvider));
	}
}

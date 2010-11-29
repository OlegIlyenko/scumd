package com.asolutions.scmsshd.event.impl;

import com.asolutions.scmsshd.ConfigurableGitSshServer;
import com.asolutions.scmsshd.event.RepositoryEvent;
import com.asolutions.scmsshd.model.security.User;

/**
 * @author Oleg Ilyenko
 */
public class BaseServerRepositoryUserEvent extends BaseServerUserEvent implements RepositoryEvent {

    private final RepositoryInfo repositoryInfo;

    public BaseServerRepositoryUserEvent(User user, ConfigurableGitSshServer server, RepositoryInfo repositoryInfo) {
        super(user, server);
        this.repositoryInfo = repositoryInfo;
    }

    public RepositoryInfo getRepositoryInfo() {
        return repositoryInfo;
    }
}


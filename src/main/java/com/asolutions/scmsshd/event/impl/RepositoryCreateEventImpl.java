package com.asolutions.scmsshd.event.impl;

import com.asolutions.scmsshd.ConfigurableGitSshServer;
import com.asolutions.scmsshd.event.RepositoryCreateEvent;
import com.asolutions.scmsshd.model.security.User;

/**
 * @author Oleg Ilyenko
 */
public class RepositoryCreateEventImpl extends BaseServerRepositoryUserEvent implements RepositoryCreateEvent {
    public RepositoryCreateEventImpl(User user, ConfigurableGitSshServer server, RepositoryInfo repositoryInfo) {
        super(user, server, repositoryInfo);
    }
}

package com.asolutions.scmsshd.event.impl;

import com.asolutions.scmsshd.ConfigurableGitSshServer;
import com.asolutions.scmsshd.event.PullEvent;
import com.asolutions.scmsshd.model.security.User;

/**
 * @author Oleg Ilyenko
 */
public class PullEventImpl extends BaseServerRepositoryUserEvent implements PullEvent {
    public PullEventImpl(User user, ConfigurableGitSshServer server, RepositoryInfo repositoryInfo) {
        super(user, server, repositoryInfo);
    }
}

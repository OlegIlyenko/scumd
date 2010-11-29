package com.asolutions.scmsshd.event.impl;

import com.asolutions.scmsshd.ConfigurableGitSshServer;
import com.asolutions.scmsshd.event.FileChangeEvent;
import com.asolutions.scmsshd.model.security.User;

/**
 * @author Oleg Ilyenko
 */
public class FileChangeEventImpl extends BaseServerRepositoryUserEvent implements FileChangeEvent {

    private final String path;

    public FileChangeEventImpl(User user, ConfigurableGitSshServer server, RepositoryInfo repositoryInfo, String path) {
        super(user, server, repositoryInfo);
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}

package com.asolutions.scmsshd;

import com.asolutions.scmsshd.event.impl.RepositoryInfo;
import com.asolutions.scmsshd.event.listener.EventDispatcher;
import com.asolutions.scmsshd.model.security.User;

/**
 * @author Oleg Ilyenko
 */
public class InteractionContext {

    private ConfigurableGitSshServer server;

    private User user;

    private RepositoryInfo repositoryInfo;

    private EventDispatcher eventDispatcher;

    private int filesProEventLimit = -1;

    private boolean allowCaching;

    public InteractionContext(ConfigurableGitSshServer server, EventDispatcher eventDispatcher, int filesProEventLimit, boolean allowCaching) {
        this.server = server;
        this.eventDispatcher = eventDispatcher;
        this.filesProEventLimit = filesProEventLimit;
        this.allowCaching = allowCaching;
    }

    public ConfigurableGitSshServer getServer() {
        return server;
    }

    public void setServer(ConfigurableGitSshServer server) {
        this.server = server;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public RepositoryInfo getRepositoryInfo() {
        return repositoryInfo;
    }

    public void setRepositoryInfo(RepositoryInfo repositoryInfo) {
        this.repositoryInfo = repositoryInfo;
    }

    public EventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    public void setEventDispatcher(EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
    }

    public int getFilesProEventLimit() {
        return filesProEventLimit;
    }

    public void setFilesProEventLimit(int filesProEventLimit) {
        this.filesProEventLimit = filesProEventLimit;
    }

    public boolean isAllowCaching() {
        return allowCaching;
    }
}

package com.asolutions.scmsshd.event.listener.impl;

import com.asolutions.scmsshd.event.*;

/**
 * @author Oleg Ilyenko
 */
public enum CancelEventTypes {
    authentication(AuthenticationEvent.class),
    authorization(AuthorizationEvent.class),
    repoCreate(RepositoryCreateEvent.class),
    pull(PullEvent.class),
    push(PushEvent.class),
    commit(CommitEvent.class);

    private Class<? extends Event> clazz;

    private CancelEventTypes(Class<? extends Event> clazz) {
        this.clazz = clazz;
    }

    public Class<? extends Event> getClazz() {
        return clazz;
    }
}

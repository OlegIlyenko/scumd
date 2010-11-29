package com.asolutions.scmsshd.event;

import com.asolutions.scmsshd.event.impl.RepositoryInfo;

/**
 * @author Oleg Ilyenko
 */
public interface RepositoryEvent extends GitServerEvent {

    RepositoryInfo getRepositoryInfo();

}

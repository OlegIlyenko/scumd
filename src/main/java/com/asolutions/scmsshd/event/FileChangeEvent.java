package com.asolutions.scmsshd.event;

/**
 * @author Oleg Ilyenko
 */
public interface FileChangeEvent extends UserEvent, GitServerEvent, RepositoryEvent {

    String getPath();

}

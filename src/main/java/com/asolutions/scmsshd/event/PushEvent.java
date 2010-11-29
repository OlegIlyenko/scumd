package com.asolutions.scmsshd.event;

import org.eclipse.jgit.transport.ReceiveCommand;
import org.eclipse.jgit.transport.ReceivePack;

import java.util.List;

/**
 * @author Oleg Ilyenko
 */
public interface PushEvent extends UserEvent, GitServerEvent, RepositoryEvent {

    ReceivePack getReceivePack();

    List<CommitEvent> getCommits();

    ReceiveCommand getCommand();
}

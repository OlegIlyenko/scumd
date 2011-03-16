package com.asolutions.scmsshd.event;

import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.ReceivePack;

import java.util.List;

/**
 * @author Oleg Ilyenko
 */
public interface CommitEvent extends UserEvent, GitServerEvent, RepositoryEvent, RefEvent {

    RevCommit getRevCommit();

    ReceivePack getReceivePack();

    List<FileChangeEvent> getFileChanges();

    int getHasMoreFiles();
}

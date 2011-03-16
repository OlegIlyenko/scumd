package com.asolutions.scmsshd.event.impl;

import com.asolutions.scmsshd.ConfigurableGitSshServer;
import com.asolutions.scmsshd.event.CommitEvent;
import com.asolutions.scmsshd.event.FileChangeEvent;
import com.asolutions.scmsshd.event.RefEvent;
import com.asolutions.scmsshd.model.security.User;
import com.asolutions.scmsshd.util.GitUtil;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.ReceiveCommand;
import org.eclipse.jgit.transport.ReceivePack;

import java.util.List;

/**
 * @author Oleg Ilyenko
 */
public class CommitEventImpl extends BaseServerRepositoryUserEvent implements CommitEvent {

    private final RevCommit revCommit;

    private final ReceivePack receivePack;

    private final List<FileChangeEvent> fileChanges;

    private final int hasMoreFiles;

    private final String refName;

    private final Type type;

    public CommitEventImpl(User user, ConfigurableGitSshServer server, RepositoryInfo repositoryInfo, RevCommit revCommit,
                           ReceivePack receivePack, List<FileChangeEvent> fileChanges, int hasMoreFiles, ReceiveCommand command) {
        super(user, server, repositoryInfo);
        this.revCommit = revCommit;
        this.receivePack = receivePack;
        this.fileChanges = fileChanges;
        this.hasMoreFiles = hasMoreFiles;
        this.refName = command.getRefName();
        this.type = GitUtil.convert(command.getType());
    }

    public RevCommit getRevCommit() {
        return revCommit;
    }

    public ReceivePack getReceivePack() {
        return receivePack;
    }

    public List<FileChangeEvent> getFileChanges() {
        return fileChanges;
    }

    public int getHasMoreFiles() {
        return hasMoreFiles;
    }

    public String getRefName() {
        return refName;
    }

    public Type getType() {
        return type;
    }
}

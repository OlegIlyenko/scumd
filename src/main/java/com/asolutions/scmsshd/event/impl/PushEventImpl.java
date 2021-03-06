package com.asolutions.scmsshd.event.impl;

import com.asolutions.scmsshd.ConfigurableGitSshServer;
import com.asolutions.scmsshd.event.CommitEvent;
import com.asolutions.scmsshd.event.PushEvent;
import com.asolutions.scmsshd.event.RefEvent;
import com.asolutions.scmsshd.model.security.User;
import com.asolutions.scmsshd.util.GitUtil;
import org.eclipse.jgit.transport.ReceiveCommand;
import org.eclipse.jgit.transport.ReceivePack;

import java.util.List;

/**
 * @author Oleg Ilyenko
 */
public class PushEventImpl extends BaseServerRepositoryUserEvent implements PushEvent {

    private final ReceivePack receivePack;

    private final List<CommitEvent> commits;

    private final ReceiveCommand command;

    private final String refName;

    private final Type type;

    public PushEventImpl(User user, ConfigurableGitSshServer server, RepositoryInfo repositoryInfo, ReceivePack receivePack,
                         List<CommitEvent> commits, ReceiveCommand command) {
        super(user, server, repositoryInfo);
        this.receivePack = receivePack;
        this.commits = commits;
        this.command = command;
        this.refName = command.getRefName();
        this.type = GitUtil.convert(command.getType());
    }

    public ReceivePack getReceivePack() {
        return receivePack;
    }

    public List<CommitEvent> getCommits() {
        return commits;
    }

    public ReceiveCommand getCommand() {
        return command;
    }

    public String getRefName() {
        return refName;
    }

    public Type getType() {
        return type;
    }
}

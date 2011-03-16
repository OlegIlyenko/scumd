package com.asolutions.scmsshd.event.impl;

import com.asolutions.scmsshd.ConfigurableGitSshServer;
import com.asolutions.scmsshd.event.FileChangeEvent;
import com.asolutions.scmsshd.event.RefEvent;
import com.asolutions.scmsshd.model.security.User;
import com.asolutions.scmsshd.util.GitUtil;
import org.eclipse.jgit.transport.ReceiveCommand;

/**
 * @author Oleg Ilyenko
 */
public class FileChangeEventImpl extends BaseServerRepositoryUserEvent implements FileChangeEvent {

    private final String path;

    private final String refName;

    private final Type type;

    public FileChangeEventImpl(User user, ConfigurableGitSshServer server, RepositoryInfo repositoryInfo, String path, ReceiveCommand command) {
        super(user, server, repositoryInfo);
        this.path = path;
        this.refName = command.getRefName();
        this.type = GitUtil.convert(command.getType());
    }

    public String getPath() {
        return path;
    }

    public String getRefName() {
        return refName;
    }

    public Type getType() {
        return type;
    }
}

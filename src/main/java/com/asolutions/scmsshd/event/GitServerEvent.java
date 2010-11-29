package com.asolutions.scmsshd.event;

import com.asolutions.scmsshd.ConfigurableGitSshServer;

/**
 * @author Oleg Ilyenko
 */
public interface GitServerEvent extends Event {
    ConfigurableGitSshServer getServer();
}

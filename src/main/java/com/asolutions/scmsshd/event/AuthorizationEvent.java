package com.asolutions.scmsshd.event;

import com.asolutions.scmsshd.model.security.Privilege;

import java.net.SocketAddress;
import java.util.Set;

/**
 * @author Oleg Ilyenko
 */
public interface AuthorizationEvent extends UserEvent, GitServerEvent, RepositoryEvent {

    Set<Privilege> getUserPrivileges();

    SocketAddress getRemoteAddress();

}

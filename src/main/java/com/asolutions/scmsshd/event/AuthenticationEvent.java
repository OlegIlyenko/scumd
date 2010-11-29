package com.asolutions.scmsshd.event;

import java.net.SocketAddress;

/**
 * @author Oleg Ilyenko
 */
public interface AuthenticationEvent extends UserEvent, GitServerEvent {

    String getUserName();

    SocketAddress getRemoteAddress();

    String getMethod();

}

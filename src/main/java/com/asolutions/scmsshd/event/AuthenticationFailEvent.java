package com.asolutions.scmsshd.event;

/**
 * @author Oleg Ilyenko
 */
public interface AuthenticationFailEvent extends AuthenticationEvent {

    String getReason();
    
}

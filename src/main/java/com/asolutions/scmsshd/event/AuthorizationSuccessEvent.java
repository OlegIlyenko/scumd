package com.asolutions.scmsshd.event;

import com.asolutions.scmsshd.authorizors.AuthorizationLevel;

/**
 * @author Oleg Ilyenko
 */
public interface AuthorizationSuccessEvent extends AuthorizationEvent {

    AuthorizationLevel getAuthorizationLevel();

}

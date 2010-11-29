package com.asolutions.scmsshd.event;

import com.asolutions.scmsshd.model.security.User;

/**
 * @author Oleg Ilyenko
 */
public interface UserEvent extends GitServerEvent {
    User getUser();
}

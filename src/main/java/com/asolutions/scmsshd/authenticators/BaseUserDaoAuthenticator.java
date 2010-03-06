package com.asolutions.scmsshd.authenticators;

import com.asolutions.scmsshd.model.security.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @author Oleg Ilyenko
 */
public abstract class BaseUserDaoAuthenticator {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected boolean checkUser(User user, String userName) {
        if (user == null) {
            log.warn("Unknown user '" + userName + "' tried to login.");
            return false;
        }

        if (!user.isActive()) {
            log.warn("Inactive user '" + userName + "' tried to login.");
            return false;
        }

        if (user.getExpirationDate() != null && user.getExpirationDate().before(new Date())) {
            log.warn("Expired user '" + userName + "' tried to login.");
            return false;
        }

        if (user.getAuthPolicy() == null) {
            log.warn("User '" + userName + "' have no authentication policy defined!");
            return false;
        }

        return true;
    }

}

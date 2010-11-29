package com.asolutions.scmsshd.authenticators;

import com.asolutions.scmsshd.InteractionContext;
import com.asolutions.scmsshd.dao.UserDao;
import com.asolutions.scmsshd.model.security.User;
import com.asolutions.scmsshd.util.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @author Oleg Ilyenko
 */
public abstract class BaseUserDaoAuthenticator {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private UserDao userDao;

    private Function<InteractionContext> contextProvider;

    protected BaseUserDaoAuthenticator(UserDao userDao, Function<InteractionContext> contextProvider) {
        this.userDao = userDao;
        this.contextProvider = contextProvider;
    }

    public Function<InteractionContext> getContextProvider() {
        return contextProvider;
    }

    public UserDao getUserDao() {
        return userDao;
    }

    /**
     * @return Error string or null if everything is OK
     */
    protected String checkUser(User user, String userName) {
        if (user == null) {
            return "Unknown user '" + userName + "' tried to login.";
        }

        if (!user.isActive()) {
            return "Inactive user '" + userName + "' tried to login.";
        }

        if (user.getExpirationDate() != null && user.getExpirationDate().before(new Date())) {
            return "Expired user '" + userName + "' tried to login.";
        }

        if (user.getAuthPolicy() == null) {
            return "User '" + userName + "' have no authentication policy defined!";
        }

        return null;
    }

}

package com.asolutions.scmsshd.authenticators;

import com.asolutions.scmsshd.InteractionContext;
import com.asolutions.scmsshd.InteractionContextKey;
import com.asolutions.scmsshd.dao.UserDao;
import com.asolutions.scmsshd.event.CancelEventException;
import com.asolutions.scmsshd.event.impl.AuthenticationEventImpl;
import com.asolutions.scmsshd.event.impl.AuthenticationFailEventImpl;
import com.asolutions.scmsshd.event.impl.AuthenticationSuccessEventImpl;
import com.asolutions.scmsshd.model.security.User;
import com.asolutions.scmsshd.util.Function;
import org.apache.sshd.server.session.ServerSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.Date;

import static com.asolutions.scmsshd.event.listener.EventDispatcher.Stage.Pre;

/**
 * @author Oleg Ilyenko
 */
public abstract class BaseUserDaoAuthenticator {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private final UserDao userDao;

    private final Function<InteractionContext> contextProvider;

    private final boolean allowAnonymous ;

    protected BaseUserDaoAuthenticator(UserDao userDao, Function<InteractionContext> contextProvider, boolean allowAnonymous) {
        this.userDao = userDao;
        this.contextProvider = contextProvider;
        this.allowAnonymous = allowAnonymous;
    }

    public boolean isAllowAnonymous() {
        return allowAnonymous;
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

    protected boolean firePreAuth(String userName, User user, InteractionContext ctx, SocketAddress address, String method) {
        try {
            ctx.getEventDispatcher().fireEvent(Pre, new AuthenticationEventImpl(user, ctx.getServer(), userName, address, method));
        } catch (CancelEventException e) {
            log.info("Authentication was cancelled by listener: " + e.getContextInfo());
            return true;
        }

        return false;
    }

    protected boolean processNoAuth(String userName, User user, InteractionContext ctx, SocketAddress address, ServerSession session) {
        ctx.setUser(user);
        session.setAttribute(InteractionContextKey.get(), ctx);

        log.debug("User '" + userName + "' passed authentication because he has NoAuth policy!");
        ctx.getEventDispatcher().fireEvent(new AuthenticationSuccessEventImpl(user, ctx.getServer(), userName, address, "NoAuth"));

        return true;
    }

    protected void processAuthResult(String userName, User user, InteractionContext ctx, SocketAddress address, boolean success, String method) {
        if (log.isDebugEnabled()) {
            if (success) {
                log.debug("User '" + userName + "' was successfully authenticated with " + method);
                ctx.getEventDispatcher().fireEvent(new AuthenticationSuccessEventImpl(user, ctx.getServer(), userName, address, method));
            } else {
                String authError = "User '" + userName + "' failed authentication with " + method + "!";
                log.warn(authError);
                ctx.getEventDispatcher().fireEvent(new AuthenticationFailEventImpl(user, ctx.getServer(), userName, address, method, authError));
            }
        }
    }
}

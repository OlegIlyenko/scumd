package com.asolutions.scmsshd.authenticators;

import com.asolutions.scmsshd.InteractionContext;
import com.asolutions.scmsshd.InteractionContextKey;
import com.asolutions.scmsshd.dao.UserDao;
import com.asolutions.scmsshd.event.CancelEventException;
import com.asolutions.scmsshd.event.impl.AuthenticationEventImpl;
import com.asolutions.scmsshd.event.impl.AuthenticationFailEventImpl;
import com.asolutions.scmsshd.event.impl.AuthenticationSuccessEventImpl;
import com.asolutions.scmsshd.model.security.PublicKeyAuthPolicy;
import com.asolutions.scmsshd.model.security.User;
import com.asolutions.scmsshd.util.Function;
import org.apache.sshd.server.PublickeyAuthenticator;
import org.apache.sshd.server.session.ServerSession;

import java.net.SocketAddress;
import java.security.PublicKey;

import static com.asolutions.scmsshd.event.listener.EventDispatcher.Stage.Pre;

/**
 * @author Oleg Ilyenko
 */
public class UserDaoPublickeyAuthenticator extends BaseUserDaoAuthenticator implements PublickeyAuthenticator {

    public UserDaoPublickeyAuthenticator(UserDao userDao, Function<InteractionContext> contextProvider) {
        super(userDao, contextProvider);
    }

    @Override
    public boolean authenticate(String userName, PublicKey key, ServerSession session) {
        User user = getUserDao().getUserByName(userName);
        InteractionContext ctx = getContextProvider().apply();
        SocketAddress address = session.getIoSession().getRemoteAddress();

        try {
            ctx.getEventDispatcher().fireEvent(Pre, new AuthenticationEventImpl(user, ctx.getServer(), userName, address, "public key"));
        } catch (CancelEventException e) {
            log.info("Authentication was cancelled by listener: " + e.getContextInfo());
            return false;
        }

        String authError = checkUser(user, userName);

        if (authError != null) {
            log.warn(authError);
            ctx.getEventDispatcher().fireEvent(new AuthenticationFailEventImpl(user, ctx.getServer(), userName, address, "public key", authError));
            return false;
        }

        if (user.getAuthPolicy() instanceof PublicKeyAuthPolicy) {
            PublicKeyAuthPolicy policy = (PublicKeyAuthPolicy) user.getAuthPolicy();
            PublicKey userKey = policy.getPublicKey();
            boolean success = key.equals(userKey);

            if (log.isDebugEnabled()) {
                if (success) {
                    log.debug("User '" + userName + "' was successfully authenticated with public key");
                    ctx.getEventDispatcher().fireEvent(new AuthenticationSuccessEventImpl(user, ctx.getServer(), userName, address, "public key"));
                } else {
                    authError = "User '" + userName + "' failed authentication with public key!";
                    log.warn(authError);
                    ctx.getEventDispatcher().fireEvent(new AuthenticationFailEventImpl(user, ctx.getServer(), userName, address, "public key", authError));
                }
            }

            ctx.setUser(user);

            session.setAttribute(InteractionContextKey.get(), ctx);

            return success;
        } else {
            return false;
        }
    }
}

package com.asolutions.scmsshd.authenticators;

import com.asolutions.scmsshd.InteractionContext;
import com.asolutions.scmsshd.InteractionContextKey;
import com.asolutions.scmsshd.dao.UserDao;
import com.asolutions.scmsshd.event.CancelEventException;
import com.asolutions.scmsshd.event.impl.AuthenticationEventImpl;
import com.asolutions.scmsshd.event.impl.AuthenticationFailEventImpl;
import com.asolutions.scmsshd.event.impl.AuthenticationSuccessEventImpl;
import com.asolutions.scmsshd.model.security.PasswordAuthPolicy;
import com.asolutions.scmsshd.model.security.User;
import com.asolutions.scmsshd.util.CryptoUtil;
import com.asolutions.scmsshd.util.Function;
import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.session.ServerSession;

import java.net.SocketAddress;

import static com.asolutions.scmsshd.event.listener.EventDispatcher.Stage.Pre;

/**
 * @author Oleg Ilyenko
 */
public class UserDaoPasswordAuthenticator extends BaseUserDaoAuthenticator implements PasswordAuthenticator {

    public UserDaoPasswordAuthenticator(UserDao userDao, Function<InteractionContext> contextProvider) {
        super(userDao, contextProvider);
    }

    @Override
    public boolean authenticate(String userName, String password, ServerSession session) {
        User user = getUserDao().getUserByName(userName);
        InteractionContext ctx = getContextProvider().apply();
        SocketAddress address = session.getIoSession().getRemoteAddress();

        try {
            ctx.getEventDispatcher().fireEvent(Pre, new AuthenticationEventImpl(user, ctx.getServer(), userName, address, "password"));
        } catch (CancelEventException e) {
            log.info("Authentication was cancelled by listener: " + e.getContextInfo());
            return false;
        }

        String authError = checkUser(user, userName);

        if (authError != null) {
            log.warn(authError);
            ctx.getEventDispatcher().fireEvent(new AuthenticationFailEventImpl(user, ctx.getServer(), userName, address, "password", authError));
            return false;
        }

        if (user.getAuthPolicy() instanceof PasswordAuthPolicy) {
            PasswordAuthPolicy policy = (PasswordAuthPolicy) user.getAuthPolicy();
            String passwordChecksum = password;

            if (policy.getEncodingAlgorithm() != PasswordAuthPolicy.EncodingAlgorithm.none) {
                passwordChecksum = CryptoUtil.calculateChecksum(password, policy.getEncodingAlgorithm().getAlgorithm());
            }

            boolean success = policy.getPassword().equals(passwordChecksum);

            if (log.isDebugEnabled()) {
                if (success) {
                    log.debug("User '" + userName + "' was successfully authenticated with password");
                    ctx.getEventDispatcher().fireEvent(new AuthenticationSuccessEventImpl(user, ctx.getServer(), userName, address, "password"));
                } else {
                    authError = "User '" + userName + "' failed authentication with password!";
                    log.warn(authError);
                    ctx.getEventDispatcher().fireEvent(new AuthenticationFailEventImpl(user, ctx.getServer(), userName, address, "password", authError));
                }
            }

            ctx.setUser(user);

            session.setAttribute(InteractionContextKey.get(), ctx);

            return success;
        } else {
            return false; /* In this case public key strategy would be used */
        }
    }
}

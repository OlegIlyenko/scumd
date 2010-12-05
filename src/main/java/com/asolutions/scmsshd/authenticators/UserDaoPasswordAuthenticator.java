package com.asolutions.scmsshd.authenticators;

import com.asolutions.scmsshd.InteractionContext;
import com.asolutions.scmsshd.InteractionContextKey;
import com.asolutions.scmsshd.dao.UserDao;
import com.asolutions.scmsshd.event.impl.AuthenticationFailEventImpl;
import com.asolutions.scmsshd.model.security.Anonymous;
import com.asolutions.scmsshd.model.security.NoAuth;
import com.asolutions.scmsshd.model.security.PasswordAuthPolicy;
import com.asolutions.scmsshd.model.security.User;
import com.asolutions.scmsshd.util.CryptoUtil;
import com.asolutions.scmsshd.util.Function;
import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.session.ServerSession;

import java.net.SocketAddress;

/**
 * @author Oleg Ilyenko
 */
public class UserDaoPasswordAuthenticator extends BaseUserDaoAuthenticator implements PasswordAuthenticator {

    public UserDaoPasswordAuthenticator(UserDao userDao, Function<InteractionContext> contextProvider, boolean allowAnonymous) {
        super(userDao, contextProvider, allowAnonymous);
    }

    @Override
    public boolean authenticate(String userName, String password, ServerSession session) {
        User user = getUserDao().getUserByName(userName);
        InteractionContext ctx = getContextProvider().apply();
        SocketAddress address = session.getIoSession().getRemoteAddress();

        if (firePreAuth(userName, user, ctx, address, "password")) return false;

        String authError = checkUser(user, userName);

        if (authError != null) {
            if (user == null && isAllowAnonymous()) {
                user = new Anonymous();
            } else {
                log.warn(authError);
                ctx.getEventDispatcher().fireEvent(new AuthenticationFailEventImpl(user, ctx.getServer(), userName, address, "password", authError));
                return false;
            }
        }

        if (user.getAuthPolicy() instanceof PasswordAuthPolicy) {
            PasswordAuthPolicy policy = (PasswordAuthPolicy) user.getAuthPolicy();
            String passwordChecksum = password;

            if (policy.getEncodingAlgorithm() != PasswordAuthPolicy.EncodingAlgorithm.none) {
                passwordChecksum = CryptoUtil.calculateChecksum(password, policy.getEncodingAlgorithm().getAlgorithm());
            }

            boolean success = policy.getPassword().equals(passwordChecksum);

            processAuthResult(userName, user, ctx, address, success, "password");

            ctx.setUser(user);
            session.setAttribute(InteractionContextKey.get(), ctx);

            return success;
        } else if (user.getAuthPolicy() instanceof NoAuth) {
            return processNoAuth(userName, user, ctx, address, session);
        } else {
            return false;
        }
    }
}

package com.asolutions.scmsshd.authenticators;

import com.asolutions.scmsshd.InteractionContext;
import com.asolutions.scmsshd.InteractionContextKey;
import com.asolutions.scmsshd.dao.UserDao;
import com.asolutions.scmsshd.event.impl.AuthenticationFailEventImpl;
import com.asolutions.scmsshd.model.security.Anonymous;
import com.asolutions.scmsshd.model.security.NoAuth;
import com.asolutions.scmsshd.model.security.PublicKeyAuthPolicy;
import com.asolutions.scmsshd.model.security.User;
import com.asolutions.scmsshd.util.Function;
import org.apache.sshd.server.PublickeyAuthenticator;
import org.apache.sshd.server.session.ServerSession;

import java.net.SocketAddress;
import java.security.PublicKey;
import java.util.List;

/**
 * @author Oleg Ilyenko
 */
public class UserDaoPublickeyAuthenticator extends BaseUserDaoAuthenticator implements PublickeyAuthenticator {

    public UserDaoPublickeyAuthenticator(UserDao userDao, Function<InteractionContext> contextProvider, boolean allowAnonymous) {
        super(userDao, contextProvider, allowAnonymous);
    }

    @Override
    public boolean authenticate(String userName, PublicKey key, ServerSession session) {
        User user = getUserDao().getUserByName(userName);
        InteractionContext ctx = getContextProvider().apply();
        SocketAddress address = session.getIoSession().getRemoteAddress();

        if (firePreAuth(userName, user, ctx, address, "public key")) return false;

        String authError = checkUser(user, userName);

        if (authError != null) {
            if (user == null && isAllowAnonymous()) {
                user = new Anonymous();
            } else {
                log.warn(authError);
                ctx.getEventDispatcher().fireEvent(new AuthenticationFailEventImpl(user, ctx.getServer(), userName, address, "public key", authError));
                return false;
            }
        }

        if (user.getAuthPolicy() instanceof PublicKeyAuthPolicy) {
            PublicKeyAuthPolicy policy = (PublicKeyAuthPolicy) user.getAuthPolicy();
            List<PublicKey> userKeys = policy.getPublicKeys();
            boolean success = false;

            if (userKeys != null) {
                for (PublicKey userKey : userKeys) {
                    if (key.equals(userKey)) {
                        success = true;
                        break;
                    }
                }
            }

            processAuthResult(userName, user, ctx, address, success, "public key");

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

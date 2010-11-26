package com.asolutions.scmsshd.authenticators;

import com.asolutions.scmsshd.dao.UserDao;
import com.asolutions.scmsshd.model.security.PublicKeyAuthPolicy;
import com.asolutions.scmsshd.model.security.User;
import org.apache.sshd.server.PublickeyAuthenticator;
import org.apache.sshd.server.session.ServerSession;

import java.security.PublicKey;

/**
 * @author Oleg Ilyenko
 */
public class UserDaoPublickeyAuthenticator extends BaseUserDaoAuthenticator implements PublickeyAuthenticator {

    private UserDao userDao;

    public UserDaoPublickeyAuthenticator(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public boolean authenticate(String userName, PublicKey key, ServerSession session) {
        User user = userDao.getUserByName(userName);

        if (!checkUser(user, userName)) {
            return false;
        }

        if (user.getAuthPolicy() instanceof PublicKeyAuthPolicy) {
            PublicKeyAuthPolicy policy = (PublicKeyAuthPolicy) user.getAuthPolicy();
            PublicKey userKey = policy.getPublicKey();
            boolean success = key.equals(userKey);

            if (log.isDebugEnabled()) {
                if (success) {
                    log.debug("User '" + userName + "' was successfully authenticated with public key");
                } else {
                    log.warn("User '" + userName + "' failed authentication with public key!");
                }
            }

            return success;
        } else {
            return false;
        }
    }
}

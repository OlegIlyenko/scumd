package com.asolutions.scmsshd.authenticators;

import com.asolutions.scmsshd.dao.UserDao;
import com.asolutions.scmsshd.model.security.PasswordAuthPolicy;
import com.asolutions.scmsshd.model.security.User;
import com.asolutions.scmsshd.util.CryptoUtil;
import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.session.ServerSession;

/**
 * @author Oleg Ilyenko
 */
public class UserDaoPasswordAuthenticator extends BaseUserDaoAuthenticator implements PasswordAuthenticator {

    private UserDao userDao;

    public UserDaoPasswordAuthenticator(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public boolean authenticate(String userName, String password, ServerSession session) {
        User user = userDao.getUserByName(userName);

        if (!checkUser(user, userName)) {
            return false;
        }

        if (user.getAuthPolicy() instanceof PasswordAuthPolicy) {
            PasswordAuthPolicy policy = (PasswordAuthPolicy) user.getAuthPolicy();
            String passwordChecksum = password;

            if (policy.getEncodingAlgorithm() != PasswordAuthPolicy.EncodingAlgorithm.none) {
                passwordChecksum = CryptoUtil.calculateChecksum(password, policy.getEncodingAlgorithm().getAlgorithm());
            }

            return policy.getPassword().equals(passwordChecksum);
        } else {
            return false;
        }
    }
}

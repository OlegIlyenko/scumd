package com.asolutions.scmsshd.dao.impl;

import com.asolutions.scmsshd.dao.UserDao;
import com.asolutions.scmsshd.model.security.*;
import com.asolutions.scmsshd.util.StringUtil;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @author Oleg Ilyenko
 */
public class DatabaseUserDao extends BaseDatabaseDao implements UserDao {

    @Override
    protected void createTables() {
        execute(sqlSource.getProperty("table.passwordHashAlgorithms.create"));

        if (jdbcTemplate.queryForInt(sqlSource.getProperty("table.passwordHashAlgorithms.check"), new HashMap<String, Object>()) == 0) {
            jdbcTemplate.update(sqlSource.getProperty("table.passwordHashAlgorithms.init"), new HashMap<String, Object>());
        }

        execute(sqlSource.getProperty("table.users.create"));
        execute(sqlSource.getProperty("table.groups.create"));
        execute(sqlSource.getProperty("table.userGroups.create"));
        execute(sqlSource.getProperty("table.publicKeys.create"));
    }

    @Override
    public List<User> getUsers() {
        UserHandler userHandler = new UserHandler();
        jdbcTemplate.query(sqlSource.getProperty("table.users.list"), new HashMap<String, Object>(), userHandler);
        return userHandler.getUsers(); 
    }

    @Override
    public User getUserByName(String name) {
        UserHandler userHandler = new UserHandler();

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userName", name);

        jdbcTemplate.query(sqlSource.getProperty("table.users.byName"), params, userHandler);

        List<User> users = userHandler.getUsers();
        return users.size() != 0 ? users.get(0) : null;
    }

    @Override
    public List<Group> getGroups() {
        return jdbcTemplate.query(sqlSource.getProperty("table.groups.list"), new HashMap<String, Object>(), new GroupMapper());
    }

    @Override
    public Group getGroupByName(String name) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("groupName", name);

        List<Group> groups = jdbcTemplate.query(sqlSource.getProperty("table.groups.byName"), params, new GroupMapper());

        if (groups.size() == 0) {
            return null;
        } else {
            return groups.get(0);
        }
    }

    private static class UserHandler implements RowCallbackHandler {
        private Map<String, User> users = new LinkedHashMap<String, User>();

        public List<User> getUsers() {
            return new ArrayList<User>(users.values());
        }

        public void processRow(ResultSet rs) throws SQLException {
            String name = rs.getString("name");
            User user = null;

            if (users.containsKey(name)) {
                user = users.get(name);
            } else {
                user = readUser(rs);
            }

            processGroup(rs, user);
            processPublicKey(rs, user);

            users.put(name, user);
        }

        private void processGroup(ResultSet rs, User user) throws SQLException {
            String groupName = rs.getString("groupName");
            if (StringUtil.hasText(groupName)) {
                List<Group> userGroups = user.getGroups();

                if (userGroups == null) {
                    userGroups = new ArrayList<Group>();
                    user.setGroups(userGroups);
                }

                Group group = new Group();
                group.setName(groupName);
                userGroups.add(group);
            }
        }

        private void processPublicKey(ResultSet rs, User user) throws SQLException {
            String publicKey = rs.getString("publicKey");
            if (StringUtil.hasText(publicKey)) {
                AuthPolicy ap = user.getAuthPolicy();

                if (ap == null) {
                    PublicKeyAuthPolicy policy = new PublicKeyAuthPolicy();
                    policy.setPublicKeyAsStrings(new ArrayList<String>());
                    user.setAuthPolicy(policy);
                    ap = policy;
                } else if (!(ap instanceof PublicKeyAuthPolicy)) {
                    throw new IllegalStateException("You should have either password or public key or no-auth auth policy for user " + user.getName());
                }


                ((PublicKeyAuthPolicy) ap).getPublicKeyAsStrings().add(publicKey);
            }
        }

        private User readUser(ResultSet rs) throws SQLException {
            User user = new User();

            user.setName(rs.getString("name"));
            user.setEmail(rs.getString("email"));
            user.setActive(Boolean.valueOf(rs.getString("active")));
            user.setExpirationDate(rs.getDate("expire"));

            String noAuth = rs.getString("no_auth");
            if (StringUtil.hasText(noAuth) && Boolean.parseBoolean(noAuth)) {
                if (user.getAuthPolicy() != null) {
                    throw new IllegalStateException("You should have either password or public key or no-auth auth policy for user " + user.getName());
                }

                user.setAuthPolicy(NoAuth.get());
            }

            String password = rs.getString("password");
            if (StringUtil.hasText(password)) {
                if (user.getAuthPolicy() != null) {
                    throw new IllegalStateException("You should have either password or public key or no-auth auth policy for user " + user.getName());
                }

                PasswordAuthPolicy policy = new PasswordAuthPolicy();
                policy.setPassword(password);

                String hashAlg = rs.getString("password_hash_alg");
                if (StringUtil.hasText(hashAlg)) {
                    policy.setEncodingAlgorithm(PasswordAuthPolicy.EncodingAlgorithm.valueOf(hashAlg));
                }

                user.setAuthPolicy(policy);
            }

            return user;
        }
    }

    private static class GroupMapper implements RowMapper<Group> {
        public Group mapRow(ResultSet rs, int rowNum) throws SQLException {
            Group group = new Group();
            group.setName(rs.getString("name"));
            return group;
        }
    }
}

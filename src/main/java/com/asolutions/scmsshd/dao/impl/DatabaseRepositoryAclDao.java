package com.asolutions.scmsshd.dao.impl;

import com.asolutions.scmsshd.dao.RepositoryAclDao;
import com.asolutions.scmsshd.dao.UserDao;
import com.asolutions.scmsshd.model.security.*;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @author Oleg Ilyenko
 */
public class DatabaseRepositoryAclDao extends BaseDatabaseDao implements RepositoryAclDao {

    private UserDao userDao;

    @Override
    public UserDao getUserDao() {
        return userDao;
    }

    @Override
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    protected void createTables() {
        execute(sqlSource.getProperty("table.privileges.create"));

        if (jdbcTemplate.queryForInt(sqlSource.getProperty("table.privileges.check"), new HashMap<String, Object>()) == 0) {
            jdbcTemplate.update(sqlSource.getProperty("table.privileges.init"), new HashMap<String, Object>());
        }

        execute(sqlSource.getProperty("table.repository.create"));
        execute(sqlSource.getProperty("table.userAcl.create"));
        execute(sqlSource.getProperty("table.groupAcl.create"));
    }

    @Override
    public List<RepositoryAcl> getRepositoryAcl() {
        RepositoryAclHandler handler = new RepositoryAclHandler(userDao.getUsers(), userDao.getGroups());

        jdbcTemplate.query(sqlSource.getProperty("table.repository.list"), new HashMap<String, Object>(), handler);

        return handler.getRepositoryAcl();
    }

    private static class RepositoryAclHandler implements RowCallbackHandler {
        private final Map<String, User> availableUsers;
        private final Map<String, Group> availableGroups;
        private final Map<String, RepositoryAcl> acl = new LinkedHashMap<String, RepositoryAcl>();

        private RepositoryAclHandler(List<User> availableUsers, List<Group> availableGroups) {
            this.availableUsers = new HashMap<String, User>();
            this.availableGroups = new HashMap<String, Group>();

            for (User u : availableUsers) {
                this.availableUsers.put(u.getName(), u);
            }

            for (Group g : availableGroups) {
                this.availableGroups.put(g.getName(), g);
            }
        }

        public List<RepositoryAcl> getRepositoryAcl() {
            return new ArrayList<RepositoryAcl>(acl.values());
        }

        public void processRow(ResultSet rs) throws SQLException {
            System.out.println(rs.getString("repoName") + ", " + rs.getString("type") + ", " + rs.getString("name"));

            String repoName = rs.getString("repoName");
            RepositoryAcl repositoryAcl = acl.get(repoName);

            if (repositoryAcl == null) {
                repositoryAcl = readRepo(rs);
                acl.put(repoName, repositoryAcl);
            }

            String type = rs.getString("type");

            if (type.equals("user")) {
                addUser(repositoryAcl, rs);
            } else if (type.equals("group")) {
                addGroup(repositoryAcl, rs);
            }
        }

        private void addGroup(RepositoryAcl repositoryAcl, ResultSet rs) throws SQLException {
            String groupName = rs.getString("name");
            Group group = availableGroups.get(groupName);
            Privilege privilege = Privilege.valueOf(rs.getString("privilege"));
            PrivilegeOwner<Group> privilegeOwner = new PrivilegeOwner<Group>(group, privilege);

            repositoryAcl.getGroups().add(privilegeOwner);
        }

        private void addUser(RepositoryAcl repositoryAcl, ResultSet rs) throws SQLException {
            String userName = rs.getString("name");
            User user = availableUsers.get(userName);
            Privilege privilege = Privilege.valueOf(rs.getString("privilege"));
            PrivilegeOwner<User> privilegeOwner = new PrivilegeOwner<User>(user, privilege);

            repositoryAcl.getUsers().add(privilegeOwner);
        }

        private RepositoryAcl readRepo(ResultSet rs) throws SQLException {
            RepositoryAcl repositoryAcl = new RepositoryAcl();

            repositoryAcl.setMatcher(new SimpleRepositoryMatcher(rs.getString("repoPath")));
            repositoryAcl.setGroups(new ArrayList<PrivilegeOwner<Group>>());
            repositoryAcl.setUsers(new ArrayList<PrivilegeOwner<User>>());

            return repositoryAcl;
        }
    }
}

package com.asolutions.scmsshd.dao.impl;

import com.asolutions.scmsshd.dao.RepositoryAclDao;
import com.asolutions.scmsshd.dao.UserDao;
import com.asolutions.scmsshd.model.security.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Oleg Ilyenko
 */
public class SimpleRepositoryAclDao implements RepositoryAclDao {

    private UserDao userDao;

    private List<RawRepositoryAcl> rawRepositoryAcl;

    public SimpleRepositoryAclDao(List<RawRepositoryAcl> rawRepositoryAcl) {
        this.rawRepositoryAcl = rawRepositoryAcl;
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public List<RepositoryAcl> getRepositoryAcl() {
        return getRepositoryAcl(rawRepositoryAcl);
    }

    private List<RepositoryAcl> getRepositoryAcl(List<RawRepositoryAcl> rawAcls) {
        List<User> availableUsers = userDao.getUsers();
        List<Group> availableGroups = userDao.getGroups();
        List<RepositoryAcl> acls = new ArrayList<RepositoryAcl>();

        for (RawRepositoryAcl r : rawAcls) {
            RepositoryAcl acl = new RepositoryAcl();
            List<PrivilegeOwner<User>> users = new ArrayList<PrivilegeOwner<User>>();
            List<PrivilegeOwner<Group>> groups = new ArrayList<PrivilegeOwner<Group>>();

            acl.setMatcher(new SimpleRepositoryMatcher(r.getPath()));
            acl.setUsers(users);
            acl.setGroups(groups);
            acls.add(acl);

            for (Privilege p : r.getUserPrivileges().keySet()) {
                for (String u : r.getUserPrivileges().get(p)) {
                    User user = getUserByName(availableUsers, u);
                    if (user == null) {
                        throw new IllegalStateException("Unknown user: " + u);
                    }

                    users.add(new PrivilegeOwner<User>(user, p));
                }
            }

            for (Privilege p : r.getGroupPrivileges().keySet()) {
                for (String g : r.getGroupPrivileges().get(p)) {
                    Group group = getGroupByName(availableGroups, g);
                    if (group == null) {
                        throw new IllegalStateException("Unknown group: " + g);
                    }

                    groups.add(new PrivilegeOwner<Group>(group, p));
                }
            }
        }

        return acls;
    }

    private User getUserByName(List<User> users, String name) {
        for (User u : users) {
            if (u.getName().equals(name)) {
                return u;
            }
        }

        return null;
    }

    private Group getGroupByName(List<Group> groups, String name) {
        for (Group g : groups) {
            if (g.getName().equals(name)) {
                return g;
            }
        }

        return null;
    }
}

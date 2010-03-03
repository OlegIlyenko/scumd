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

    private final List<RepositoryAcl> repositoryAcl;

    private final UserDao userDao;

    public SimpleRepositoryAclDao(List<RawRepositoryAcl> rawRepositoryAcl, UserDao userDao) {
        this.userDao = userDao;
        this.repositoryAcl = createRepositoryAcl(rawRepositoryAcl);
    }

    @Override
    public List<RepositoryAcl> getRepositoryAcl() {
        return repositoryAcl;
    }

    private List<RepositoryAcl> createRepositoryAcl(List<RawRepositoryAcl> rawAcls) {
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
                    User user = userDao.getUserByName(u);
                    if (user == null) {
                        throw new IllegalStateException("Unknown user: " + u);
                    }

                    users.add(new PrivilegeOwner<User>(user, p));
                }
            }

            for (Privilege p : r.getGroupPrivileges().keySet()) {
                for (String g : r.getGroupPrivileges().get(p)) {
                    Group group = userDao.getGroupByName(g);
                    if (group == null) {
                        throw new IllegalStateException("Unknown group: " + g);
                    }

                    groups.add(new PrivilegeOwner<Group>(group, p));
                }
            }
        }
        
        return acls;
    }
}

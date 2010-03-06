package com.asolutions.scmsshd.service.impl;

import com.asolutions.scmsshd.dao.RepositoryAclDao;
import com.asolutions.scmsshd.model.security.*;
import com.asolutions.scmsshd.service.RepositoryAclService;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Oleg Ilyenko
 */
public class SimpleRepositoryAclService implements RepositoryAclService {

    private final RepositoryAclDao repositoryAclDao;

    public SimpleRepositoryAclService(RepositoryAclDao repositoryAclDao) {
        this.repositoryAclDao = repositoryAclDao;
    }

    public Set<Privilege> getAvailablePrivileges(String project, User user) {
        Set<Privilege> available = new HashSet<Privilege>();

        for (RepositoryAcl acl : repositoryAclDao.getRepositoryAcl()) {
            if (acl.getMatcher().matches(project)) {
                available.addAll(findPrivileges(user, acl));
            }
        }
        
        return available;
    }

    private Set<Privilege> findPrivileges(User user, RepositoryAcl acl) {
        Set<Privilege> available = new HashSet<Privilege>();

        for (PrivilegeOwner<User> userOwner : acl.getUsers()) {
            if (userOwner.getOwner().getName().equals(user.getName())) {
                available.add(userOwner.getPrivilege());
            }
        }

        for (PrivilegeOwner<Group> groupOwner : acl.getGroups()) {
            if (hasGroup(user, groupOwner.getOwner())) {
                available.add(groupOwner.getPrivilege());
            }
        }

        return available;
    }

    private boolean hasGroup(User user, Group group) {
        for (Group g : user.getGroups()) {
            if (g.getName().equals(group.getName())) {
                return true;
            }
        }

        return false;
    }

}

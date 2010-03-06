package com.asolutions.scmsshd.model.security;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Oleg Ilyenko
 */
public class RepositoryAcl {

    private RepositoryMatcher matcher;

    private List<PrivilegeOwner<User>> users = new ArrayList<PrivilegeOwner<User>>();

    private List<PrivilegeOwner<Group>> groups = new ArrayList<PrivilegeOwner<Group>>();

    public RepositoryMatcher getMatcher() {
        return matcher;
    }

    public void setMatcher(RepositoryMatcher matcher) {
        this.matcher = matcher;
    }

    public List<PrivilegeOwner<User>> getUsers() {
        return users;
    }

    public void setUsers(List<PrivilegeOwner<User>> users) {
        this.users = users;
    }

    public List<PrivilegeOwner<Group>> getGroups() {
        return groups;
    }

    public void setGroups(List<PrivilegeOwner<Group>> groups) {
        this.groups = groups;
    }

}
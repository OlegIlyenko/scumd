package com.asolutions.scmsshd.model.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Oleg Ilyenko
 */
public class RepositoryAcl {

    private PathMatcher matcher;

    private List<PrivilegeOwner<User>> users = new ArrayList<PrivilegeOwner<User>>();

    private List<PrivilegeOwner<Group>> groups = new ArrayList<PrivilegeOwner<Group>>();

    private Set<Privilege> publicPrivileges;

    public PathMatcher getMatcher() {
        return matcher;
    }

    public void setMatcher(PathMatcher matcher) {
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

    public Set<Privilege> getPublicPrivileges() {
        return publicPrivileges;
    }

    public void setPublicPrivileges(Set<Privilege> publicPrivileges) {
        this.publicPrivileges = publicPrivileges;
    }
}

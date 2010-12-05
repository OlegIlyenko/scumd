package com.asolutions.scmsshd.model.security;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Oleg Ilyenko
 */
public class RawRepositoryAcl {

    private String path;

    private Map<Privilege, List<String>> userPrivileges;

    private Map<Privilege, List<String>> groupPrivileges;

    private Set<Privilege> publicPrivileges;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<Privilege, List<String>> getUserPrivileges() {
        return userPrivileges;
    }

    public void setUserPrivileges(Map<Privilege, List<String>> userPrivileges) {
        this.userPrivileges = userPrivileges;
    }

    public Map<Privilege, List<String>> getGroupPrivileges() {
        return groupPrivileges;
    }

    public void setGroupPrivileges(Map<Privilege, List<String>> groupPrivileges) {
        this.groupPrivileges = groupPrivileges;
    }

    public Set<Privilege> getPublicPrivileges() {
        return publicPrivileges;
    }

    public void setPublicPrivileges(Set<Privilege> publicPrivileges) {
        this.publicPrivileges = publicPrivileges;
    }
}

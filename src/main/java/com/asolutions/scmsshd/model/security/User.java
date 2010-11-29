package com.asolutions.scmsshd.model.security;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Oleg Ilyenko
 */
public class User {

    private String name;

    private String email;

    private AuthPolicy authPolicy;

    private List<Group> groups = new ArrayList<Group>();

    private Date expirationDate;

    private Boolean active = true;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public AuthPolicy getAuthPolicy() {
        return authPolicy;
    }

    public void setAuthPolicy(AuthPolicy authPolicy) {
        this.authPolicy = authPolicy;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return name + (email != null ? " <" + email + ">" : "");
    }
}

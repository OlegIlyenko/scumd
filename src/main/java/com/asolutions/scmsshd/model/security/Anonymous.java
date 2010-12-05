package com.asolutions.scmsshd.model.security;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author Oleg Ilyenko
 */
public final class Anonymous extends User {

    @Override
    public String getName() {
        return "anonymous";
    }

    @Override
    public void setName(String name) {
        throw new IllegalStateException("Operation is not supported for anonymous user");
    }

    @Override
    public String getEmail() {
        return null;
    }

    @Override
    public void setEmail(String email) {
        throw new IllegalStateException("Operation is not supported for anonymous user");
    }

    @Override
    public AuthPolicy getAuthPolicy() {
        return NoAuth.get();
    }

    @Override
    public void setAuthPolicy(AuthPolicy authPolicy) {
        throw new IllegalStateException("Operation is not supported for anonymous user");
    }

    @Override
    public List<Group> getGroups() {
        return Collections.emptyList();
    }

    @Override
    public void setGroups(List<Group> groups) {
        throw new IllegalStateException("Operation is not supported for anonymous user");
    }

    @Override
    public Date getExpirationDate() {
        return null;
    }

    @Override
    public void setExpirationDate(Date expirationDate) {
        throw new IllegalStateException("Operation is not supported for anonymous user");
    }

    @Override
    public Boolean isActive() {
        return true;
    }

    @Override
    public void setActive(Boolean active) {
        throw new IllegalStateException("Operation is not supported for anonymous user");
    }
}

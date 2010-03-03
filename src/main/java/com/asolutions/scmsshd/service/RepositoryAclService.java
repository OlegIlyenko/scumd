package com.asolutions.scmsshd.service;

import com.asolutions.scmsshd.model.security.Privilege;
import com.asolutions.scmsshd.model.security.User;

import java.util.Set;

/**
 * @author Oleg Ilyenko
 */
public interface RepositoryAclService {

    Set<Privilege> getAvailablePrivileges(String project, User user);

}

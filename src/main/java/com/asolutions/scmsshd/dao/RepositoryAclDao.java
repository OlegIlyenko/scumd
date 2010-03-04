package com.asolutions.scmsshd.dao;

import com.asolutions.scmsshd.model.security.RepositoryAcl;

import java.util.List;

/**
 * @author Oleg Ilyenko
 */
public interface RepositoryAclDao {

    UserDao getUserDao();

    void setUserDao(UserDao userDao);

    List<RepositoryAcl> getRepositoryAcl();

}
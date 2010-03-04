package com.asolutions.scmsshd.dao.impl;

import com.asolutions.scmsshd.dao.RepositoryAclDao;
import com.asolutions.scmsshd.dao.UserDao;
import com.asolutions.scmsshd.model.security.RepositoryAcl;
import com.asolutions.scmsshd.spring.helper.ApplicationContextHelper;

import java.util.List;

/**
 * @author Oleg Ilyenko
 */
public class SpringRepositoryAclDao implements RepositoryAclDao {

    private ApplicationContextHelper helper;

    public SpringRepositoryAclDao(ApplicationContextHelper helper) {
        this.helper = helper;
    }

    @Override
    public UserDao getUserDao() {
        return helper.getBean(RepositoryAclDao.class).getUserDao();
    }

    @Override
    public void setUserDao(UserDao userDao) {
        helper.getBean(RepositoryAclDao.class).setUserDao(userDao);
    }

    @Override
    public List<RepositoryAcl> getRepositoryAcl() {
        return helper.getBean(RepositoryAclDao.class).getRepositoryAcl();
    }
}

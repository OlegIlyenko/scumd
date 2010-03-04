package com.asolutions.scmsshd.spring.helper.impl;

import com.asolutions.scmsshd.dao.RepositoryAclDao;
import com.asolutions.scmsshd.dao.UserDao;

/**
 * @author Oleg Ilyenko
 */
public class DaoAwareAutoReloadableApplicationContextHelper extends AutoReloadableApplicationContextHelper {

    public DaoAwareAutoReloadableApplicationContextHelper(String applicationContextPath) {
        super(applicationContextPath);
    }

    @Override
    protected void doReload() {
        super.doReload();

        UserDao userDao = applicationContext.getBean(UserDao.class);
        RepositoryAclDao repositoryAclDao = applicationContext.getBean(RepositoryAclDao.class);

        repositoryAclDao.setUserDao(userDao);
    }
}

package com.asolutions.scmsshd.dao.impl;

import com.asolutions.scmsshd.dao.RepositoryAclDao;
import com.asolutions.scmsshd.dao.UserDao;
import com.asolutions.scmsshd.model.security.RepositoryAcl;

import java.util.List;

/**
 * @author Oleg Ilyenko
 */
public class DatabaseRepositoryAclDao extends BaseDatabaseDao implements RepositoryAclDao {

    @Override
    protected void createTables() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public UserDao getUserDao() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setUserDao(UserDao userDao) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<RepositoryAcl> getRepositoryAcl() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}

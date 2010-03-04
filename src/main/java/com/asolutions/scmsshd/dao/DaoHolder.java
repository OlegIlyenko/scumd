package com.asolutions.scmsshd.dao;

/**
 * @author Oleg Ilyenko
 */
public class DaoHolder {

    private UserDao userDao;

    private RepositoryAclDao repositoryAclDao;

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public RepositoryAclDao getRepositoryAclDao() {
        return repositoryAclDao;
    }

    public void setRepositoryAclDao(RepositoryAclDao repositoryAclDao) {
        this.repositoryAclDao = repositoryAclDao;
    }
}

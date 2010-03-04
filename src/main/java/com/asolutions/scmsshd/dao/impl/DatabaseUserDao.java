package com.asolutions.scmsshd.dao.impl;

import com.asolutions.scmsshd.dao.UserDao;
import com.asolutions.scmsshd.model.security.Group;
import com.asolutions.scmsshd.model.security.User;

import java.util.List;

/**
 * @author Oleg Ilyenko
 */
public class DatabaseUserDao extends BaseDatabaseDao implements UserDao {

    @Override
    protected void createTables() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<User> getUsers() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public User getUserByName(String name) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Group> getGroups() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Group getGroupByName(String name) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}

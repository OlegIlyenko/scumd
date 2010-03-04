package com.asolutions.scmsshd.dao.impl;

import com.asolutions.scmsshd.dao.UserDao;
import com.asolutions.scmsshd.model.security.Group;
import com.asolutions.scmsshd.model.security.User;
import com.asolutions.scmsshd.spring.helper.ApplicationContextHelper;

import java.util.List;

/**
 * @author Oleg Ilyenko
 */
public class SpringUserDao implements UserDao {

    private ApplicationContextHelper helper;

    public SpringUserDao(ApplicationContextHelper helper) {
        this.helper = helper;
    }

    @Override
    public List<User> getUsers() {
        return helper.getBean(UserDao.class).getUsers();
    }

    @Override
    public User getUserByName(String name) {
        return helper.getBean(UserDao.class).getUserByName(name);
    }

    @Override
    public List<Group> getGroups() {
        return helper.getBean(UserDao.class).getGroups();
    }

    @Override
    public Group getGroupByName(String name) {
        return helper.getBean(UserDao.class).getGroupByName(name);
    }
}

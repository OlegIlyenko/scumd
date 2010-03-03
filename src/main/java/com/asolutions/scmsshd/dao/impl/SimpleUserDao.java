package com.asolutions.scmsshd.dao.impl;

import com.asolutions.scmsshd.dao.UserDao;
import com.asolutions.scmsshd.model.security.Group;
import com.asolutions.scmsshd.model.security.User;

import java.util.List;

/**
 * @author Oleg Ilyenko
 */
public class SimpleUserDao implements UserDao {

    private final List<User> users;

    private final List<Group> groups;

    public SimpleUserDao(List<User> users, List<Group> groups) {
        this.users = users;
        this.groups = groups;
    }

    @Override
    public List<User> getUsers() {
        return users;
    }

    @Override
    public User getUserByName(String name) {
        for (User user : users) {
            if (user.getName().equals(name)) {
                return user;
            }
        }

        return null;
    }

    @Override
    public List<Group> getGroups() {
        return groups;
    }

    @Override
    public Group getGroupByName(String name) {
        for (Group group : groups) {
            if (group.getName().equals(name)) {
                return group;
            }
        }

        return null;
    }
}

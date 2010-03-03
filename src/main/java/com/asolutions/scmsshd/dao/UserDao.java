package com.asolutions.scmsshd.dao;

import com.asolutions.scmsshd.model.security.Group;
import com.asolutions.scmsshd.model.security.User;

import java.util.List;

/**
 * @author Oleg Ilyenko
 */
public interface UserDao {

    List<User> getUsers();

    User getUserByName(String name);

    List<Group> getGroups();

    Group getGroupByName(String name);
    
}

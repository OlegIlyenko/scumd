package com.asolutions.scmsshd.event.listener.impl;

import com.asolutions.scmsshd.dao.UserDao;
import com.asolutions.scmsshd.event.*;
import com.asolutions.scmsshd.model.security.Group;
import com.asolutions.scmsshd.model.security.User;
import com.asolutions.scmsshd.util.GitUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Oleg Ilyenko
 */
public class EmailListener {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    public static final String SUBJECT_PREFIX = "[git]";

    private String subject;
    private List<String> users;
    private List<String> groups;
    private List<String> emails;
    private EmailSender emailSender;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public List<String> getGroups() {
        return groups;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    public EmailSender getEmailSender() {
        return emailSender;
    }

    public void setEmailSender(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void on(AuthenticationSuccessEvent e) {
        sendEmail(getUserDao(e), e.getUserName() + "successfully authenticated", GitUtil.render(e));
    }

    private UserDao getUserDao(GitServerEvent e) {
        return e.getServer().getDaoHolder().getUserDao();
    }

    public void on(AuthenticationFailEvent e) {
        sendEmail(getUserDao(e), e.getUserName() + " authentication failed", GitUtil.render(e));
    }

    public void on(AuthorizationSuccessEvent e) {
        sendEmail(getUserDao(e), e.getUser().getName() + "successfully authorized", GitUtil.render(e));
    }

    public void on(AuthorizationFailEvent e) {
        sendEmail(getUserDao(e), e.getUser().getName() + " authorization failed", GitUtil.render(e));
    }

    public void on(RepositoryCreateEvent e) {
        sendEmail(getUserDao(e), e.getUser().getName() + " created repository " + e.getRepositoryInfo().getRepositoryPath(), GitUtil.render(e));
    }

    public void on(PullEvent e) {
        sendEmail(getUserDao(e), e.getUser().getName() + " pulled changes from repository " + e.getRepositoryInfo().getRepositoryPath(), GitUtil.render(e));
    }

    public void on(PushEvent e) {
        sendEmail(getUserDao(e), e.getUser().getName() + getPushDescription(e), GitUtil.render(e));
    }

    private String getPushDescription(PushEvent e) {
        switch (e.getType()) {
            case Create:
                return " created ref " + (e.getRefName() != null ? e.getRefName() : "") +
                        " in repository " + e.getRepositoryInfo().getRepositoryPath();
            case Delete:
                return " deleted ref " + (e.getRefName() != null ? e.getRefName() : "") +
                        " in repository " + e.getRepositoryInfo().getRepositoryPath();
            case Update:
                return " pushed changes to repository " + e.getRepositoryInfo().getRepositoryPath() +
                        (e.getRefName() != null ? " @" + e.getRefName() : "");
            default:
                throw new IllegalArgumentException("Unsupported type: " + e.getType());
        }
    }

    public void on(CommitEvent e) {
        sendEmail(getUserDao(e),
                e.getUser().getName() +
                        " pushed commit to repository " + e.getRepositoryInfo().getRepositoryPath() +
                        (e.getRefName() != null ? " @" + e.getRefName() : ""),
                GitUtil.render(e));
    }

    private void sendEmail(UserDao userDao, String defaultSubject, String body) {
        emailSender.sendMessage(getAllEmails(userDao), getSubject(defaultSubject), body);
    }

    private String getSubject(String defaultSubject) {
        return SUBJECT_PREFIX + " " + (subject != null && !subject.trim().equals("") ? subject : defaultSubject);
    }

    public List<String> getAllEmails(UserDao userDao) {
        List<String> allEmails = new ArrayList<String>();
        List<User> allUsers = userDao.getUsers();

        addEmails(allEmails);
        addUsers(allEmails, allUsers);
        addGroups(allEmails, allUsers);

        return allEmails;
    }

    private void addGroups(List<String> allEmails, List<User> allUsers) {
        if (groups != null) {
            for (String groupName : groups) {
                for (User u : getUsersByGroup(allUsers, groupName)) {
                    if (u.getEmail() != null && !u.getEmail().trim().equals("")) {
                        allEmails.add(u.getEmail());
                    }
                }
            }
        }
    }

    private void addUsers(List<String> allEmails, List<User> allUsers) {
        if (users != null) {
            for (String userName : users) {
                User u = getUserByName(allUsers, userName);

                if (u != null && u.getEmail() != null && !u.getEmail().trim().equals("")) {
                    allEmails.add(u.getEmail());
                }
            }
        }
    }

    private void addEmails(List<String> allEmails) {
        if (emails != null) {
            allEmails.addAll(emails);
        }
    }

    private User getUserByName(List<User> users, String name) {
        for (User u : users) {
            if (u.getName().equals(name)) {
                return u;
            }
        }

        return null;
    }

    private List<User> getUsersByGroup(List<User> users, String name) {
        List<User> result = new ArrayList<User>();

        for (User u : users) {
            for (Group g : u.getGroups()) {
                if (g.getName().equals(name)) {
                    result.add(u);
                    break;
                }
            }
        }

        return result;
    }
}

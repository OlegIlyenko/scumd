package com.asolutions.scmsshd.authorizors;

import com.asolutions.scmsshd.commands.git.GitSCMRepositoryProvider;
import com.asolutions.scmsshd.dao.UserDao;
import com.asolutions.scmsshd.model.security.Privilege;
import com.asolutions.scmsshd.model.security.User;
import com.asolutions.scmsshd.service.RepositoryAclService;
import com.asolutions.scmsshd.sshd.IProjectAuthorizer;
import com.asolutions.scmsshd.sshd.UnparsableProjectException;

import java.io.File;
import java.util.Set;

/**
 * @author Oleg Ilyenko
 */
public class RepositoryAclProjectAuthorizer implements IProjectAuthorizer {

    private RepositoryAclService aclService;

    private GitSCMRepositoryProvider repositoryProvider;

    private String repositoriesDir;

    private UserDao userDao;

    public RepositoryAclService getAclService() {
        return aclService;
    }

    public void setAclService(RepositoryAclService aclService) {
        this.aclService = aclService;
    }

    public GitSCMRepositoryProvider getRepositoryProvider() {
        return repositoryProvider;
    }

    public void setRepositoryProvider(GitSCMRepositoryProvider repositoryProvider) {
        this.repositoryProvider = repositoryProvider;
    }

    public String getRepositoriesDir() {
        return repositoriesDir;
    }

    public void setRepositoriesDir(String repositoriesDir) {
        this.repositoriesDir = repositoriesDir;
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public AuthorizationLevel userIsAuthorizedForProject(String userName, String project) throws UnparsableProjectException {
        User user = userDao.getUserByName(userName);
        Set<Privilege> available = aclService.getAvailablePrivileges(project, user);

        boolean repositoryExists = repositoryProvider.exists(new File(repositoriesDir), project);

        if (!repositoryExists && available.contains(Privilege.Create)) {
            return AuthorizationLevel.AUTH_LEVEL_READ_WRITE;
        } else if (!repositoryExists) {
            return null;
        }

        if (available.contains(Privilege.ReadWrite)) {
            return AuthorizationLevel.AUTH_LEVEL_READ_WRITE;
        } else if (available.contains(Privilege.ReadOnly)) {
            return AuthorizationLevel.AUTH_LEVEL_READ_ONLY;
        } else {
            return null;
        }
    }
}

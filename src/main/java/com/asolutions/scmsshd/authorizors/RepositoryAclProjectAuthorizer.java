package com.asolutions.scmsshd.authorizors;

import com.asolutions.scmsshd.commands.git.GitSCMRepositoryProvider;
import com.asolutions.scmsshd.dao.UserDao;
import com.asolutions.scmsshd.model.security.Privilege;
import com.asolutions.scmsshd.model.security.User;
import com.asolutions.scmsshd.service.RepositoryAclService;
import com.asolutions.scmsshd.sshd.ProjectAuthorizer;
import com.asolutions.scmsshd.sshd.UnparsableProjectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Set;

/**
 * @author Oleg Ilyenko
 */
public class RepositoryAclProjectAuthorizer implements ProjectAuthorizer {

    protected final Logger log = LoggerFactory.getLogger(getClass());

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
        log.debug("User '" + userName + "' made attempt to access" + (repositoryExists ? "" : " non-existing") + " repository '" + project +
                "'. He has following privileges for it: " + available);

        if (!repositoryExists && available.contains(Privilege.Create)) {
            return AuthorizationLevel.AUTH_LEVEL_READ_WRITE;
        } else if (!repositoryExists) {
            log.warn("User '" + userName + "' made attempt to create new repository '" + project + "' but he does not have Create privilege!");
            return null;
        }

        if (available.contains(Privilege.ReadWrite)) {
            return AuthorizationLevel.AUTH_LEVEL_READ_WRITE;
        } else if (available.contains(Privilege.ReadOnly)) {
            return AuthorizationLevel.AUTH_LEVEL_READ_ONLY;
        } else {
            log.warn("User '" + userName + "' made attempt to access repository '" + project + "' but he does not have rights for it!");
            return null;
        }
    }
}

package com.asolutions.scmsshd.authorizors;

import com.asolutions.scmsshd.InteractionContext;
import com.asolutions.scmsshd.InteractionContextKey;
import com.asolutions.scmsshd.commands.git.GitSCMRepositoryProvider;
import com.asolutions.scmsshd.dao.UserDao;
import com.asolutions.scmsshd.event.CancelEventException;
import com.asolutions.scmsshd.event.impl.AuthorizationEventImpl;
import com.asolutions.scmsshd.event.impl.AuthorizationFailEventImpl;
import com.asolutions.scmsshd.event.impl.AuthorizationSuccessEventImpl;
import com.asolutions.scmsshd.event.impl.RepositoryInfo;
import com.asolutions.scmsshd.event.listener.EventDispatcher;
import com.asolutions.scmsshd.model.security.Privilege;
import com.asolutions.scmsshd.model.security.User;
import com.asolutions.scmsshd.service.RepositoryAclService;
import com.asolutions.scmsshd.sshd.ProjectAuthorizer;
import com.asolutions.scmsshd.sshd.UnparsableProjectException;
import org.apache.sshd.server.session.ServerSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.SocketAddress;
import java.util.Set;

import static com.asolutions.scmsshd.event.listener.EventDispatcher.Stage.Pre;

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
    public AuthorizationLevel userIsAuthorizedForProject(String userName, String project, ServerSession session)
            throws UnparsableProjectException {
        InteractionContext context = session.getAttribute(InteractionContextKey.get());
        EventDispatcher eventDispatcher = context.getEventDispatcher();
        User user = userDao.getUserByName(userName);
        Set<Privilege> available = aclService.getAvailablePrivileges(project, user);
        boolean repositoryExists = repositoryProvider.exists(new File(repositoriesDir), project, context);
        RepositoryInfo info = new RepositoryInfo(null, project, repositoryExists);
        SocketAddress address = session.getIoSession().getRemoteAddress();

        log.debug("User '" + userName + "' made attempt to access" +
                (repositoryExists ? "" : " non-existing") + " repository '" + project +
                "'. He has following privileges for it: " + available);

        try {
            eventDispatcher.fireEvent(Pre, new AuthorizationEventImpl(user, context.getServer(), info, available, address));
        } catch (CancelEventException e) {
            log.info("Authorization was cancelled by listener: \n" + e.getContextInfo());
            return null;
        }

        if (!repositoryExists && available.contains(Privilege.Create)) {
            eventDispatcher.fireEvent(new AuthorizationSuccessEventImpl(user, context.getServer(), info, available, address, AuthorizationLevel.AUTH_LEVEL_READ_WRITE));
            return AuthorizationLevel.AUTH_LEVEL_READ_WRITE;
        } else if (!repositoryExists) {
            String reason = "User '" + userName + "' made attempt to create new repository '" + project + "' but he does not have Create privilege!";
            log.warn(reason);
            eventDispatcher.fireEvent(new AuthorizationFailEventImpl(user, context.getServer(), info, available, address, reason));
            return null;
        }

        if (available.contains(Privilege.ReadWrite)) {
            eventDispatcher.fireEvent(new AuthorizationSuccessEventImpl(user, context.getServer(), info, available, address, AuthorizationLevel.AUTH_LEVEL_READ_WRITE));
            return AuthorizationLevel.AUTH_LEVEL_READ_WRITE;
        } else if (available.contains(Privilege.ReadOnly)) {
            eventDispatcher.fireEvent(new AuthorizationSuccessEventImpl(user, context.getServer(), info, available, address, AuthorizationLevel.AUTH_LEVEL_READ_ONLY));
            return AuthorizationLevel.AUTH_LEVEL_READ_ONLY;
        } else {
            String reason = "User '" + userName + "' made attempt to access repository '" + project + "' but he does not have rights for it!";
            log.warn(reason);
            eventDispatcher.fireEvent(new AuthorizationFailEventImpl(user, context.getServer(), info, available, address, reason));
            return null;
        }
    }
}

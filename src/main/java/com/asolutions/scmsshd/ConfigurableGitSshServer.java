package com.asolutions.scmsshd;

import com.asolutions.scmsshd.authenticators.UserDaoPasswordAuthenticator;
import com.asolutions.scmsshd.authenticators.UserDaoPublickeyAuthenticator;
import com.asolutions.scmsshd.authorizors.RepositoryAclProjectAuthorizer;
import com.asolutions.scmsshd.commands.factories.CommandFactoryBase;
import com.asolutions.scmsshd.commands.factories.GitCommandFactory;
import com.asolutions.scmsshd.commands.factories.GitSCMCommandFactory;
import com.asolutions.scmsshd.commands.git.GitSCMRepositoryProvider;
import com.asolutions.scmsshd.converters.path.regexp.ConfigurablePathToProjectConverter;
import com.asolutions.scmsshd.dao.DaoHolder;
import com.asolutions.scmsshd.dao.RepositoryAclDao;
import com.asolutions.scmsshd.dao.UserDao;
import com.asolutions.scmsshd.event.CancelEventException;
import com.asolutions.scmsshd.event.impl.GitServerStartEventImpl;
import com.asolutions.scmsshd.event.impl.GitServerStopEventImpl;
import com.asolutions.scmsshd.event.listener.DefaultEventDispatcher;
import com.asolutions.scmsshd.event.listener.EventDispatcher;
import com.asolutions.scmsshd.event.listener.UncheckedListener;
import com.asolutions.scmsshd.service.RepositoryAclService;
import com.asolutions.scmsshd.service.impl.SimpleRepositoryAclService;
import com.asolutions.scmsshd.spring.xml.ObjectHolder;
import com.asolutions.scmsshd.util.Function;
import org.apache.sshd.SshServer;
import org.apache.sshd.common.Compression;
import org.apache.sshd.common.KeyPairProvider;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.common.compression.CompressionNone;
import org.apache.sshd.common.util.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import javax.naming.NamingException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import static com.asolutions.scmsshd.event.listener.EventDispatcher.Stage.Post;
import static com.asolutions.scmsshd.event.listener.EventDispatcher.Stage.Pre;

/**
 * @author Oleg Ilyenko
 */
public class ConfigurableGitSshServer implements InitializingBean, DisposableBean {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private int port;

    private String repositoriesDir;

    private UserDao userDao;

    private RepositoryAclDao repositoryAclDao;

    private KeyPairProvider serverKeyPairProvider;

    private RepositoryAclService repositoryAclService;

    private DaoHolder daoHolder;

    private EventDispatcher eventDispatcher = new DefaultEventDispatcher();

    private SshServer sshd;

    private int filesProEventLimit = 2000;

    private boolean allowCaching = true;

    private List<UncheckedListener> listeners;

    private ObjectHolder<List<List<UncheckedListener>>> globalListeners;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getRepositoriesDir() {
        return repositoriesDir;
    }

    public void setRepositoriesDir(String repositoriesDir) {
        this.repositoriesDir = repositoriesDir;
    }

    public KeyPairProvider getServerKeyPairProvider() {
        return serverKeyPairProvider;
    }

    public void setServerKeyPairProvider(KeyPairProvider serverKeyPairProvider) {
        this.serverKeyPairProvider = serverKeyPairProvider;
    }

    public DaoHolder getDaoHolder() {
        return daoHolder;
    }

    public void setDaoHolder(DaoHolder daoHolder) {
        this.daoHolder = daoHolder;
    }

    public EventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    public void setEventDispatcher(EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
    }

    public int getFilesProEventLimit() {
        return filesProEventLimit;
    }

    public void setFilesProEventLimit(int filesProEventLimit) {
        this.filesProEventLimit = filesProEventLimit;
    }

    public boolean isAllowCaching() {
        return allowCaching;
    }

    public void setAllowCaching(boolean allowCaching) {
        this.allowCaching = allowCaching;
    }

    public List<UncheckedListener> getListeners() {
        return listeners;
    }

    public void setListeners(List<UncheckedListener> listeners) {
        this.listeners = listeners;
    }

    public ObjectHolder<List<List<UncheckedListener>>> getGlobalListeners() {
        return globalListeners;
    }

    public void setGlobalListeners(ObjectHolder<List<List<UncheckedListener>>> globalListeners) {
        this.globalListeners = globalListeners;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        userDao = daoHolder.getUserDao();
        repositoryAclDao = daoHolder.getRepositoryAclDao();
        repositoryAclDao.setUserDao(userDao);
        repositoryAclService = new SimpleRepositoryAclService(repositoryAclDao);

        SecurityUtils.setRegisterBouncyCastle(true);

        final SshServer sshd = SshServer.setUpDefaultServer();
        GitSCMRepositoryProvider repositoryProvider = new GitSCMRepositoryProvider();

        sshd.setPort(port);
        setCommandFactory(sshd, repositoryProvider);
        sshd.setKeyPairProvider(serverKeyPairProvider);
        sshd.setCompressionFactories(Arrays.<NamedFactory<Compression>>asList(new CompressionNone.Factory()));

        setupAuthenticators(sshd);
        setupListeners(eventDispatcher, listeners);
        setupListenerList(eventDispatcher, globalListeners.getObject());

        try {
            log.info("Starting SSH Server on port " + port + " for serving repositories at: " + repositoriesDir);
            eventDispatcher.fireEvent(Pre, new GitServerStartEventImpl(this));
            this.sshd = sshd;
            this.sshd.start();
            eventDispatcher.fireEvent(Post, new GitServerStartEventImpl(this));
        } catch (CancelEventException e) {
            log.error("Aborting because listener cancelled it: \n" + e.getContextInfo());
            destroy();
        } catch (Exception e) {
            log.error("Aborting because of exceptoin.", e);
            destroy();
        } catch (Throwable e) {
            log.error("Aborting because of exceptoin.", e);
            destroy();
        }
    }

    private void setupListenerList(EventDispatcher eventDispatcher, List<List<UncheckedListener>> listenersList) {
        if (listenersList != null) {
            for (List<UncheckedListener> listener : listenersList) {
                if (listener != null && listener.size() > 0) {
                    setupListeners(eventDispatcher, listener);
                }
            }
        }
    }

    private void setupListeners(EventDispatcher eventDispatcher, List<UncheckedListener> listeners) {
        for (UncheckedListener listener : listeners) {
            eventDispatcher.addListener(listener);
        }
    }

    @Override
    public void destroy() throws Exception {
        if (sshd != null) {
            log.info("Stoppeing SSH Server.");
            eventDispatcher.fireEvent(Pre, new GitServerStopEventImpl(this));
            sshd.stop(true);
            eventDispatcher.fireEvent(Post, new GitServerStopEventImpl(this));
        }
    }

    private void setupAuthenticators(SshServer sshd) {
        Function<InteractionContext> contextProvider = new Function<InteractionContext>() {
            public InteractionContext apply() {
                return new InteractionContext(ConfigurableGitSshServer.this, eventDispatcher, filesProEventLimit, allowCaching);
            }
        };

        sshd.setPasswordAuthenticator(new UserDaoPasswordAuthenticator(userDao, contextProvider, true));
        sshd.setPublickeyAuthenticator(new UserDaoPublickeyAuthenticator(userDao, contextProvider, true));
    }

    private void setCommandFactory(SshServer sshd, GitSCMRepositoryProvider repositoryProvider) throws NamingException {
        CommandFactoryBase commandFactory = new GitCommandFactory(repositoryProvider);
        commandFactory.setPathToProjectNameConverter(new ConfigurablePathToProjectConverter(Pattern.compile("^/(.*)$")));

        setupAuthorizers(commandFactory, repositoryProvider);

        Properties config = new Properties();
        config.setProperty(GitSCMCommandFactory.REPOSITORY_BASE, repositoriesDir);
        commandFactory.setConfiguration(config);

        sshd.setCommandFactory(commandFactory);
    }

    private void setupAuthorizers(CommandFactoryBase commandFactory, GitSCMRepositoryProvider repositoryProvider) throws NamingException {
        RepositoryAclProjectAuthorizer authorizer = new RepositoryAclProjectAuthorizer();
        authorizer.setRepositoriesDir(repositoriesDir);
        authorizer.setRepositoryProvider(repositoryProvider);
        authorizer.setAclService(repositoryAclService);

        commandFactory.setProjectAuthorizor(authorizer);
    }
}

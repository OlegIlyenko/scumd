package com.asolutions.scmsshd;

import com.asolutions.scmsshd.authenticators.UserDaoPasswordAuthenticator;
import com.asolutions.scmsshd.authenticators.UserDaoPublickeyAuthenticator;
import com.asolutions.scmsshd.authorizors.RepositoryAclProjectAuthorizer;
import com.asolutions.scmsshd.commands.factories.CommandFactoryBase;
import com.asolutions.scmsshd.commands.factories.GitCommandFactory;
import com.asolutions.scmsshd.commands.factories.GitSCMCommandFactory;
import com.asolutions.scmsshd.commands.git.AutoCreatingGitSCMRepositoryProvider;
import com.asolutions.scmsshd.converters.path.regexp.ConfigurablePathToProjectConverter;
import com.asolutions.scmsshd.dao.DaoHolder;
import com.asolutions.scmsshd.dao.RepositoryAclDao;
import com.asolutions.scmsshd.dao.UserDao;
import com.asolutions.scmsshd.service.RepositoryAclService;
import com.asolutions.scmsshd.service.impl.SimpleRepositoryAclService;
import org.apache.sshd.SshServer;
import org.apache.sshd.common.Compression;
import org.apache.sshd.common.KeyPairProvider;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.common.compression.CompressionNone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import javax.naming.NamingException;
import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * @author Oleg Ilyenko
 */
public class ConfigurableGitSshServer implements InitializingBean {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private int port;

    private UserDao userDao;

    private RepositoryAclDao repositoryAclDao;

    private String repositoriesDir;

    private KeyPairProvider serverKeyPairProvider;

    private RepositoryAclService repositoryAclService;

    private DaoHolder daoHolder;

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

    @Override
    public void afterPropertiesSet() throws Exception {
        userDao = daoHolder.getUserDao();
        repositoryAclDao = daoHolder.getRepositoryAclDao();
        repositoryAclDao.setUserDao(userDao);
        repositoryAclService = new SimpleRepositoryAclService(repositoryAclDao);

        final SshServer sshd = SshServer.setUpDefaultServer();
        AutoCreatingGitSCMRepositoryProvider repositoryProvider = new AutoCreatingGitSCMRepositoryProvider();

        sshd.setPort(port);
        setCommandFactory(sshd, repositoryProvider);
        sshd.setKeyPairProvider(serverKeyPairProvider);
        sshd.setCompressionFactories(Arrays.<NamedFactory<Compression>>asList(new CompressionNone.Factory()));

        setupAuthenticators(sshd);

        try {
            log.info("Starting SSH Server on port " + port + "...");
            sshd.start();
        } catch (Exception e) {
            log.error("Aborting because of exceptoin.", e);
            sshd.stop();
        } catch (Throwable e) {
            log.error("Aborting because of exceptoin.", e);
            sshd.stop();
        }
    }
    
    private void setupAuthenticators(SshServer sshd) {
        sshd.setPasswordAuthenticator(new UserDaoPasswordAuthenticator(userDao));
        sshd.setPublickeyAuthenticator(new UserDaoPublickeyAuthenticator(userDao));
    }

    private void setCommandFactory(SshServer sshd, AutoCreatingGitSCMRepositoryProvider repositoryProvider) throws NamingException {
        CommandFactoryBase commandFactory = new GitCommandFactory(repositoryProvider);
        commandFactory.setPathToProjectNameConverter(new ConfigurablePathToProjectConverter(Pattern.compile("^/(.*)$")));

        setupAuthorizers(commandFactory, repositoryProvider);

        Properties config = new Properties();
        config.setProperty(GitSCMCommandFactory.REPOSITORY_BASE, repositoriesDir);
        commandFactory.setConfiguration(config);

        sshd.setCommandFactory(commandFactory);
    }

    private void setupAuthorizers(CommandFactoryBase commandFactory, AutoCreatingGitSCMRepositoryProvider repositoryProvider) throws NamingException {
        RepositoryAclProjectAuthorizer authorizer = new RepositoryAclProjectAuthorizer();
        authorizer.setRepositoriesDir(repositoriesDir);
        authorizer.setRepositoryProvider(repositoryProvider);
        authorizer.setUserDao(userDao);
        authorizer.setAclService(repositoryAclService);

        commandFactory.setProjectAuthorizor(authorizer);
    }
}

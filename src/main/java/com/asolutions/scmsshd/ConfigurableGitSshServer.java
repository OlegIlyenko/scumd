package com.asolutions.scmsshd;

import com.asolutions.scmsshd.authenticators.UserDaoPasswordAuthenticator;
import com.asolutions.scmsshd.authenticators.UserDaoPublickeyAuthenticator;
import com.asolutions.scmsshd.authorizors.RepositoryAclProjectAuthorizer;
import com.asolutions.scmsshd.commands.factories.CommandFactoryBase;
import com.asolutions.scmsshd.commands.factories.GitCommandFactory;
import com.asolutions.scmsshd.commands.factories.GitSCMCommandFactory;
import com.asolutions.scmsshd.commands.git.AutoCreatingGitSCMRepositoryProvider;
import com.asolutions.scmsshd.converters.path.regexp.ConfigurablePathToProjectConverter;
import com.asolutions.scmsshd.dao.UserDao;
import com.asolutions.scmsshd.dao.impl.SimpleRepositoryAclDao;
import com.asolutions.scmsshd.model.security.RawRepositoryAcl;
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
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * @author Oleg Ilyenko
 */
public class ConfigurableGitSshServer implements InitializingBean {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private int port;

    private List<RawRepositoryAcl> rawRepositoryAcl;

    private UserDao userDao;

    private String repositoriesDir;

    private KeyPairProvider serverKeyPairProvider;

    private RepositoryAclService repositoryAclService;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public List<RawRepositoryAcl> getRawRepositoryAcl() {
        return rawRepositoryAcl;
    }

    public void setRawRepositoryAcl(List<RawRepositoryAcl> rawRepositoryAcl) {
        this.rawRepositoryAcl = rawRepositoryAcl;
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
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

    @Override
    public void afterPropertiesSet() throws Exception {
        repositoryAclService = new SimpleRepositoryAclService(new SimpleRepositoryAclDao(rawRepositoryAcl, userDao));

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

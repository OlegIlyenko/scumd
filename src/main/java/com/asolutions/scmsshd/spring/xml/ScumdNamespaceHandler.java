package com.asolutions.scmsshd.spring.xml;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * @author Oleg Ilyenko
 */
public class ScumdNamespaceHandler extends NamespaceHandlerSupport {

    public static final String GIT_SSH_SERVER_ELEM = "git-ssh-server";
    public static final String SIMPLE_USER_DAO_ELEM = "simple-user-dao";
    public static final String ACL_ELEM = "acl";
    public static final String DEFAULT_SERVER_KEY_PAIR_ELEM = "default-server-key-pair";
    public static final String FILE_SERVER_KEY_PAIR_ELEM = "file-server-key-pair";
    public static final String SIMPLE_SERVER_KEY_PAIR_ELEM = "simple-server-key-pair";
    public static final String USER_DAO_ACL_FILE_ELEM = "user-dao-acl-file";

    public static final String DEFAULT_GIT_SSH_SERVER_ID = "git-ssh-server";
    public static final String DEFAULT_USER_DAO_ID = "user-dao";
    public static final String DEFAULT_ACL_ID = "repository-acl";
    public static final String DEFAULT_SERVER_KEY_PAIR_ID = "server-key-pair";
    public static final String DEFAULT_DAO_HOLDER_ID = "dao-holder";

    @Override
    public void init() {
        registerBeanDefinitionParser(GIT_SSH_SERVER_ELEM, new GitSshServerBeanDefinitionParser());
        registerBeanDefinitionParser(SIMPLE_USER_DAO_ELEM, new SimpleUserDaoBeanDefinitionParser());
        registerBeanDefinitionParser(ACL_ELEM, new AclBeanDefinitionParser());
        registerBeanDefinitionParser(USER_DAO_ACL_FILE_ELEM, new UserDaoAclFileBeanDefinitionParser());

        registerBeanDefinitionParser(DEFAULT_SERVER_KEY_PAIR_ELEM, new DefaultKeyPairBeanDefinitionParser());
        registerBeanDefinitionParser(FILE_SERVER_KEY_PAIR_ELEM, new FileKeyPairBeanDefinitionParser());
        registerBeanDefinitionParser(SIMPLE_SERVER_KEY_PAIR_ELEM, new SimpleKeyPairBeanDefinitionParser());
    }
}

package com.asolutions.scmsshd.spring.xml;

import com.asolutions.scmsshd.ConfigurableGitSshServer;
import com.asolutions.scmsshd.dao.DaoHolder;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

/**
 * @author Oleg Ilyenko
 */
public class GitSshServerBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    public static final String PORT_ATTR = "port";
    public static final String REPOSITORIES_BASE_ATTR = "repositories-base";
    public static final String USER_DAO_REF_ATTR = "user-dao-ref";
    public static final String ACL_REF_ATTR = "acl-ref";
    public static final String SERVER_KEY_PAIR_REF_ATTR = "server-key-pair-ref";
    public static final String USER_DAO_ACL_REF_ATTR = "user-dao-acl-ref";

    @Override
    protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) throws BeanDefinitionStoreException {
        if (parserContext.isNested()) {
            return null;
        }

        return StringUtils.hasText(element.getAttribute("id")) ? element.getAttribute("id") : ScumdNamespaceHandler.DEFAULT_GIT_SSH_SERVER_ID;
    }

    @Override
    protected Class getBeanClass(Element element) {
        return ConfigurableGitSshServer.class;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        builder.addPropertyValue("port", element.getAttribute(PORT_ATTR));
        builder.addPropertyValue("repositoriesDir", element.getAttribute(REPOSITORIES_BASE_ATTR));

        processKeyPair(element, parserContext, builder);

        BeanDefinitionBuilder shadowDaoHolderBuilder = BeanDefinitionBuilder.genericBeanDefinition(DaoHolder.class);
        boolean userDaoFound = processUserDao(element, parserContext, shadowDaoHolderBuilder);
        boolean repositoryAclFound = processRepositoryAcl(element, parserContext, shadowDaoHolderBuilder);

        if (!userDaoFound || !repositoryAclFound) {
            processDaoHolder(element, parserContext, builder);
        } else {
            builder.addPropertyValue("daoHolder", shadowDaoHolderBuilder.getBeanDefinition());
        }
    }

    protected void processKeyPair(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        String keyPairRef = element.getAttribute(SERVER_KEY_PAIR_REF_ATTR);

        if (StringUtils.hasText(keyPairRef)) {
            builder.addPropertyReference("serverKeyPairProvider", keyPairRef);
            return;
        }

        Element keyPair = DomUtils.getChildElementByTagName(element, ScumdNamespaceHandler.DEFAULT_SERVER_KEY_PAIR_ELEM);

        if (keyPair == null) {
            keyPair = DomUtils.getChildElementByTagName(element, ScumdNamespaceHandler.FILE_SERVER_KEY_PAIR_ELEM);
        }

        if (keyPair == null) {
            keyPair = DomUtils.getChildElementByTagName(element, ScumdNamespaceHandler.SIMPLE_SERVER_KEY_PAIR_ELEM);
        }

        if (keyPair != null) {
            BeanDefinition keyPairBean = parserContext.getDelegate().parseCustomElement(keyPair, builder.getRawBeanDefinition());
            builder.addPropertyValue("serverKeyPairProvider", keyPairBean);
        } else {
            builder.addPropertyReference("serverKeyPairProvider", ScumdNamespaceHandler.DEFAULT_SERVER_KEY_PAIR_ID);
        }
    }

    protected boolean processUserDao(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        String userDaoRef = element.getAttribute(USER_DAO_REF_ATTR);
        Element userDao = DomUtils.getChildElementByTagName(element, ScumdNamespaceHandler.SIMPLE_USER_DAO_ELEM);

        if (StringUtils.hasText(userDaoRef)) {
            builder.addPropertyReference("userDao", userDaoRef);
        } else if (userDao != null) {
            BeanDefinition userDaoBean = parserContext.getDelegate().parseCustomElement(userDao, builder.getRawBeanDefinition());
            builder.addPropertyValue("userDao", userDaoBean);
        } else {
            return false;
        }

        return true;
    }

    protected boolean processRepositoryAcl(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        String aclRef = element.getAttribute(ACL_REF_ATTR);
        Element acl = DomUtils.getChildElementByTagName(element, ScumdNamespaceHandler.ACL_ELEM);

        if (StringUtils.hasText(aclRef)) {
            builder.addPropertyReference("repositoryAclDao", aclRef);
        } else if (acl != null) {
            BeanDefinition aclBean = parserContext.getDelegate().parseCustomElement(acl, builder.getRawBeanDefinition());
            builder.addPropertyValue("repositoryAclDao", aclBean);
        } else {
            return false;
        }

        return true;
    }

    /**
     * @return true if dao holder found
     */
    protected boolean processDaoHolder(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        String daoHolderRef = element.getAttribute(USER_DAO_ACL_REF_ATTR);
        Element fileDaoHolder = DomUtils.getChildElementByTagName(element, ScumdNamespaceHandler.FILE_USER_DAO_ACL_ELEM);
        Element databaseDaoHolder = DomUtils.getChildElementByTagName(element, ScumdNamespaceHandler.DATABASE_USER_DAO_ACL_ELEM);

        if (StringUtils.hasText(daoHolderRef)) {
            builder.addPropertyReference("daoHolder", daoHolderRef);
        } else if (fileDaoHolder != null) {
            BeanDefinition daoHolderBean = parserContext.getDelegate().parseCustomElement(fileDaoHolder, builder.getRawBeanDefinition());
            builder.addPropertyValue("daoHolder", daoHolderBean);
        } else if (databaseDaoHolder != null) {
            BeanDefinition daoHolderBean = parserContext.getDelegate().parseCustomElement(databaseDaoHolder, builder.getRawBeanDefinition());
            builder.addPropertyValue("daoHolder", daoHolderBean);
        } else {
            builder.addPropertyReference("daoHolder", ScumdNamespaceHandler.DEFAULT_DAO_HOLDER_ID);
        }

        return true;
    }

}

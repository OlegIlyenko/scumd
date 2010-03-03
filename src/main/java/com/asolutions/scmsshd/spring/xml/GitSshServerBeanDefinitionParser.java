package com.asolutions.scmsshd.spring.xml;

import com.asolutions.scmsshd.ConfigurableGitSshServer;
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

    public static final String ID_ATTR = "id";
    public static final String PORT_ATTR = "port";
    public static final String REPOSITORIES_BASE_ATTR = "repositories-base";
    public static final String USER_DAO_REF_ATTR = "user-dao-ref";
    public static final String ACL_REF_ATTR = "acl-ref";
    public static final String SERVER_KEY_PAIR_REF_ATTR = "server-key-pair-ref";

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
        builder.addPropertyValue("port", element.getAttribute("port"));
        builder.addPropertyValue("repositoriesDir", element.getAttribute(REPOSITORIES_BASE_ATTR));

        processUserDao(element, parserContext, builder);
        processRepositoryAcl(element, parserContext, builder);
        processKeyPair(element, parserContext, builder);
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

    protected void processUserDao(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        String userDaoRef = element.getAttribute(USER_DAO_REF_ATTR);
        Element userDao = DomUtils.getChildElementByTagName(element, ScumdNamespaceHandler.SIMPLE_USER_DAO_ELEM);

        if (StringUtils.hasText(userDaoRef)) {
            builder.addPropertyReference("userDao", userDaoRef);
        } else if (userDao != null) {
            BeanDefinition userDaoBean = parserContext.getDelegate().parseCustomElement(userDao, builder.getRawBeanDefinition());
            builder.addPropertyValue("userDao", userDaoBean);
        } else {
            builder.addPropertyReference("userDao", ScumdNamespaceHandler.DEFAULT_USER_DAO_ID);
        }
    }

    protected void processRepositoryAcl(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        String aclRef = element.getAttribute(ACL_REF_ATTR);
        Element acl = DomUtils.getChildElementByTagName(element, ScumdNamespaceHandler.ACL_ELEM);

        if (StringUtils.hasText(aclRef)) {
            builder.addPropertyReference("rawRepositoryAcl", aclRef);
        } else if (acl != null) {
            BeanDefinition aclBean = parserContext.getDelegate().parseCustomElement(acl, builder.getRawBeanDefinition());
            builder.addPropertyValue("rawRepositoryAcl", aclBean);
        } else {
            builder.addPropertyReference("rawRepositoryAcl", ScumdNamespaceHandler.DEFAULT_ACL_ID);
        }
    }
}

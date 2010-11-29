package com.asolutions.scmsshd.spring.xml;

import com.asolutions.scmsshd.ConfigurableGitSshServer;
import com.asolutions.scmsshd.dao.DaoHolder;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

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
    public static final String MAX_FILES_PRO_EVENT_ATTR = "max-files-pro-event";
    public static final String ALLOW_CACHING_ATTR = "allow-caching";

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

        if (StringUtils.hasText(element.getAttribute(MAX_FILES_PRO_EVENT_ATTR))) {
            builder.addPropertyValue("filesProEventLimit", element.getAttribute(MAX_FILES_PRO_EVENT_ATTR));
        }

        if (StringUtils.hasText(element.getAttribute(ALLOW_CACHING_ATTR))) {
            builder.addPropertyValue("allowCaching", element.getAttribute(ALLOW_CACHING_ATTR));
        }

        processKeyPair(element, parserContext, builder);

        BeanDefinitionBuilder shadowDaoHolderBuilder = BeanDefinitionBuilder.genericBeanDefinition(DaoHolder.class);
        boolean userDaoFound = processUserDao(element, parserContext, shadowDaoHolderBuilder);
        boolean repositoryAclFound = processRepositoryAcl(element, parserContext, shadowDaoHolderBuilder);

        if (!userDaoFound || !repositoryAclFound) {
            processDaoHolder(element, parserContext, builder);
        } else {
            builder.addPropertyValue("daoHolder", shadowDaoHolderBuilder.getBeanDefinition());
        }

        List<BeanDefinition> listeners = new ManagedList<BeanDefinition>();
        listeners.addAll(getOwnListeners(element, parserContext, builder));
        builder.addPropertyValue("listeners", listeners);

        setupGlobalListeners(element, parserContext, builder);
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

    protected List<BeanDefinition> getOwnListeners(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        List<BeanDefinition> listeners = new ArrayList<BeanDefinition>();

        for (Element e : DomUtils.getChildElementsByTagName(element, ScumdNamespaceHandler.LISTENERS_ELEM)) {
            BeanDefinition b = parserContext.getDelegate().parseCustomElement(e, builder.getRawBeanDefinition());
            List<BeanDefinition> l = (List<BeanDefinition>) b.getPropertyValues().getPropertyValue("object").getValue();

            if (l != null && !l.isEmpty()) {
                listeners.addAll(l);
            }
        }

        return listeners;
    }

    protected void setupGlobalListeners(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        builder.addPropertyReference("globalListeners", ScumdNamespaceHandler.GLOBAL_LISTENERES_HOLDER_ID);

        try {
            parserContext.getRegistry().getBeanDefinition(ScumdNamespaceHandler.GLOBAL_LISTENERES_HOLDER_ID);
        } catch (NoSuchBeanDefinitionException e) {
            List<List<BeanDefinition>> registry = new ManagedList<List<BeanDefinition>>();
            BeanDefinitionBuilder b = BeanDefinitionBuilder.genericBeanDefinition(ObjectHolder.class);

            b.addPropertyValue("object", registry);
            parserContext.getRegistry().registerBeanDefinition(
                ScumdNamespaceHandler.GLOBAL_LISTENERES_HOLDER_ID, b.getRawBeanDefinition());
        }
//        try {
//            BeanDefinition registryHolder = parserContext.getRegistry().getBeanDefinition(ScumdNamespaceHandler.GLOBAL_LISTENERES_HOLDER_ID);
//            List<List<BeanDefinition>> registry = (List<List<BeanDefinition>>) registryHolder.getPropertyValues().getPropertyValue("object").getValue();
//            List<BeanDefinition> result = new ArrayList<BeanDefinition>();
//
//            for (List<BeanDefinition> definitionList : registry) {
//                if (definitionList != null && definitionList.size() > 0) {
//                    result.addAll(definitionList);
//                }
//            }
//
//            return result;
//        } catch (Exception e) {
//            return Collections.emptyList();
//        }
    }
}

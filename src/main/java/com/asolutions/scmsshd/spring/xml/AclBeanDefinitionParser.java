package com.asolutions.scmsshd.spring.xml;

import com.asolutions.scmsshd.dao.DaoHolder;
import com.asolutions.scmsshd.dao.impl.SimpleRepositoryAclDao;
import com.asolutions.scmsshd.model.security.Privilege;
import com.asolutions.scmsshd.model.security.RawRepositoryAcl;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import java.util.*;

/**
 * @author Oleg Ilyenko
 */
public class AclBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    public static final String REPOSITORY_ELEM = "repository";
    public static final String USERS_ELEM = "users";
    public static final String GROUPS_ELEM = "groups";

    public static final String PATH_ATTR = "path";
    public static final String ALLOW_ATTR = "allow";
    public static final String LIST_ATTR = "list";


    @Override
    protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) throws BeanDefinitionStoreException {
        if (parserContext.isNested()) {
            return null;
        }

        return StringUtils.hasText(element.getAttribute("id")) ? element.getAttribute("id") : ScumdNamespaceHandler.DEFAULT_ACL_ID;
    }

    @Override
    protected Class getBeanClass(Element element) {
        return SimpleRepositoryAclDao.class;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        List<RawRepositoryAcl> rawAcl = new ArrayList<RawRepositoryAcl>();

        for (Element repoElem : DomUtils.getChildElementsByTagName(element, REPOSITORY_ELEM)) {
            rawAcl.add(getAcl(repoElem));
        }

        builder.addConstructorArgValue(rawAcl);
        registerInDaoHolder(parserContext, element, builder.getBeanDefinition());
    }

    private void registerInDaoHolder(ParserContext parserContext, Element element, BeanDefinition beanDefinition) {
        if (!parserContext.isNested() && !StringUtils.hasText(element.getAttribute("id"))) {
            BeanDefinition daoHolder = null;
            
            try {
                daoHolder = parserContext.getRegistry().getBeanDefinition(ScumdNamespaceHandler.DEFAULT_DAO_HOLDER_ID);
            } catch (NoSuchBeanDefinitionException e) {
                daoHolder = BeanDefinitionBuilder.genericBeanDefinition(DaoHolder.class).getBeanDefinition();
                parserContext.getRegistry().registerBeanDefinition(ScumdNamespaceHandler.DEFAULT_DAO_HOLDER_ID, daoHolder);
            }

            daoHolder.getPropertyValues().addPropertyValue("repositoryAclDao", beanDefinition);
        }
    }
    
    private RawRepositoryAcl getAcl(Element element) {
        RawRepositoryAcl acl = new RawRepositoryAcl();
        acl.setPath(element.getAttribute(PATH_ATTR).trim());

        Map<Privilege, List<String>> users = new HashMap<Privilege, List<String>>();

        for (Element userElem : DomUtils.getChildElementsByTagName(element, USERS_ELEM)) {
            processAcl(userElem, users);
        }

        Map<Privilege, List<String>> groups = new HashMap<Privilege, List<String>>();

        for (Element groupElem : DomUtils.getChildElementsByTagName(element, GROUPS_ELEM)) {
            processAcl(groupElem, groups);
        }

        acl.setUserPrivileges(users);
        acl.setGroupPrivileges(groups);

        return acl;
    }

    private void processAcl(Element element, Map<Privilege, List<String>> entity) {
        for (Privilege p : getPrivileges(element)) {
            List<String> allowed = entity.get(p);

            if (allowed == null) {
                allowed = new ArrayList<String>();
                entity.put(p, allowed);
            }

            for (String a : getAllowed(element)) {
                if (!allowed.contains(a)) {
                    allowed.add(a);
                }
            }
        }
    }

    private List<Privilege> getPrivileges(Element element) {
        List<Privilege> privileges = new ArrayList<Privilege>();

        for (String p : element.getAttribute(ALLOW_ATTR).split("\\s*,\\s*")) {
            privileges.add(Privilege.valueOf(p));
        }

        return privileges;
    }

    private List<String> getAllowed(Element element) {
        String allowedString = element.getAttribute(LIST_ATTR);

        if (!StringUtils.hasText(allowedString)) {
            allowedString = DomUtils.getTextValue(element);
        }

        return Arrays.asList(allowedString.split("\\s*,\\s*"));
    }
}
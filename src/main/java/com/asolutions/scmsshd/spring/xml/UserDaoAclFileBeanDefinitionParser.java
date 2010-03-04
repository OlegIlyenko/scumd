package com.asolutions.scmsshd.spring.xml;

import com.asolutions.scmsshd.dao.DaoHolder;
import com.asolutions.scmsshd.dao.impl.SpringRepositoryAclDao;
import com.asolutions.scmsshd.dao.impl.SpringUserDao;
import com.asolutions.scmsshd.spring.helper.impl.DaoAwareAutoReloadableApplicationContextHelper;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

/**
 * @author Oleg Ilyenko
 */
public class UserDaoAclFileBeanDefinitionParser extends KeyPairBeanDefinitionParser {

    public static final String FILE_ATTR = "file";

    @Override
    protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) throws BeanDefinitionStoreException {
        if (parserContext.isNested()) {
            return null;
        }

        return StringUtils.hasText(element.getAttribute("id")) ? element.getAttribute("id") : ScumdNamespaceHandler.DEFAULT_DAO_HOLDER_ID;
    }

    @Override
    protected Class getBeanClass(Element element) {
        return DaoHolder.class;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        String file = element.getAttribute(FILE_ATTR);

        if (!StringUtils.hasText(file)) {
            file = DomUtils.getTextValue(element);
        }

        BeanDefinition helperBean = getHelperBeanDefinition(file);

        BeanDefinitionBuilder userDaoBuilder = BeanDefinitionBuilder.rootBeanDefinition(SpringUserDao.class);
        userDaoBuilder.addConstructorArgValue(helperBean);

        BeanDefinitionBuilder repositoryAclDaoBuilder = BeanDefinitionBuilder.rootBeanDefinition(SpringRepositoryAclDao.class);
        repositoryAclDaoBuilder.addConstructorArgValue(helperBean);

        builder.addPropertyValue("userDao", userDaoBuilder.getBeanDefinition());
        builder.addPropertyValue("repositoryAclDao", repositoryAclDaoBuilder.getBeanDefinition());
    }

    protected BeanDefinition getHelperBeanDefinition(String file) {
        BeanDefinitionBuilder helperBuilder = BeanDefinitionBuilder.genericBeanDefinition(DaoAwareAutoReloadableApplicationContextHelper.class);
        helperBuilder.addConstructorArgValue(file);
        return helperBuilder.getBeanDefinition();
    }
}
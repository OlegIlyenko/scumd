package com.asolutions.scmsshd.spring.xml;

import com.asolutions.scmsshd.dao.DaoHolder;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * @author Oleg Ilyenko
 */
public abstract class BaseUserDaoAclBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

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

}

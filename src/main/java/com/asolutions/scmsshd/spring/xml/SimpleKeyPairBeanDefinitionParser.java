package com.asolutions.scmsshd.spring.xml;

import com.asolutions.scmsshd.keyprovider.StringKeyPairProvider;
import com.asolutions.scmsshd.util.StringUtil;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import java.util.List;

/**
 * @author Oleg Ilyenko
 */
public class SimpleKeyPairBeanDefinitionParser extends KeyPairBeanDefinitionParser {

    public static final String KEY_ELEM = "key";

    @Override
    protected Class getBeanClass(Element element) {
        return StringKeyPairProvider.class;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        List list = new ManagedList();

        for (Element key : DomUtils.getChildElementsByTagName(element, KEY_ELEM)) {
            list.add(StringUtil.cleanString(DomUtils.getTextValue(key)));
        }

        builder.addPropertyValue("stringKeys", list);
    }
}
package com.asolutions.scmsshd.spring.xml;

import com.asolutions.scmsshd.keyprovider.DefaultKeyPairProvider;
import org.w3c.dom.Element;

/**
 * @author Oleg Ilyenko
 */
public class DefaultKeyPairBeanDefinitionParser extends KeyPairBeanDefinitionParser {

    @Override
    protected Class getBeanClass(Element element) {
        return DefaultKeyPairProvider.class;
    }
}
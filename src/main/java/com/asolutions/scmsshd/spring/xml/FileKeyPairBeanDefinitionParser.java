package com.asolutions.scmsshd.spring.xml;

import com.asolutions.scmsshd.keyprovider.FileKeyPairProvider;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import java.util.List;

/**
 * @author Oleg Ilyenko
 */
public class FileKeyPairBeanDefinitionParser extends KeyPairBeanDefinitionParser {

    public static final String KEY_FILE_ELEM = "key-file";

    public static final String PATH_ATTR = "path";

    @Override
    protected Class getBeanClass(Element element) {
        return FileKeyPairProvider.class;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        List list = new ManagedList();

        for (Element keyFile : DomUtils.getChildElementsByTagName(element, KEY_FILE_ELEM)) {
            String fileName = keyFile.getAttribute(PATH_ATTR);

            if (!StringUtils.hasText(fileName)) {
                fileName = DomUtils.getTextValue(keyFile);
            }

            list.add(fileName);
        }

        builder.addPropertyValue("fileKeys", list);
    }
}
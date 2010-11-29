package com.asolutions.scmsshd.spring.xml;

import com.asolutions.scmsshd.event.listener.impl.EmailSender;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * @author Oleg Ilyenko
 */
public class EmailSenderBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    public static final String DISABLE = "disable";
    public static final String HOST = "host";
    public static final String PORT = "port";
    public static final String PROTOCOL = "protocol";
    public static final String AUTH = "auth";
    public static final String USER = "user";
    public static final String PASSWORD = "password";
    public static final String FROM = "from";
    public static final String REPLAY_TO = "replay-to";
    public static final String FORCE_EMAIL = "force-email";

    @Override
    protected Class getBeanClass(Element element) {
        return EmailSender.class;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        setProperty(element, builder, "disable", DISABLE);
        setProperty(element, builder, "host", HOST);
        setProperty(element, builder, "port", PORT);
        setProperty(element, builder, "protocol", PROTOCOL);
        setProperty(element, builder, "auth", AUTH);
        setProperty(element, builder, "user", USER);
        setProperty(element, builder, "password", PASSWORD);
        setProperty(element, builder, "from", FROM);
        setProperty(element, builder, "replayTo", REPLAY_TO);
        setProperty(element, builder, "forceEmail", FORCE_EMAIL);
    }

    private void setProperty(Element element, BeanDefinitionBuilder builder, String name, String attr) {
        if (StringUtils.hasText(element.getAttribute(attr))) {
            builder.addPropertyValue(name, element.getAttribute(attr));
        }
    }

    @Override
    protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) throws BeanDefinitionStoreException {
        if (parserContext.isNested()) {
            throw new IllegalStateException("Email sender should be global and cannot be nested within other tags!");
        }

        return ScumdNamespaceHandler.EMAIL_SENDER_ID; 
    }
}

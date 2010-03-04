package com.asolutions.scmsshd.spring.xml;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * @author Oleg Ilyenko
 */
public class DbcpConnectionPoolBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    public static final String MAX_IDLE_ATTR = "max-idle";
    public static final String MIN_IDLE_ATTR = "min-idle";
    public static final String MAX_ACTIVE_ATTR = "max-active";
    public static final String MAX_WAIT_ATTR = "max-wait";
    public static final String MIN_EVICTABLE_IDLE_TIME_MILLIS_ATTR = "min-evictable-idle-time-millis";
    public static final String NUM_TESTS_PER_EVICTION_RUN_ATTR = "num-tests-per-eviction-run";
    public static final String MAX_OPEN_PREPARED_STATEMENTS_ATTR = "max-open-prepared-statements";

    @Override
    protected Class getBeanClass(Element element) {
        return BasicDataSource.class;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        addProperty(builder, "maxIdle", element.getAttribute(MAX_IDLE_ATTR));
        addProperty(builder, "minIdle", element.getAttribute(MIN_IDLE_ATTR));
        addProperty(builder, "maxActive", element.getAttribute(MAX_ACTIVE_ATTR));
        addProperty(builder, "maxWait", element.getAttribute(MAX_WAIT_ATTR));
        addProperty(builder, "minEvictableIdleTimeMillis", element.getAttribute(MIN_EVICTABLE_IDLE_TIME_MILLIS_ATTR));
        addProperty(builder, "numTestsPerEvictionRun", element.getAttribute(NUM_TESTS_PER_EVICTION_RUN_ATTR));
        addProperty(builder, "maxOpenPreparedStatements", element.getAttribute(MAX_OPEN_PREPARED_STATEMENTS_ATTR));
    }

    private void addProperty(BeanDefinitionBuilder builder, String propertyName, String value) {
        if (StringUtils.hasText(value)) {
            builder.addPropertyValue(propertyName, value);
        }
    }

}

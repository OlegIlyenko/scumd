package com.asolutions.scmsshd.spring.xml;

import com.asolutions.scmsshd.dao.impl.DatabaseRepositoryAclDao;
import com.asolutions.scmsshd.dao.impl.DatabaseUserDao;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Oleg Ilyenko
 */
public class DatabaseUserDaoAclBeanDefinitionParser extends BaseUserDaoAclBeanDefinitionParser {

    public static final String URL_ATTR = "url";
    public static final String USERNAME_ATTR = "username";
    public static final String PASSWORD_ATTR = "password";
    public static final String DRIVER_CLASS_ATTR = "driver-class";
    public static final String SQL_STATEMENTS_FILE_ATTR = "sql-statements-file";
    
    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        String url = element.getAttribute(URL_ATTR);
        String userName = element.getAttribute(USERNAME_ATTR);
        String password = element.getAttribute(PASSWORD_ATTR);
        String driverClass = element.getAttribute(DRIVER_CLASS_ATTR);
        String sqlStatementsFile = element.getAttribute(SQL_STATEMENTS_FILE_ATTR);
        
        BeanDefinition cpBean = processConnectionPool(element, parserContext, builder, url, userName, password, driverClass);

        BeanDefinitionBuilder jdbcTemplateBuilder = BeanDefinitionBuilder.rootBeanDefinition(NamedParameterJdbcTemplate.class);
        jdbcTemplateBuilder.addConstructorArgValue(cpBean);

        BeanDefinition jdbcTemplateBean = jdbcTemplateBuilder.getBeanDefinition();
        String jdbcTemplateBeanName = parserContext.getReaderContext().generateBeanName(jdbcTemplateBean);
        parserContext.getRegistry().registerBeanDefinition(jdbcTemplateBeanName, jdbcTemplateBean);

        BeanDefinitionBuilder userDaoBuilder = BeanDefinitionBuilder.rootBeanDefinition(DatabaseUserDao.class);
        initCommonProperties(userDaoBuilder, sqlStatementsFile, jdbcTemplateBeanName);

        BeanDefinitionBuilder repositoryAclDaoBuilder = BeanDefinitionBuilder.rootBeanDefinition(DatabaseRepositoryAclDao.class);
        initCommonProperties(repositoryAclDaoBuilder, sqlStatementsFile, jdbcTemplateBeanName);

        builder.addPropertyValue("userDao", userDaoBuilder.getBeanDefinition());
        builder.addPropertyValue("repositoryAclDao", repositoryAclDaoBuilder.getBeanDefinition());
    }
    
    private BeanDefinition processConnectionPool(Element element, ParserContext parserContext, BeanDefinitionBuilder builder, String url, String userName, String password, String driverClass) {
        Element connectionPoolElement = getFirstChildElement(element);
        BeanDefinition connectionPoolBean = parserContext.getDelegate().parseCustomElement(connectionPoolElement, builder.getRawBeanDefinition());

        connectionPoolBean.getPropertyValues().addPropertyValue("url", url);
        connectionPoolBean.getPropertyValues().addPropertyValue("username", userName);
        connectionPoolBean.getPropertyValues().addPropertyValue("password", password);
        connectionPoolBean.getPropertyValues().addPropertyValue("driverClassName", driverClass);

        return connectionPoolBean;
    }
    
    private void initCommonProperties(BeanDefinitionBuilder builder, String sqlStatementsFile, String jdbcTemplateBeanName) {
        if (StringUtils.hasText(sqlStatementsFile)) {
            builder.addPropertyValue("sqlStatementsFile", sqlStatementsFile);
        }
        
        builder.addPropertyReference("jdbcTemplate", jdbcTemplateBeanName);
    }
    
    private Element getFirstChildElement(Element element) {
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node instanceof Element) {
                return (Element) node;
            }
        }

        return null;
    }

}
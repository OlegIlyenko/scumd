package com.asolutions.scmsshd.spring.xml;

import com.asolutions.scmsshd.dao.DaoHolder;
import com.asolutions.scmsshd.dao.impl.SimpleUserDao;
import com.asolutions.scmsshd.model.security.*;
import com.asolutions.scmsshd.util.StringUtil;
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

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Oleg Ilyenko
 */
public class SimpleUserDaoBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    public static final String GROUP_ELEM = "group";
    public static final String USER_ELEM = "user";
    public static final String PASSWORD_ELEM = "password";
    public static final String PUBLIC_KEY_ELEM = "public-key";

    public static final String NAME_ATTR = "name";
    public static final String EXPIRE_ATTR = "expire";
    public static final String ACTIVE_ATTR = "active";
    public static final String GROUPS_ATTR = "groups";
    public static final String PASSWORD_ATTR = "password";
    public static final String CHECKSUM_ATTR = "checksum";
    public static final String FILE_ATTR = "file";

    public static final DateFormat XML_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) throws BeanDefinitionStoreException {
        if (parserContext.isNested()) {
            return null;
        }

        return StringUtils.hasText(element.getAttribute("id")) ? element.getAttribute("id") : ScumdNamespaceHandler.DEFAULT_USER_DAO_ID;
    }

    @Override
    protected Class getBeanClass(Element element) {
        return SimpleUserDao.class;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        List<Group> groups = findGroups(element);
        List<User> users = findUsers(element, groups, null);

        for (Element groupElem : DomUtils.getChildElementsByTagName(element, GROUP_ELEM)) {
            String defaultGroupName = groupElem.getAttribute(NAME_ATTR).trim();
            Group defaultGroup = findGroupByName(groups, defaultGroupName);

            users.addAll(findUsers(groupElem, groups, defaultGroup));
        }

        builder.addConstructorArgValue(users);
        builder.addConstructorArgValue(groups);
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

            daoHolder.getPropertyValues().addPropertyValue("userDao", beanDefinition);
        }
    }

    private List<Group> findGroups(Element element) {
        List<Group> groups = new ArrayList<Group>();

        for (Element groupElem : DomUtils.getChildElementsByTagName(element, GROUP_ELEM)) {
            Group group = new Group();
            group.setName(groupElem.getAttribute(NAME_ATTR).trim());

            groups.add(group);
        }

        return groups;
    }

    private List<User> findUsers(Element element, List<Group> availableGroups, Group defaultGroup) {
        List<User> users = new ArrayList<User>();

        for (Element userElem : DomUtils.getChildElementsByTagName(element, USER_ELEM)) {
            users.add(getUser(userElem, availableGroups, defaultGroup));
        }

        return users;
    }

    private User getUser(Element element, List<Group> availableGroups, Group defaultGroup) {
        User user = new User();
        user.setName(element.getAttribute(NAME_ATTR).trim());

        if (StringUtils.hasText(element.getAttribute(EXPIRE_ATTR))) {
            user.setExpirationDate(parseDate(element.getAttribute(EXPIRE_ATTR)));
        }

        if (StringUtils.hasText(element.getAttribute(ACTIVE_ATTR))) {
            user.setActive(Boolean.valueOf(element.getAttribute(ACTIVE_ATTR)));
        }

        List<Group> userGroups = new ArrayList<Group>();
        if (defaultGroup != null) {
            userGroups.add(defaultGroup);
        }

        if (StringUtils.hasText(element.getAttribute(GROUPS_ATTR))) {
            for (String g : element.getAttribute(GROUPS_ATTR).split("\\s*,\\s*")) {
                if (findGroupByName(userGroups, g) == null) {
                    Group availableGroup = findGroupByName(availableGroups, g);

                    if (availableGroup == null) {
                        throw new IllegalStateException("Unknown group with name " + g);
                    }
                    userGroups.add(availableGroup);
                }
            }
        }

        user.setGroups(userGroups);

        AuthPolicy policy = getAuthPolicy(element);

        if (policy == null) {
            throw new IllegalStateException("No auth policy found for user: " + user.getName());
        }

        user.setAuthPolicy(policy);

        return user;
    }

    private AuthPolicy getAuthPolicy(Element userElem) {
        Element passwordElem = DomUtils.getChildElementByTagName(userElem, PASSWORD_ELEM);

        if (passwordElem != null) {
            PasswordAuthPolicy policy = new PasswordAuthPolicy();
            policy.setPassword(passwordElem.getAttribute(PASSWORD_ATTR).trim());

            if (StringUtils.hasText(passwordElem.getAttribute(CHECKSUM_ATTR))) {
                policy.setEncodingAlgorithm(PasswordAuthPolicy.EncodingAlgorithm.valueOf(passwordElem.getAttribute(CHECKSUM_ATTR)));
            }

            return policy;
        }

        Element publicKeyElem = DomUtils.getChildElementByTagName(userElem, PUBLIC_KEY_ELEM);

        if (publicKeyElem != null) {
            PublicKeyAuthPolicy policy = new PublicKeyAuthPolicy();

            if (StringUtils.hasText(publicKeyElem.getAttribute(FILE_ATTR))) {
                policy.setPublicKeyAsString(readFile(publicKeyElem.getAttribute(FILE_ATTR)));
            } else {
                policy.setPublicKeyAsString(StringUtil.cleanString(DomUtils.getTextValue(publicKeyElem)));
            }

            return policy;
        }

        if (StringUtils.hasText(userElem.getAttribute(PASSWORD_ATTR))) {
            PasswordAuthPolicy policy = new PasswordAuthPolicy();
            policy.setPassword(userElem.getAttribute(PASSWORD_ATTR).trim());
            return policy;
        }

        return null;
    }

    private String readFile(String path) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            try {
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line).append("\n");
                }

                return result.toString().trim();
            } finally {
                reader.close();
            }
        } catch (Exception e) {
            throw new IllegalStateException("File not found: " + path, e);
        }
    }

    private Group findGroupByName(List<Group> groups, String name) {
        for (Group group : groups) {
            if (group.getName().equals(name)) {
                return group;
            }
        }

        return null;
    }

    private Date parseDate(String stringDate) {
        try {
            return XML_DATE_FORMAT.parse(stringDate);
        } catch (ParseException e) {
            throw new IllegalStateException("Wrong date format: " + stringDate, e);
        }
    }
}
package com.asolutions.scmsshd.spring.xml;

import com.asolutions.scmsshd.event.Event;
import com.asolutions.scmsshd.event.PushEvent;
import com.asolutions.scmsshd.event.UserEvent;
import com.asolutions.scmsshd.event.listener.DefaultUncheckedListener;
import com.asolutions.scmsshd.event.listener.Support;
import com.asolutions.scmsshd.event.listener.SupportBuilder;
import com.asolutions.scmsshd.event.listener.impl.*;
import com.asolutions.scmsshd.util.Function1;
import com.asolutions.scmsshd.util.Tuple;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.asolutions.scmsshd.event.listener.EventDispatcher.Stage.Post;
import static com.asolutions.scmsshd.event.listener.EventDispatcher.Stage.Pre;
import static com.asolutions.scmsshd.event.listener.SupportBuilder.ALWAYS_SUPPORTS;
import static com.asolutions.scmsshd.event.listener.SupportBuilder.builder;

/**
 * @author Oleg Ilyenko
 */
public class ListenersBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    // support
    public static final String NOT_ELEM = "not";
    public static final String REPO_ELEM = "repo";
    public static final String FILE_ELEM = "file";
    public static final String USER_ELEM = "user";
    public static final String GROUP_ELEM = "group";
    public static final String AUTHOR_ELEM = "author";

    public static final String INCLUDES = "includes";
    public static final String EXCLUDES = "excludes";

    // listener
    public static final String EMAIL_ELEM = "email";
    public static final String PUSH_LOG_ELEM = "push-log";
    public static final String ACCESS_LOG_ELEM = "access-log";
    public static final String CANCEL_ELEM = "cancel";

    public static final String SUBJECT = "subject";
    public static final String EMAILS = "emails";
    public static final String USERS = "users";
    public static final String GROUPS = "groups";
    public static final String AUTHORS = "authors";

    public static final String EVENT_ATTR = "event";
    public static final String EVENT_CLASS_ATTR = "event-class";

    public static final String PATH = "path";
    public static final String MAX_FILE_SIZE = "max-file-size";
    public static final String MAX_BACKUP_INDEX = "max-backup-index";
    public static final String PATTERN = "pattern";

    @Override
    protected Class getBeanClass(Element element) {
        return ObjectHolder.class;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        Support support = getSupport(element);
        List<Tuple<Function1<Event, Object>, Support>> listeners = getListeners(element);
        List<BeanDefinition> resultListeners = new ManagedList<BeanDefinition>();

        resultListeners.addAll(getBeanListeners(element, parserContext, builder, support));

        for (Tuple<Function1<Event, Object>, Support> t : listeners) {
            SupportBuilder b = builder();

            if (t.getB() != null) {
                b.support(t.getB());
            }

            if (support != null) {
                b.support(support);
            }

            BeanDefinitionBuilder listener = BeanDefinitionBuilder.genericBeanDefinition(DefaultUncheckedListener.class);

            listener.addConstructorArgValue(b.hasSupports() ? b.build() : ALWAYS_SUPPORTS);
            listener.addConstructorArgValue(t.getA());

            resultListeners.add(listener.getRawBeanDefinition());
        }

        builder.addPropertyValue("object", resultListeners);

        if (!parserContext.isNested()) {
            registerAsGlobal(resultListeners, parserContext);
        }
    }

    private void registerAsGlobal(List<BeanDefinition> resultListeners, ParserContext parserContext) {
        BeanDefinition registryHolder;
        List<List<BeanDefinition>> registry;

        try {
            registryHolder = parserContext.getRegistry().getBeanDefinition(ScumdNamespaceHandler.GLOBAL_LISTENERES_HOLDER_ID);
            registry = (List<List<BeanDefinition>>) registryHolder.getPropertyValues().getPropertyValue("object").getValue();
        } catch (NoSuchBeanDefinitionException e) {
            registry = new ManagedList<List<BeanDefinition>>();
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ObjectHolder.class);

            builder.addPropertyValue("object", registry);
            parserContext.getRegistry().registerBeanDefinition(
                    ScumdNamespaceHandler.GLOBAL_LISTENERES_HOLDER_ID, builder.getRawBeanDefinition());
        }

        registry.add(resultListeners);
    }

    private Collection<? extends BeanDefinition> getBeanListeners(Element element, ParserContext parserContext,
                                                                 BeanDefinitionBuilder builder, Support support) {
        List<BeanDefinition> beanDefinitions = new ArrayList<BeanDefinition>();

        for (Element e : DomUtils.getChildElementsByTagName(element, "bean")) {
            BeanDefinitionHolder bean = parserContext.getDelegate().parseBeanDefinitionElement(e);
            BeanDefinitionBuilder listener = BeanDefinitionBuilder.genericBeanDefinition(DefaultUncheckedListener.class);

            listener.addConstructorArgValue(support);
            listener.addConstructorArgValue(bean);

            beanDefinitions.add(listener.getRawBeanDefinition());
        }

        return beanDefinitions;
    }

    @SuppressWarnings("unchecked") // after several hours struggling with generics :(
    private <T> List<Tuple<T, Support>> getListeners(Element element) {
        return mapChildElements(element, new Function1<Element, Tuple<T, Support>>() {
            public Tuple<T, Support> apply(Element e) {
                if (e.getLocalName().equals(EMAIL_ELEM)) {
                    return (Tuple<T, Support>) email(e);
                } else if (e.getLocalName().equals(PUSH_LOG_ELEM)) {
                    return (Tuple<T, Support>) pushLog(e);
                } else if (e.getLocalName().equals(ACCESS_LOG_ELEM)) {
                    return (Tuple<T, Support>) accessLog(e);
                } else if (e.getLocalName().equals(CANCEL_ELEM)) {
                    return (Tuple<T, Support>) cancel(e);
                } else {
                    return null;
                }
            }

            private Tuple<AbstractBeanDefinition, Support> email(Element e) {
                BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(EmailListener.class);
                builder.addPropertyValue("subject", getAgrOrElem(e, SUBJECT));
                builder.addPropertyValue("users", getAgrOrElemList(e, USERS));
                builder.addPropertyValue("emails", getAgrOrElemList(e, EMAILS));
                builder.addPropertyValue("groups", getAgrOrElemList(e, GROUPS));
                builder.addPropertyReference("emailSender", ScumdNamespaceHandler.EMAIL_SENDER_ID);

                return Tuple.valueOf(
                        builder.getRawBeanDefinition(),
                        builder().stage(Post)
                                .eventClass(EmailEventTypes.valueOf(e.getAttribute(EVENT_ATTR)).getClazz())
                                .build());
            }

            private <T extends  Event> Tuple<CancelListener<T>, Support> cancel(Element e) {
                String message = DomUtils.getTextValue(e);

                if (StringUtils.hasText(message)) {
                    message = message.trim();
                } else {
                    message = null;
                }

                SupportBuilder b = builder().stage(Pre);

                if (StringUtils.hasText(e.getAttribute(EVENT_ATTR))) {
                    b.eventClass(CancelEventTypes.valueOf(e.getAttribute(EVENT_ATTR)).getClazz());
                } else if (StringUtils.hasText(e.getAttribute(EVENT_CLASS_ATTR))) {
                    try {
                        b.eventClass((Class<Event>) Class.forName(e.getAttribute(EVENT_CLASS_ATTR)));
                    } catch (ClassNotFoundException e1) {
                        throw new IllegalStateException("Unknown class: " + e.getAttribute(EVENT_CLASS_ATTR), e1);
                    }
                }

                return Tuple.valueOf(new CancelListener<T>(message), b.hasSupports() ? b.build() : null);
            }

            private Tuple<PushLogListener, Support> pushLog(Element e) {
                return Tuple.valueOf(
                        setupLogListener(new PushLogListener(), e),
                        builder().stage(Post).eventClass(PushEvent.class).build());
            }

            private Tuple<AccessLogListener, Support> accessLog(Element e) {
                return Tuple.valueOf(
                        setupLogListener(new AccessLogListener(), e),
                        builder().stage(Post)
                                .eventClass(UserEvent.class)
                                .build());
            }

            private <T extends BaseLogListener> T setupLogListener(T listener, Element e) {
                String path = e.getAttribute(PATH);
                String maxFileSize = e.getAttribute(MAX_FILE_SIZE);
                String maxBackupIndex = e.getAttribute(MAX_BACKUP_INDEX);
                String pattern = e.getAttribute(PATTERN);

                if (StringUtils.hasLength(path)) {
                    listener.setPath(path);
                }

                if (StringUtils.hasLength(maxFileSize)) {
                    listener.setMaxFileSize(maxFileSize);
                }

                if (StringUtils.hasLength(maxBackupIndex)) {
                    listener.setMaxBackupIndex(Integer.valueOf(maxBackupIndex));
                }

                if (StringUtils.hasLength(pattern)) {
                    listener.setPattern(pattern);
                }

                listener.setupLogger();

                return listener;
            }
        });
    }

    private Support getSupport(Element element) {
        List<Support> supports = mapChildElements(element, new Function1<Element, Support>() {
            public Support apply(Element e) {
                if (e.getLocalName().equals(NOT_ELEM)) {
                    return not(e);
                } else if (e.getLocalName().equals(REPO_ELEM)) {
                    return repo(e);
                } else if (e.getLocalName().equals(FILE_ELEM)) {
                    return file(e);
                } else if (e.getLocalName().equals(USER_ELEM)) {
                    return user(e);
                } else if (e.getLocalName().equals(GROUP_ELEM)) {
                    return group(e);
                } else if (e.getLocalName().equals(AUTHOR_ELEM)) {
                    return author(e);
                } else {
                    return null;
                }
            }

            private Support repo(Element e) {
                return builder().repo(getAgrOrElem(e, INCLUDES), getAgrOrElem(e, EXCLUDES)).build();
            }

            private Support file(Element e) {
                return builder().file(getAgrOrElem(e, INCLUDES), getAgrOrElem(e, EXCLUDES)).build();
            }

            private Support user(Element e) {
                List<String> users = getTextList(e);

                if (!users.isEmpty()) {
                    SupportBuilder b = builder();

                    for (String s : users) {
                        b.user(s);
                    }

                    return b.build();
                } else {
                    return null;
                }
            }

            private Support group(Element e) {
                List<String> groups = getTextList(e);

                if (!groups.isEmpty()) {
                    SupportBuilder b = builder();

                    for (String s : groups) {
                        b.group(s);
                    }

                    return b.build();
                } else {
                    return null;
                }
            }

            private Support author(Element e) {
                List<String> authors = getTextList(e);

                if (!authors.isEmpty()) {
                    SupportBuilder b = builder();

                    for (String s : authors) {
                        b.author(s);
                    }

                    return b.build();
                } else {
                    return null;
                }
            }

            private Support not(Element e) {
                Support notSupports = getSupport(e);

                return notSupports != null ? builder().not().support(notSupports).build() : null;
            }
        });

        return supports != null ? new SupportBuilder.AggregateSupport(supports) : null;
    }

    private List<String> getAgrOrElemList(Element e, String name) {
        List<String> result = new ArrayList<String>();
        String text = getAgrOrElem(e, name);

        if (text != null) {
            result.addAll(Arrays.asList(text.trim().split("\\s*,\\s*")));
        }

        return result;
    }

    private List<String> getTextList(Element e) {
        List<String> result = new ArrayList<String>();
        String text = DomUtils.getTextValue(e);

        if (StringUtils.hasLength(text)) {
            result.addAll(Arrays.asList(text.split("\\s*,\\s*")));
        }

        return result;
    }

    private String getAgrOrElem(Element e, String name) {
        if (StringUtils.hasLength(e.getAttribute(name))) {
            return e.getAttribute(name).trim();
        } else {
            Element maybeElem = DomUtils.getChildElementByTagName(e, name);

            if (maybeElem != null) {
                return DomUtils.getTextValue(maybeElem);
            } else {
                return null;
            }
        }
    }

    private <T> List<T> mapChildElements(Element element, Function1<Element, T> fn) {
        List<T> result = new ArrayList<T>();
        NodeList children = element.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);

            if (node instanceof Element) {
                T res = fn.apply((Element) node);

                if (res != null) {
                    result.add(res);
                }
            }
        }

        return result;
    }

    @Override
    protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) throws BeanDefinitionStoreException {
        if (parserContext.isNested()) {
            return null;
        }

        return parserContext.getReaderContext().generateBeanName(definition); 
    }
}

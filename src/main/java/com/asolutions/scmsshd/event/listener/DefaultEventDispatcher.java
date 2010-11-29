package com.asolutions.scmsshd.event.listener;

import com.asolutions.scmsshd.event.CancelEventException;
import com.asolutions.scmsshd.event.Event;
import com.asolutions.scmsshd.util.Function1;
import com.asolutions.scmsshd.util.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static com.asolutions.scmsshd.event.listener.SupportBuilder.builder;

/**
 * @author Oleg Ilyenko
 */
public class DefaultEventDispatcher implements EventDispatcher {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private final List<ListenerInfo> listeners = new ArrayList<ListenerInfo>();

    @Override
    public void fireEvent(Event event) {
        fireEvent(Stage.Post, event);
    }

    @Override
    public void fireEvent(Stage stage, Event event) {
        List<CancelEventException> cancelExceptions = new ArrayList<CancelEventException>();

        for (ListenerInfo info : listeners) {
            CancelEventException e = fireEvent(stage, event, info);

            if (e != null) {
                cancelExceptions.add(e);
            }
        }

        precessCancelExceptions(cancelExceptions);
    }

    private CancelEventException fireEvent(Stage stage, Event event, ListenerInfo info) {
        try {
            if (info.getSupports().supports(stage, event)) {
                try {
                    info.invoke(event);
                } catch (CancelEventException e) {
                    String contextInfo = formatContextInfo(stage, event, info) + "Reason: " + e.getMessage();
                    if (stage == Stage.Pre) {
                        e.setContextInfo(contextInfo);
                        return e;
                    } else {
                        log.warn("Cancel exception caught from the listener, but post events cannot be cancelled! \n" + contextInfo);
                    }
                } catch (Exception e) {
                    log.warn("Exception during listener call! \n" + formatContextInfo(stage, event, info), e);
                }
            }
        } catch (Exception e) {
            log.warn("Some problem with Support", e);
        }

        return null;
    }

    private String formatContextInfo(Stage stage, Event event, ListenerInfo info) {
        return "Event: [" + stage + "] " + event + "\n" + info + "\n";
    }

    private void precessCancelExceptions(List<CancelEventException> cancelExceptions) {
        if (!cancelExceptions.isEmpty()) {
            if (cancelExceptions.size() == 1) {
                throw cancelExceptions.get(0);
            } else {
                throw mergeCancelExceptions(cancelExceptions);
            }
        }
    }

    private CancelEventException mergeCancelExceptions(List<CancelEventException> cancelExceptions) {
        StringBuilder sb = new StringBuilder("Several cancel reasons:\n");

        for (CancelEventException exception : cancelExceptions) {
            sb.append(exception.getMessage()).append("\n");
        }

        return new CancelEventException(sb.toString());
    }

    @Override
    public void addListener(Object... listener) {
        for (Object l : listener) {
            if (!contains(l)) {
                listeners.addAll(getListenerInfo(l));
            }
        }
    }

    @Override
    public void removeListener(Object... listener) {
        for (Object listenerToRemove : listener) {
            Iterator<ListenerInfo> it = listeners.iterator();

            while (it.hasNext()) {
                ListenerInfo info = it.next();

                if (info.isSame(listenerToRemove)) {
                    it.remove();
                    break;
                }
            }
        }
    }

    private List<ListenerInfo> getListenerInfo(Object l) {
        if (l instanceof UncheckedListener) {
            UncheckedListener uncheckedListener = (UncheckedListener) l;

            if (uncheckedListener.getListener() instanceof Function1) {
                return Arrays.asList((ListenerInfo) new UncheckedListenerInfo((UncheckedListener) l));
            } else {
                return getReflectionListenerInfo(uncheckedListener.getListener(), uncheckedListener.getSupport());
            }
        } else {
            return getReflectionListenerInfo(l, null);
        }
    }

    @SuppressWarnings("unchecked")
    private List<ListenerInfo> getReflectionListenerInfo(Object l, Support extraSupport) {
        List<Method> listenerMethods = getListenerMethods(l);
        Support support = getSupport(l);
        List<ListenerInfo> result = new ArrayList<ListenerInfo>();

        for (Method method : listenerMethods) {
            result.add(new ReflectionListenerInfo(
                l, method,
                builder()
                    .eventClass((Class<Event>) method.getParameterTypes()[0])
                    .stage(method.getParameterAnnotations()[0])
                    .support(support)
                    .support(extraSupport)
                    .build()
            ));
        }

        return result;
    }

    private List<Method> getListenerMethods(Object l) {
        List<Method> listenerMethods = new ArrayList<Method>();

        for (Method method : l.getClass().getMethods()) {
            if (isListenerMethod(method)) {
                listenerMethods.add(method);
            }
        }

        return listenerMethods;
    }

    private boolean isListenerMethod(Method method) {
        return method.getParameterTypes().length == 1 &&
                Event.class.isAssignableFrom(method.getParameterTypes()[0]) &&
                method.getReturnType().equals(Void.TYPE) &&
                Modifier.isPublic(method.getModifiers());
    }

    private Support getSupport(Object l) {
        for (Method method : l.getClass().getMethods()) {
            if (isSupportMethod(method)) {
                return invoke(method, l);
            } else if (isSupportBuilderMethod(method)) {
                return ((SupportBuilder) invoke(method, l)).build();
            }
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private <T> T invoke(Method method, Object l, Object... args) {
        if (!Modifier.isPublic(method.getModifiers())) {
            return null;
        }

        boolean isAccessible = method.isAccessible();

        try {
            if (!isAccessible) {
                method.setAccessible(true);
            }

            return (T) method.invoke(l, args);
        } catch (InvocationTargetException e) {
            if (e.getTargetException() != null &&
                    CancelEventException.class.isAssignableFrom(e.getTargetException().getClass())) {
                throw (CancelEventException) e.getTargetException();
            } else {
                throw new IllegalStateException(e);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            if (!isAccessible) {
                method.setAccessible(isAccessible);
            }
        }
    }

    private boolean isSupportMethod(Method method) {
        return method.getParameterTypes().length == 0 &&
                Support.class.isAssignableFrom(method.getReturnType()) &&
                Modifier.isPublic(method.getModifiers());
    }

    private boolean isSupportBuilderMethod(Method method) {
        return method.getParameterTypes().length == 0 &&
                SupportBuilder.class.isAssignableFrom(method.getReturnType()) &&
                Modifier.isPublic(method.getModifiers());
    }

    private boolean contains(Object listener) {
        for (ListenerInfo info : listeners) {
            if (info.isSame(listener)) {
                return true;
            }
        }

        return false;
    }

    private static interface ListenerInfo {
        Support getSupports();
        void invoke(Event event);
        boolean isSame(Object listener);
    }

    private static class UncheckedListenerInfo implements ListenerInfo {

        private UncheckedListener listener;

        private UncheckedListenerInfo(UncheckedListener listener) {
            this.listener = listener;
        }

        public Support getSupports() {
            return listener.getSupport();
        }

        @SuppressWarnings("unchecked")
        public void invoke(Event event) {
            ((Function1<Event, Object>) listener.getListener()).apply(event);
        }

        @Override
        public String toString() {
            return "Unchecked Listener: " + listener.getListener().getClass().getName();
        }

        @Override
        public boolean isSame(Object listener) {
            return this.listener.equals(listener);
        }
    }

    private static class ReflectionListenerInfo implements ListenerInfo {
        private final Object listener;
        private final Method method;
        private final Support supports;

        private ReflectionListenerInfo(Object listener, Method method, Support supports) {
            this.listener = listener;
            this.method = method;
            this.supports = supports;
        }

        public Object getListener() {
            return listener;
        }

        public Method getMethod() {
            return method;
        }

        public Support getSupports() {
            return supports;
        }

        public void invoke(Event event) {
            ReflectionUtil.invoke(getMethod(), getListener(), event);
        }

        @Override
        public String toString() {
            return "Listener: " + getListener().getClass().getName() + "\n" +
                    "Method: " + getMethod();
        }

        @Override
        public boolean isSame(Object listener) {
            return this.listener.equals(listener);
        }
    }
}

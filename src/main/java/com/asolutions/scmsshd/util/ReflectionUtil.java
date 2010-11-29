package com.asolutions.scmsshd.util;

import com.asolutions.scmsshd.event.CancelEventException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * @author Oleg Ilyenko
 */
public class ReflectionUtil {

    @SuppressWarnings("unchecked")
    public static <T> T invoke(Method method, Object l, Object... args) {
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

}

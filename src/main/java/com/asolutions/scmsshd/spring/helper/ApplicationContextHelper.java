package com.asolutions.scmsshd.spring.helper;

/**
 * @author Oleg Ilyenko
 */
public interface ApplicationContextHelper {

    <T> T getBean(Class<T> desiredClass);

}

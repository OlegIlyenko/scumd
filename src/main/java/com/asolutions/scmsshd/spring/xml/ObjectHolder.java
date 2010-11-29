package com.asolutions.scmsshd.spring.xml;

/**
 * @author Oleg Ilyenko
 */
public class ObjectHolder<T> {
    private T object;

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }
}

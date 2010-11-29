package com.asolutions.scmsshd.event.listener.impl;

import com.asolutions.scmsshd.event.CancelEventException;
import com.asolutions.scmsshd.event.Event;
import com.asolutions.scmsshd.util.Function1;

/**
 * @author Oleg Ilyenko
 */
public class CancelListener<T extends Event> implements Function1<T, Object> {

    private final String message;

    public CancelListener(String message) {
        this.message = message;
    }

    @Override
    public Object apply(T parameter1) {
        throw new CancelEventException(message);
    }
}

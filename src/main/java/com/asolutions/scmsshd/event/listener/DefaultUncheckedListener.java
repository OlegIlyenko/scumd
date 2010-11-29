package com.asolutions.scmsshd.event.listener;

/**
 * @author Oleg Ilyenko
 */
public class DefaultUncheckedListener implements UncheckedListener {

    private final Support support;

    private final Object listener;

    public DefaultUncheckedListener(Support support, Object listener) {
        this.support = support;
        this.listener = listener;
    }

    @Override
    public Support getSupport() {
        return support;
    }

    @Override
    public Object getListener() {
        return listener;
    }
}

package com.asolutions.scmsshd.model.security;

/**
 * @author Oleg Ilyenko
 */
public final class NoAuth implements AuthPolicy {

    private static final NoAuth INSTANCE = new NoAuth();

    private NoAuth() {}

    public static NoAuth get() {
        return INSTANCE;
    }
}

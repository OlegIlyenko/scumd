package com.asolutions.scmsshd;

import org.apache.sshd.common.Session;

/**
 * @author Oleg Ilyenko
 */
public class InteractionContextKey extends Session.AttributeKey<InteractionContext> {

    private static final InteractionContextKey INSTANCE = new InteractionContextKey();

    public static InteractionContextKey get() {
        return INSTANCE;
    }

}

package com.asolutions.scmsshd.util;

/**
 * @author Oleg Ilyenko
 */
public interface Function2<P1, P2, R> {
    R apply(P1 parameter1, P2 parameter2);
}

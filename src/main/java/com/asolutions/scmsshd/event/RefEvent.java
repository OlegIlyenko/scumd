package com.asolutions.scmsshd.event;

/**
 * @author Oleg Ilyenko
 */
public interface RefEvent {

    enum Type {Create, Delete, Update}

    String getRefName();

    Type getType();
}

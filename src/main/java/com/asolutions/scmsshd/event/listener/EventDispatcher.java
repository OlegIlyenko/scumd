package com.asolutions.scmsshd.event.listener;

import com.asolutions.scmsshd.event.Event;

/**
 * @author Oleg Ilyenko
 */
public interface EventDispatcher {

    enum Stage {Pre, Post}


    void fireEvent(Event event);

    void fireEvent(Stage stage, Event event);

    void addListener(Object... listener);

    void removeListener(Object... listener);

}

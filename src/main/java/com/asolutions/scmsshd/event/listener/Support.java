package com.asolutions.scmsshd.event.listener;

import com.asolutions.scmsshd.event.Event;

import static com.asolutions.scmsshd.event.listener.EventDispatcher.Stage;

/**
 * @author Oleg Ilyenko
 */
public interface Support {

    boolean supports(Stage stage, Event event);

}

package com.asolutions.scmsshd.event;

/**
 * Can be thrown within event body and will stop action that takes place.
 * You throw it from methods annotated with {@link com.asolutions.scmsshd.event.Pre}
 *
 * @author Oleg Ilyenko
 */
public class CancelEventException extends RuntimeException {

    private String contextInfo;

    public CancelEventException() {
        super();
    }

    public CancelEventException(String message) {
        super(message);
    }

    public String getContextInfo() {
        return contextInfo;
    }

    public void setContextInfo(String contextInfo) {
        this.contextInfo = contextInfo;
    }
}

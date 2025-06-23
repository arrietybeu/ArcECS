package org.arc.utils.reflect;

public class ReflectionException extends Exception {

    private static final long serialVersionUID = -7146287043138864498L;

    public ReflectionException() {
        super();
    }

    public ReflectionException(String message) {
        super(message);
    }

    public ReflectionException(Throwable cause) {
        super(cause);
    }

    public ReflectionException(String message, Throwable cause) {
        super(message, cause);
    }

}

package org.eclipse.persistence.buildtools.helper;

public class VersionException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private Throwable cause;

    public VersionException() {
        super();
    }

    public VersionException(String message) {
        super(message);
    }

    public VersionException(Throwable cause) {
        super(cause.toString());
        this.cause = cause;
    }

    public VersionException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }
    
    public Throwable getException() {
        return cause;
    }

    public Throwable getCause() {
        return getException();
    }

    public void printStackTrace() {
        printStackTrace(System.err);
    }
}

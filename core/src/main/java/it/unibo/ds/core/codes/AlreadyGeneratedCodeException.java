package it.unibo.ds.core.codes;

public class AlreadyGeneratedCodeException extends Exception {

    public AlreadyGeneratedCodeException(final String message) {
        super(message);
    }

    public AlreadyGeneratedCodeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public AlreadyGeneratedCodeException(final Throwable cause) {
        super(cause);
    }
}

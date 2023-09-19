package it.unibo.ds.core.codes;

public class AlreadyConsumedCodeException extends Exception {

    public AlreadyConsumedCodeException(final String message) {
        super(message);
    }

    public AlreadyConsumedCodeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public AlreadyConsumedCodeException(final Throwable cause) {
        super(cause);
    }
}

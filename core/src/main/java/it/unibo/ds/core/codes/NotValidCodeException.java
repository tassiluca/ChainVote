package it.unibo.ds.core.codes;

public class NotValidCodeException extends Exception {

    public NotValidCodeException(final String message) {
        super(message);
    }

    public NotValidCodeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public NotValidCodeException(final Throwable cause) {
        super(cause);
    }
}

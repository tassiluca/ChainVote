package it.unibo.ds.core.codes;

/**
 * Signals that the {@link OneTimeCode} is not correct.
 */
public class IncorrectCodeException extends Exception {

    /**
     * Constructs the exception with the specified detail message.
     * @param message the String that contains a detailed message
     */
    public IncorrectCodeException(final String message) {
        super(message);
    }

    /**
     * Constructs the exception with the specified detail message and cause.
     * @param message the String that contains a detailed message
     * @param cause the cause (which is saved for later retrieval by the {@link Throwable#getCause()} method).
     *              (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public IncorrectCodeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause and a detail message of
     * {@code (cause==null ? null : cause.toString())}
     * @param cause the cause (which is saved for later retrieval by the {@link Throwable#getCause()} method).
     *              (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public IncorrectCodeException(final Throwable cause) {
        super(cause);
    }
}

package it.unibo.ds.core.codes;

/**
 * An interface modeling a one-time-code, i.e. a code
 * that is valid for only one transaction.
 */
public interface OneTimeCode {

    /**
     * @return the one time code.
     */
    Long getCode();

    /**
     * Consume this code, i.e. make it unusable for future transactions.
     * @throws AlreadyConsumedCodeException if it was already consumed
     */
    void consume() throws AlreadyConsumedCodeException;

    /**
     * @return true if this code has already been consumed, false otherwise.
     */
    boolean consumed();
}

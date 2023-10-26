package it.unibo.ds.core.codes;

/**
 * An interface modeling a one-time-code, i.e. a code that is valid for only one transaction.
 */
public interface OneTimeCode {

    /**
     * @return the raw code.
     */
    String getCode();

    /**
     * Consume this code, i.e. make it unusable for future transactions.
     * @throws InvalidCodeException if it was already consumed
     */
    void consume() throws InvalidCodeException;

    /**
     * @return true if this code has already been consumed, false otherwise.
     */
    boolean consumed();
}

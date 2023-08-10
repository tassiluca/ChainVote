package it.unibo.ds.core.codes;

/**
 * An interface modeling a {@link OneTimeCode} manager, which exposes functions
 * to generate, (in)validate and verify one-time-codes.
 * @param <C> the type of the context.
 */
public interface CodeManager<C> {

    /**
     * Generates a new {@link OneTimeCode}.
     * @param context the context of the transaction
     * @param votingId the voting identifier
     * @param userId the user identifier
     * @return a new {@link OneTimeCode} instance for the given user and voting.
     */
    OneTimeCode generateFor(C context, Long votingId, String userId);

    /**
     * Generates a new {@link OneTimeCode}.
     * @param votingId the voting identifier
     * @param userId the user identifier
     * @return a new {@link OneTimeCode} instance for the given user and voting.
     */
    default OneTimeCode generateFor(final Long votingId, final String userId) {
        return generateFor(null, votingId, userId);
    }

    /**
     * Check if the given code is still valid, i.e. has not been consumed yet,
     * for the given user and voting.
     * @param context the context of the transaction
     * @param votingId the voting identifier
     * @param userId the user identifier
     * @param code the code to be validated
     * @return true if the given code is still valid, false otherwise.
     */
    boolean isValid(C context, Long votingId, String userId, OneTimeCode code);

    /**
     * Check if the given code is still valid, i.e. has not been consumed yet,
     * for the given user and voting.
     * @param votingId the voting identifier
     * @param userId the user identifier
     * @param code the code to be validated
     * @return true if the given code is still valid, false otherwise.
     */
    default boolean isValid(final Long votingId, final String userId, final OneTimeCode code) {
        return isValid(null, votingId, userId, code);
    }

    /**
     * Invalidate the given code for the given user and voting. After calling this
     * method the code can no longer be used.
     * @param context the context of the transaction
     * @param votingId the voting identifier
     * @param code the code to be validated
     */
    void invalidate(C context, Long votingId, OneTimeCode code);

    /**
     * Invalidate the given code for the given user and voting. After calling this
     * method the code can no longer be used.
     * @param votingId the voting identifier
     * @param code the code to be validated
     */
    default void invalidate(final Long votingId, final OneTimeCode code) {
        invalidate(null, votingId, code);
    }

    /**
     * Verifies if the given code has been generated for the given user and voting.
     * @param context the context of the transaction
     * @param votingId the voting identifier
     * @param userId the user identifier
     * @param code the code to be validated
     * @return true if the given code is correct, false otherwise.
     */
    boolean verifyCodeOwner(C context, Long votingId, String userId, OneTimeCode code);

    /**
     * Verifies if the given code has been generated for the given user and voting.
     * @param votingId the voting identifier
     * @param userId the user identifier
     * @param code the code to be validated
     * @return true if the given code is correct, false otherwise.
     */
    default boolean verifyCodeOwner(final Long votingId, final String userId, final OneTimeCode code) {
        return verifyCodeOwner(null, votingId, userId, code);
    }
}

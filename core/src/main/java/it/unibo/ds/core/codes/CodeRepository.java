package it.unibo.ds.core.codes;

import java.util.Optional;
import java.util.Set;

/**
 * An interface with method to store/retrieve from a data source the one-time-codes data.
 * @param <C> the type of the context.
 */
public interface CodeRepository<C> {

    /**
     * Retrieve the one-time-code associated to the given user for the given voting.
     * @param context the context of the transaction
     * @param votingId the voting identifier
     * @param userId the user identifier
     * @return an {@link Optional} with the searched {@link OneTimeCode} or an empty one
     * in case no code has been generated for the user in the given voting.
     */
    Optional<OneTimeCode> get(C context, Long votingId, String userId);

    /**
     * Retrieves all the one-time-code generated for the given voting.
     * @param context the context of the transaction
     * @param votingId the voting identifier
     * @return a set of {@link OneTimeCode}s.
     */
    Set<OneTimeCode> getAllOf(C context, Long votingId);

    /**
     * Save the association between the given user and code for the given voting.
     * @param context the context of the transaction
     * @param votingId the voting identifier
     * @param userId the user identifier
     * @param code the code to be saved.
     */
    void put(C context, Long votingId, String userId, OneTimeCode code);

    /**
     * Replace the old otc associated to the voting with the given one.
     * @param context the context of the transaction
     * @param votingId the voting identifier
     * @param code the updated code
     */
    void replace(C context, Long votingId, OneTimeCode code);
}

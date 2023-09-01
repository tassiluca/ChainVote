package it.unibo.ds.core.codes;

import java.util.Optional;

/**
 * An interface with method to store/retrieve from a data source the one-time-codes data.
 * @param <C> the type of the context.
 */
public interface CodeRepository<C> {

    /**
     * Retrieve the one-time-code associated to the given user for the given election.
     * @param context the context of the transaction
     * @param electionId the election identifier
     * @param userId the user identifier
     * @return an {@link Optional} with the searched {@link OneTimeCode} or an empty one
     * in case no code has been generated for the user in the given election.
     */
    Optional<OneTimeCode> get(C context, String electionId, String userId);

    /**
     * Save the association between the given user and code for the given election.
     * @param context the context of the transaction
     * @param electionId the election identifier
     * @param userId the user identifier
     * @param code the code to be saved.
     */
    void put(C context, String electionId, String userId, OneTimeCode code);

    /**
     * Replace the old otc associated to the election with the given one.
     * @param context the context of the transaction
     * @param electionId the election identifier
     * @param userId the user identifier
     * @param code the updated code
     */
    void replace(C context, String electionId, String userId, OneTimeCode code);
}

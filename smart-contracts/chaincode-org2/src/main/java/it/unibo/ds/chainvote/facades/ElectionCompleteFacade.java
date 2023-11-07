package it.unibo.ds.chainvote.facades;

import java.util.Map;

/**
 * An interface modeling the {@link it.unibo.ds.chainvote.assets.Election} serialization with results.
 */
public interface ElectionCompleteFacade {
    /**
     * Allows access to {@link ElectionFacade} in which standard information are stored.
     * @return the {@link ElectionFacade} containing all the standard information.
     */
    ElectionFacade getElectionFacade();

    /**
     * Allows access to {@link Map} representing results of the {@link it.unibo.ds.chainvote.assets.Election}.
     * @return the {@link Map} representing the results.
     */
    Map<String, Long> getResults();
}

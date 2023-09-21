package it.unibo.ds.core.assets;

import it.unibo.ds.core.utils.Choice;

import java.time.LocalDateTime;

/**
 * An interface modeling the {@link Ballot}.
 */
public interface Ballot {

    /**
     * Return the {@link Election} ID the {@link Ballot} is related to.
     * @return the {@link Election} ID.
     */
    String getElectionID();

    /**
     * Return the {@link Ballot}'s voter ID.
     * @return the {@link Ballot}'s voter ID.
     */
    String getVoterID();

    /**
     * Return the {@link LocalDateTime} the vote is casted.
     * @return the {@link LocalDateTime}.
     */
    LocalDateTime getDate();

    /**
     * Return the {@link Ballot}'s {@link Choice} casted.
     * @return the {@link Choice}.
     */
    Choice getChoice();
}

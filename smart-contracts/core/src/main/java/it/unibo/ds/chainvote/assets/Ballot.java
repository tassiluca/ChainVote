package it.unibo.ds.chainvote.assets;

import it.unibo.ds.chainvote.utils.Choice;

import java.time.LocalDateTime;

/**
 * An interface modeling the {@link Ballot}.
 */
public interface Ballot {

    /**
     * Return the {@link Election} id the {@link Ballot} is related to.
     * @return the {@link Election} id.
     */
    String getElectionId();

    /**
     * Return the {@link Ballot}'s voter id.
     * @return the {@link Ballot}'s voter id.
     */
    String getVoterId();

    /**
     * Return the {@link LocalDateTime} the vote is cast.
     * @return the {@link LocalDateTime}.
     */
    LocalDateTime getDate();

    /**
     * Return the {@link Ballot}'s {@link Choice} cast.
     * @return the {@link Choice}.
     */
    Choice getChoice();
}

package it.unibo.ds.core.assets;

import it.unibo.ds.core.utils.Choice;

import java.time.LocalDateTime;

/**
 * A {@link Ballot} builder interface.
 */
public interface BallotBuilder {

    /**
     * Return the {@link BallotBuilder} with the electionID set if the input is correct.
     * @param electionID the electionID to set.
     * @return the {@link BallotBuilder}.
     */
    BallotBuilder electionID(String electionID);

    /**
     * Return the {@link BallotBuilder} with the voterID set if the input is correct.
     * @param voterID the voterID to set.
     * @return the {@link BallotBuilder}.
     */
    BallotBuilder voterID(String voterID);

    /**
     * Return the {@link BallotBuilder} with the {@link LocalDateTime} set if the input is correct.
     * @param date the {@link LocalDateTime} to set.
     * @return the {@link BallotBuilder}.
     */
    BallotBuilder date(LocalDateTime date);

    /**
     * Return the {@link BallotBuilder} with the {@link Choice} set if the input is correct.
     * @param choice the {@link Choice} to set.
     * @return the {@link BallotBuilder}.
     */
    BallotBuilder choice(Choice choice);

    /**
     * Return the new {@link Ballot} if params are correctly set.
     * @return the new {@link Ballot}.
     */
    Ballot build();
}

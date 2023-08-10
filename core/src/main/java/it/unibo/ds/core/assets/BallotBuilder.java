package it.unibo.ds.core.assets;

import it.unibo.ds.core.utils.Choice;

import java.time.LocalDateTime;
import java.util.List;

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
    BallotBuilder dateUnchecked(LocalDateTime date);

    /**
     * Return the {@link BallotBuilder} with the {@link LocalDateTime} set if the input is correct, checked
     * against starting and ending {@link LocalDateTime} of the {@link Election}.
     * @param date the {@link LocalDateTime} to set.
     * @param start the {@link LocalDateTime} to check.
     * @param end the {@link LocalDateTime} to check.
     * @return the {@link BallotBuilder}.
     */
    BallotBuilder dateChecked(LocalDateTime date, LocalDateTime start, LocalDateTime end);

    /**
     * Return the {@link BallotBuilder} with the {@link Choice} set if the input is correct.
     * @param choice the {@link Choice} to set.
     * @return the {@link BallotBuilder}.
     */
    BallotBuilder choiceUnchecked(Choice choice);

    /**
     * Return the {@link BallotBuilder} with the {@link Choice} set if the input is correct.
     * @param choice the {@link Choice} to set.
     * @param choices the {@link Choice}s of the {@link Election} where the {@link Choice} is casted,
     *               used to check the input {@link Choice}.
     * @return the {@link BallotBuilder}.
     */
    BallotBuilder choiceChecked(Choice choice, List<Choice> choices);

    /**
     * Return the new {@link Ballot} if params are correctly set.
     * @return the new {@link Ballot}.
     */
    Ballot build();
}

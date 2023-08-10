package it.unibo.ds.core.assets;

import it.unibo.ds.core.utils.Choice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * An interface modeling a voting.
 */
public interface Election {

    /**
     * Return the {@link Election}'s ID.
     * @return the {@link Election}'s ID.
     */
    String getElectionID();

    /**
     * Return the {@link Election}'s goal.
     * @return the {@link Election}'s goal.
     */
    String getGoal();

    /**
     * Return the {@link Election}'s voters expected.
     * @return the {@link Election}'s voters expected.
     */
    long getVotersNumber();

    /**
     * Return the {@link Election}'s starting {@link LocalDateTime}.
     * @return the {@link Election}'s starting {@link LocalDateTime}.
     */
    LocalDateTime getStartingDate();

    /**
     * Return the {@link Election}'s ending {@link LocalDateTime}.
     * @return the {@link Election}'s ending {@link LocalDateTime}.
     */
    LocalDateTime getEndingDate();

    /**
     * Return the {@link Election}'s list of {@link Choice}.
     * @return the {@link Election}'s list of {@link Choice}.
     */
    List<Choice> getChoices();

    /**
     * Return the {@link Election}'s results.
     * @return the {@link Election}'s results.
     */
    Map<Choice, Long> getResults();

    /**
     * Return the {@link Election}'s list of {@link Ballot}s.
     * @return the {@link Election}'s list of {@link Ballot}s.
     */
    List<Choice> getBallots();

    /**
     * Return an {@link Optional} containing the structure of accountability of the {@link Election} if present.
     * @return an {@link Optional} of the accountability of the {@link Election}.
     */
    Optional<Map<String, Choice>> getAccountability();

    /**
     * Check if the {@link Election} is open now.
     * @return if the {@link Election} is open now.
     */
    boolean isOpen();

    /**
     * Cast a {@link Ballot} in the {@link Election} if it's valid.
     * @param ballot the {@link Ballot} to cast.
     * @return if the {@link Ballot} is correctly casted.
     */
    boolean castVote(Ballot ballot);
}

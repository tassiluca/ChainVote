package it.unibo.ds.chainvote.assets;

import it.unibo.ds.chainvote.utils.Choice;

import java.time.LocalDateTime;
import java.util.List;

/**
 * An interface modeling the informative aspect of an {@link Election}
 */
public interface ElectionInfo {

    /**
     * Return the {@link Election}'s id.
     * @return the {@link Election}'s id.
     */
    String getElectionId();

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
    LocalDateTime getStartDate();

    /**
     * Return the {@link Election}'s ending {@link LocalDateTime}.
     * @return the {@link Election}'s ending {@link LocalDateTime}.
     */
    LocalDateTime getEndDate();

    /**
     * Return the {@link Election}'s list of {@link Choice}.
     * @return the {@link Election}'s list of {@link Choice}.
     */
    List<Choice> getChoices();


    /**
     * Check if the {@link Election} is open now.
     * @return if the {@link Election} is open now.
     */
    boolean isOpen();
}

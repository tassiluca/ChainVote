package it.unibo.ds.core.assets;

import it.unibo.ds.core.utils.Choice;

import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO document.
 */
public interface ElectionInfo {

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
     * Check if the {@link Election} is open now.
     * @return if the {@link Election} is open now.
     */
    boolean isOpen();
}

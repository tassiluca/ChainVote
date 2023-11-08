package it.unibo.ds.chainvote.facade;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * An interface modeling the {@link it.unibo.ds.chainvote.assets.Election} serialization.
 */
public interface ElectionFacade {

    /**
     * Return the {@link ElectionStatus} of the {@link ElectionFacade}.
     * @return the {@link ElectionStatus}.
     */
    ElectionStatus getStatus();

    /**
     * Return the {@link ElectionFacade}'s electionId.
     * @return the {@link ElectionFacade} id.
     */
    String getId();

    /**
     * Return the {@link ElectionFacade}'s goal.
     * @return the {@link ElectionFacade} goal.
     */
    String getGoal();

    /**
     * Return the {@link ElectionFacade}'s start {@link LocalDateTime}.
     * @return the {@link ElectionFacade} start date.
     */
    LocalDateTime getStartDate();

    /**
     * Return the {@link ElectionFacade}'s end {@link LocalDateTime}.
     * @return the {@link ElectionFacade} end date.
     */
    LocalDateTime getEndDate();

    /**
     * Return the {@link ElectionFacade}'s affluence.
     * @return the {@link ElectionFacade} affluence.
     */
    double getAffluence();

    /**
     * Allows access to {@link Map} representing results of the {@link it.unibo.ds.chainvote.assets.Election}.
     * @return the {@link Map} representing the results.
     */
    Map<String, Long> getResults();
}

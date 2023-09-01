package it.unibo.ds.core.assets;

import it.unibo.ds.core.utils.Choice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * The {@link Election} builder interface.
 */
public interface ElectionBuilder {

    /**
     * Return the {@link ElectionBuilder} with the goal set if the input is correct.
     * @param goal the goal to set.
     * @return the {@link ElectionBuilder}.
     */
    ElectionBuilder goal(String goal);

    /**
     * Return the {@link ElectionBuilder} with the voters' number set if the input is correct.
     * @param number the voters number to set.
     * @return the {@link ElectionBuilder}.
     */
    ElectionBuilder voters(long number);

    /**
     * Return the {@link ElectionBuilder} with the starting {@link LocalDateTime} set if the input is correct.
     * @param start the starting {@link LocalDateTime} to set.
     * @return the {@link ElectionBuilder}.
     */
    ElectionBuilder start(LocalDateTime start);

    /**
     * Return the {@link ElectionBuilder} with the ending {@link LocalDateTime} set if the input is correct.
     * @param end the ending {@link LocalDateTime} to set.
     * @return the {@link ElectionBuilder}.
     */
    ElectionBuilder end(LocalDateTime end);

    /**
     * Return the {@link ElectionBuilder} with the list of {@link Choice}s set if the input is correct.
     * @param choices the list of {@link Choice}s to set.
     * @return the {@link ElectionBuilder}.
     */
    ElectionBuilder choices(List<Choice> choices);

    /**
     * Return the {@link ElectionBuilder} with the list of results set if the input is correct.
     * @param results the list of results to set.
     * @return the {@link ElectionBuilder}.
     */
    ElectionBuilder results(Map<Choice, Long> results);

    /**
     * Return the {@link ElectionBuilder} with the list of ballots set if the input is correct.
     * @param ballots the list of ballots to set.
     * @return the {@link ElectionBuilder}.
     */
    ElectionBuilder ballots(List<Choice> ballots);

    /**
     * Return the new {@link Election} if params are correctly set.
     * @return the new {@link Election}.
     */
    Election build();
}

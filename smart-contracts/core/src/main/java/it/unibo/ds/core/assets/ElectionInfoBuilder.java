package it.unibo.ds.core.assets;

import it.unibo.ds.core.utils.Choice;

import java.time.LocalDateTime;
import java.util.List;

public interface ElectionInfoBuilder {

    /**
     * Return the {@link ElectionInfoBuilder} with the goal set if the input is correct.
     * @param goal the goal to set.
     * @return the {@link ElectionInfoBuilder}.
     */
    ElectionInfoBuilder goal(String goal);

    /**
     * Return the {@link ElectionInfoBuilder} with the voters' number set if the input is correct.
     * @param number the voters number to set.
     * @return the {@link ElectionInfoBuilder}.
     */
    ElectionInfoBuilder voters(long number);

    /**
     * Return the {@link ElectionInfoBuilder} with the starting {@link LocalDateTime} set if the input is correct.
     * @param start the starting {@link LocalDateTime} to set.
     * @return the {@link ElectionInfoBuilder}.
     */
    ElectionInfoBuilder start(LocalDateTime start);

    /**
     * Return the {@link ElectionInfoBuilder} with the ending {@link LocalDateTime} set if the input is correct.
     * @param end the ending {@link LocalDateTime} to set.
     * @return the {@link ElectionInfoBuilder}.
     */
    ElectionInfoBuilder end(LocalDateTime end);

    /**
     * Return the {@link ElectionInfoBuilder} with the list of {@link Choice}s set if the input is correct.
     * @param choices the list of {@link Choice}s to set.
     * @return the {@link ElectionInfoBuilder}.
     */
    ElectionInfoBuilder choices(List<Choice> choices);

    /**
     * Return the new {@link ElectionInfo} if params are correctly set.
     * @return the new {@link ElectionInfo}.
     */
    ElectionInfo build();
}

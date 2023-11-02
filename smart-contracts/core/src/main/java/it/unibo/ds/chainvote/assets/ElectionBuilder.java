package it.unibo.ds.chainvote.assets;

import it.unibo.ds.chainvote.utils.Choice;

import java.util.List;
import java.util.Map;

/**
 * The {@link Election} builder interface.
 */
public interface ElectionBuilder {

    /**
     * Return the {@link ElectionBuilder} with the list of results set if the input is correct.
     * @param results the list of results to set.
     * @return the {@link ElectionBuilder}.
     */
    ElectionBuilder results(Map<String, Long> results);

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

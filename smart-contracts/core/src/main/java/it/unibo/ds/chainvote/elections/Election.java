package it.unibo.ds.chainvote.elections;

import it.unibo.ds.chainvote.utils.Choice;

import java.util.List;
import java.util.Map;

/**
 * An interface modeling a real election.
 */
public interface Election {

    /**
     * Return the {@link Election}'s results.
     * @return the {@link Election}'s results.
     */
    Map<String, Long> getResults();

    /**
     * Return the {@link Election}'s list of {@link Ballot}s.
     * @return the {@link Election}'s list of {@link Ballot}s.
     */
    List<Choice> getBallots();

    /**
     * Cast a {@link Ballot} in the {@link Election} if it's valid.
     * @param ballot the {@link Ballot} to cast.
     * @return if the {@link Ballot} is correctly cast.
     */
    boolean castVote(Ballot ballot);
}

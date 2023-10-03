package it.unibo.ds.core.assets;

import it.unibo.ds.core.utils.Choice;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * An interface modeling a voting.
 */
public interface Election {

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
     * Cast a {@link Ballot} in the {@link Election} if it's valid.
     * @param ballot the {@link Ballot} to cast.
     * @return if the {@link Ballot} is correctly casted.
     */
    boolean castVote(Ballot ballot);
}

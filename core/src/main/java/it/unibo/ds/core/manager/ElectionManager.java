package it.unibo.ds.core.manager;

import it.unibo.ds.core.assets.Ballot;
import it.unibo.ds.core.assets.Election;
import it.unibo.ds.core.utils.Choice;

import java.util.List;

/**
 * An interface modeling an {@link Election} manager.
 */
public interface ElectionManager {

    /**
     * Given a list of {@link Choice}s, it eventually adds the {@link it.unibo.ds.core.utils.FixedVotes} and distinct
     * the {@link Choice}s that are equals.
     * @param choices the list of {@link Choice}s given.
     * @return the new list of {@link Choice}s.
     */
    List<Choice> initializeChoice(List<Choice> choices);

    /**
     * Check if a {@link Ballot} can be correctly casted in an {@link Election}.
     * @param election the {@link Election} where the {@link Ballot} could be casted.
     * @param ballot the {@link Ballot} containing the {@link Choice} to cast.
     * @return if the {@link Ballot} is correctly casted.
     */
    boolean isBallotValid(Election election, Ballot ballot);
}

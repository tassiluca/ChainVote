package it.unibo.ds.core.manager;

import it.unibo.ds.core.assets.Ballot;
import it.unibo.ds.core.assets.Election;
import it.unibo.ds.core.utils.Choice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
    List<Choice> initializeChoices(List<Choice> choices);

    Map<Choice, Long> initializeResults(Map<Choice, Long> results, List<Choice> choices, long voters);

    List<Choice> initializeBallots(List<Choice> ballots, List<Choice> choices, Map<Choice, Long> results, long voters);

    void checkData(LocalDateTime start, LocalDateTime end);

    void checkVoters(long voters);

    /**
     * Check if a {@link Ballot} can be correctly casted in an {@link Election}.
     * @param election the {@link Election} where the {@link Ballot} could be casted.
     * @param ballot the {@link Ballot} containing the {@link Choice} to cast.
     */
    void checkBallot(Election election, Ballot ballot);
}

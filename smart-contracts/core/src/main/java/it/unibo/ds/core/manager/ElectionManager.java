package it.unibo.ds.core.manager;

import it.unibo.ds.core.assets.Ballot;
import it.unibo.ds.core.assets.Election;
import it.unibo.ds.core.assets.ElectionInfo;

/**
 * An interface modeling an {@link Election} manager.
 */
public interface ElectionManager {

    /**
     * Cast a vote given by the {@link Ballot} in the {@link Election}, given the {@link ElectionInfo}.
     * @param election the {@link Election} in which the vote is registered.
     * @param electionInfo the {@link ElectionInfo} of the {@link Election}.
     * @param ballot the {@link Ballot} containing the vote to cast.
     */
    void castVote(Election election, ElectionInfo electionInfo, Ballot ballot);
}

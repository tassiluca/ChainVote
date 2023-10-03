package it.unibo.ds.core.manager;

import it.unibo.ds.core.assets.Ballot;
import it.unibo.ds.core.assets.Election;
import it.unibo.ds.core.assets.ElectionInfo;
import it.unibo.ds.core.utils.Choice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * An interface modeling an {@link Election} manager.
 */
public interface ElectionManager {

    void castVote(Election election, ElectionInfo electionInfo, Ballot ballot);
}

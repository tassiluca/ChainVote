package it.unibo.ds.core.manager;

import it.unibo.ds.core.assets.*;
import it.unibo.ds.core.utils.Choice;
import it.unibo.ds.core.utils.FixedVotes;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static it.unibo.ds.core.utils.Utils.isDateBetween;

/**
 * An implementation of {@link ElectionManager}.
 */
public final class ElectionManagerImpl implements ElectionManager {

    private static ElectionManager singleInstance = null;

    private ElectionManagerImpl() { }

    /**
     * Return a Singleton {@link ElectionManager} instance.
     * @return an {@link ElectionManager} instance.
     */
    public static synchronized ElectionManager getInstance() {
        if (singleInstance == null) {
            singleInstance = new ElectionManagerImpl();
        }
        return singleInstance;
    }

    @Override
    public void castVote(Election election, ElectionInfo electionInfo, Ballot ballot) {
        this.checkBallot(electionInfo, election, ballot);
        if (!election.castVote(ballot)) {
            throw new IllegalStateException("Something went wrong casting vote");
        }
    }

    private void illegal(final String message) {
        throw new IllegalArgumentException(message);
    }

    private void checkBallot(final ElectionInfo electionInfo, final Election election, final Ballot ballot) {
        if (!electionInfo.isOpen()
                || !isDateBetween(ballot.getDate(), electionInfo.getStartingDate(), electionInfo.getEndingDate())
                || !electionInfo.getChoices().contains(ballot.getChoice())
                || election.getResults().values().stream().reduce(Long::sum).orElseThrow() >= electionInfo.getVotersNumber()) {
            illegal("Invalid ballot " + ballot);
        }
    }
}

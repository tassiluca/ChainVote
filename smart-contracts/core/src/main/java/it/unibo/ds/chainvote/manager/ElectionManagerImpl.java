package it.unibo.ds.chainvote.manager;

import it.unibo.ds.chainvote.assets.Ballot;
import it.unibo.ds.chainvote.assets.Election;
import it.unibo.ds.chainvote.assets.ElectionInfo;

import static it.unibo.ds.chainvote.utils.Utils.isDateBetween;

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
    public void castVote(final Election election, final ElectionInfo electionInfo, final Ballot ballot) {
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

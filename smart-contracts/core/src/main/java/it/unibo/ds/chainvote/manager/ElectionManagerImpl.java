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

    /**
     * Throws an {@link IllegalArgumentException} with the given message.
     * @param message the message used to build the {@link IllegalArgumentException}.
     */
    private void illegal(final String message) {
        throw new IllegalArgumentException(message);
    }

    /**
     * Check if the {@link Ballot} can be cast in a given {@link Election}, in particular:
     * If the {@link ElectionInfo} is open.
     * If the {@link java.time.LocalDateTime} of the {@link Ballot} is between {@link Election} start and end {@link java.time.LocalDateTime}
     * If the voters limit has already been reached.
     * @param electionInfo the {@link ElectionInfo} in order to check dates and {@link it.unibo.ds.chainvote.utils.Choice}s.
     * @param election the {@link Election} in which the {@link Ballot} is cast, used to check if the voters limit has already been reached.
     * @param ballot the {@link Ballot} to cast.
     * @throws IllegalArgumentException in above rules are not fit.
     */
    private void checkBallot(final ElectionInfo electionInfo, final Election election, final Ballot ballot) {
        if (!electionInfo.isOpen()
                || !isDateBetween(ballot.getDate(), electionInfo.getStartDate(), electionInfo.getEndDate())
                || !electionInfo.getChoices().contains(ballot.getChoice())
                || election.getResults().values().stream().reduce(Long::sum).orElseThrow() >= electionInfo.getVotersNumber()) {
            illegal("Invalid ballot " + ballot);
        }
    }
}

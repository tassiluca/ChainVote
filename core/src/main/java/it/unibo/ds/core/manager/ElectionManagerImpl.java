package it.unibo.ds.core.manager;

import it.unibo.ds.core.assets.Ballot;
import it.unibo.ds.core.assets.Election;
import it.unibo.ds.core.utils.Choice;
import it.unibo.ds.core.utils.FixedVotes;

import java.util.ArrayList;
import java.util.List;

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

    private boolean isAValidChoice(final Choice choice) {
        return true;
    }

    @Override
    public List<Choice> initializeChoice(final List<Choice> choices) {
        List<Choice> retList = new ArrayList<>();
        choices.stream().distinct()
                .filter(this::isAValidChoice)
                .forEach(choice -> retList.add(new Choice(choice)));
        if (!retList.contains(FixedVotes.INFORMAL_BALLOT.getChoice())) {
            retList.add(FixedVotes.INFORMAL_BALLOT.getChoice());
        }
        return retList;
    }

    @Override
    public boolean isBallotValid(final Election election, final Ballot ballot) {
        return election.isOpen()
                && isDateBetween(ballot.getDate(), election.getStartingDate(), election.getEndingDate())
                && election.getChoices().contains(ballot.getChoice())
                && election.getResults().values().stream().reduce(Long::sum).orElseThrow() < election.getVotersNumber();
    }
}

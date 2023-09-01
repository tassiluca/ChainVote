package it.unibo.ds.core.manager;

import it.unibo.ds.core.assets.Ballot;
import it.unibo.ds.core.assets.Election;
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

    private boolean isAValidChoice(final Choice choice) {
        return true;
    }

    @Override
    public List<Choice> initializeBallots(final List<Choice> ballots,
                                          final List<Choice> choices,
                                          final Map<Choice, Long> results,
                                          final long voters) {
        this.checkBallots(ballots, voters, choices, results);
        return ballots;
    }

    @Override
    public List<Choice> initializeChoices(final List<Choice> choices) {
        this.checkChoices(choices);
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
    public Map<Choice, Long> initializeResults(final Map<Choice, Long> results, final List<Choice> choices, final long votersNumber) {
        this.checkResults(results, votersNumber, choices);
        Map<Choice, Long> retResults = new HashMap<>();
        results.keySet()
                .forEach(choice -> retResults.put(choice, results.get(choice)));
        choices.stream()
                .filter(choice -> !results.containsKey(choice))
                .forEach(choice -> retResults.put(choice, (long) 0));
        if (!retResults.containsKey(FixedVotes.INFORMAL_BALLOT.getChoice())) {
            retResults.put(FixedVotes.INFORMAL_BALLOT.getChoice(), (long) 0);
        }
        return retResults;
    }

    private void illegal(final String message) {
        throw new IllegalArgumentException(message);
    }

    @Override
    public void checkData(final LocalDateTime start, final LocalDateTime end) {
        if (start.isAfter(end) || start.isEqual(end)) {
            illegal("Invalid SD " + start + " and ED " + end);
        }
    }

    private void checkResults(final Map<Choice, Long> results, final long votersNumber, final List<Choice> choices) {
        if (results.values().stream().reduce(Long::sum).orElse((long) 0) > votersNumber
                || results.values().stream().anyMatch(l -> l < 0)
                || !new HashSet<>(choices).containsAll(results.keySet())
                || results.keySet().size() != results.keySet().stream().distinct().count()) {
            illegal("Invalid results " + results);
        }
    }

    @Override
    public void checkBallot(final Election election, final Ballot ballot) {
        if (!election.isOpen()
                || !isDateBetween(ballot.getDate(), election.getStartingDate(), election.getEndingDate())
                || !election.getChoices().contains(ballot.getChoice())
                || election.getResults().values().stream().reduce(Long::sum).orElseThrow() >= election.getVotersNumber()) {
            illegal("Invalid ballot " + ballot);
        }
    }

    private void checkChoices(final List<Choice> choices) {
        if (choices.stream().distinct()
                .filter(choice -> !choice.equals(FixedVotes.INFORMAL_BALLOT.getChoice()))
                .count() < 2 || choices.size() != choices.stream().distinct().count()) {
            illegal("Invalid choices " + choices);
        }
    }

    private void checkBallots(final List<Choice> ballots, final long votersNumber, final List<Choice> choices,
                              final Map<Choice, Long> results) {
        if (ballots.size() > votersNumber
                || !new HashSet<>(choices).containsAll(ballots)
                || !ballots.stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()))
                .entrySet().stream().allMatch(e -> e.getValue() <= results.get(e.getKey()))) {
            illegal("Invalid ballots " + ballots);
        }
    }

    @Override
    public void checkVoters(final long voters) {
        if (voters < 1) {
            illegal("Invalid voters " + voters);
        }
    }
}

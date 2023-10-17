package it.unibo.ds.core.factory;

import it.unibo.ds.core.assets.Election;
import it.unibo.ds.core.assets.ElectionImpl;
import it.unibo.ds.core.assets.ElectionInfo;
import it.unibo.ds.core.assets.ElectionInfoImpl;
import it.unibo.ds.core.utils.Choice;
import it.unibo.ds.core.utils.FixedVotes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * A factory for an {@link Election}.
 */
public class ElectionFactory {

    /**
     * TODO document.
     * @param goal TODO
     * @param votersNumber TODO
     * @param startingDate TODO
     * @param endingDate TODO
     * @param choices TODO
     * @return .
     */
    public static ElectionInfo buildElectionInfo(
        final String goal,
        final long votersNumber,
        final LocalDateTime startingDate,
        final LocalDateTime endingDate,
        final List<Choice> choices
    ) {
        checkVoters(votersNumber);
        checkData(startingDate, endingDate);
        return new ElectionInfoImpl.Builder()
            .goal(goal)
            .voters(votersNumber)
            .start(startingDate)
            .end(endingDate)
            .choices(initializeChoices(choices))
            .build();
    }

    /**
     * TODO document.
     * @param electionInfo TODO
     * @return TODO
     */
    public static Election buildElection(final ElectionInfo electionInfo) {
        return buildElection(electionInfo, new HashMap<>());
    }

    /**
     * TODO document.
     * @param electionInfo TODO
     * @param results TODO
     * @return TODO
     */
    public static Election buildElection(final ElectionInfo electionInfo, final Map<Choice, Long> results) {
        checkDataAndResults(electionInfo.getEndingDate(), results);
        return new ElectionImpl.Builder()
            .results(initializeResults(results, electionInfo.getChoices(), electionInfo.getVotersNumber()))
            .ballots(new ArrayList<>())
            .build();
    }

    private static boolean isAValidChoice(final Choice choice) {
        return true;
    }

    private static List<Choice> initializeChoices(final List<Choice> choices) {
        checkChoices(choices);
        final List<Choice> retList = new ArrayList<>();
        choices.stream().distinct()
            .filter(ElectionFactory::isAValidChoice)
            .forEach(choice -> retList.add(new Choice(choice)));
        if (!retList.contains(FixedVotes.INFORMAL_BALLOT.getChoice())) {
            retList.add(FixedVotes.INFORMAL_BALLOT.getChoice());
        }
        return retList;
    }

    private static Map<Choice, Long> initializeResults(
        final Map<Choice, Long> results,
        final List<Choice> choices,
        final long votersNumber
    ) {
        checkResults(results, votersNumber, choices);
        final Map<Choice, Long> retResults = new HashMap<>();
        results.keySet().forEach(choice -> retResults.put(choice, results.get(choice)));
        choices.stream()
            .filter(choice -> !results.containsKey(choice))
            .forEach(choice -> retResults.put(choice, (long) 0));
        if (!retResults.containsKey(FixedVotes.INFORMAL_BALLOT.getChoice())) {
            retResults.put(FixedVotes.INFORMAL_BALLOT.getChoice(), (long) 0);
        }
        return retResults;
    }

    private static void illegal(final String message) {
        throw new IllegalArgumentException(message);
    }

    private static void checkData(final LocalDateTime start, final LocalDateTime end) {
        if (start.isAfter(end) || start.isEqual(end)) {
            illegal("Invalid starting date " + start + " and ending date " + end);
        }
    }

    private static void checkDataAndResults(final LocalDateTime end, final Map<Choice, Long> results) {
        boolean alreadyClosedElection = LocalDateTime.now().isAfter(end);
        boolean resultsAreEmpty = results.equals(new HashMap<>())
                || results.values().stream().reduce(Long::sum).orElseThrow() == 0;
        if (alreadyClosedElection && resultsAreEmpty) {
            illegal("Already close election and empty results");
        }
    }

    private static void checkResults(final Map<Choice, Long> results, final long votersNumber, final List<Choice> choices) {
        if (results.values().stream().reduce(Long::sum).orElse((long) 0) > votersNumber
                || results.values().stream().anyMatch(l -> l < 0)
                || !new HashSet<>(choices).containsAll(results.keySet())
                || results.keySet().size() != results.keySet().stream().distinct().count()) {
            illegal("Invalid results " + results);
        }
    }

    private static void checkChoices(final List<Choice> choices) {
        if (choices.stream().distinct()
                .filter(choice -> !choice.equals(FixedVotes.INFORMAL_BALLOT.getChoice()))
                .count() < 2 || choices.size() != choices.stream().distinct().count()) {
            illegal("Invalid choices " + choices);
        }
    }

    private static void checkVoters(final long voters) {
        if (voters < 1) {
            illegal("Invalid voters " + voters);
        }
    }
}

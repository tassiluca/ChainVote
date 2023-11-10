package it.unibo.ds.chainvote.factory;

import it.unibo.ds.chainvote.assets.Election;
import it.unibo.ds.chainvote.assets.ElectionImpl;
import it.unibo.ds.chainvote.assets.ElectionInfo;
import it.unibo.ds.chainvote.assets.ElectionInfoImpl;
import it.unibo.ds.chainvote.utils.Choice;
import it.unibo.ds.chainvote.utils.FixedVotes;
import it.unibo.ds.chainvote.utils.Utils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A factory for {@link Election}s.
 */
public class ElectionFactory {

    /**
     * Build an {@link ElectionInfo} checking if parameters are correct:
     * Each object must be non-null,
     * String must be non-empty.
     * @param goal the goal of the {@link ElectionInfo} to build.
     * @param votersNumber the maximum number of voters of the {@link ElectionInfo} to build.
     * @param startingDate the starting {@link LocalDateTime} of the {@link ElectionInfo} to build.
     * @param endingDate the ending {@link LocalDateTime} of the {@link ElectionInfo} to build.
     * @param choices the {@link List} of {@link Choice}s  of the {@link ElectionInfo} to build.
     * @return the new {@link ElectionInfo}.
     * @throws IllegalArgumentException in case checks on parameters fail.
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
     * Build an {@link Election} given the {@link ElectionInfo}.
     * @param electionInfo the {@link ElectionInfo} used to build the {@link Election}.
     * @return the new {@link Election}.
     * @throws IllegalArgumentException in case {@link Election} is already closed.
     */
    public static Election buildElection(final ElectionInfo electionInfo) {
        return buildElection(electionInfo, new HashMap<>());
    }

    /**
     * Build an {@link Election} given the {@link ElectionInfo} and a starting results by following
     * the {@link ElectionFactory#checkDataAndResults(LocalDateTime, Map)} rules.
     * @param electionInfo the {@link ElectionInfo} used to build the {@link Election}.
     * @param results the {@link Map} representing the starting results used to build the {@link Election}.
     * @return the new {@link Election}.
     * @throws IllegalArgumentException in case checks on parameters fail.
     */
    public static Election buildElection(final ElectionInfo electionInfo, final Map<String, Long> results) {
        checkDataAndResults(electionInfo.getEndDate(), results);
        return new ElectionImpl.Builder()
            .results(initializeResults(results, electionInfo.getChoices(), electionInfo.getVotersNumber()))
            .ballots(new ArrayList<>())
            .build();
    }

    /**
     * Check if a {@link Choice} fit preliminary rules.
     * @param choice the {@link Choice} to check.
     * @return a {@link Boolean} representing the check.
     */
    private static boolean isAValidChoice(final Choice choice) {
        return true;
    }

    /**
     * Given a {@link List} of {@link Choice}s, filter them by applying the {@link ElectionFactory#checkChoices(List)}
     * rules and, if check is positive, add if not present {@link FixedVotes#INFORMAL_BALLOT}.
     * @param choices the {@link List} of {@link Choice} to initialize.
     * @return the {@link List} of {@link Choice} filtered and initialized.
     */
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

    /**
     * Given a {@link Map} representing an {@link Election} results, it's checked if all the {@link Choice} of the {@link Election}
     * are present in the results, adding them if not. All rules of {@link ElectionFactory#checkResults(Map, long, List)}
     * are verified.
     * @param results the {@link Map} to check and initialize.
     * @param choices the {@link List} of {@link Choice} used to check if results contains all the {@link Choice} of {@link Election}.
     * @param votersNumber the number representing the limit of voters.
     * @return a {@link Map} with all {@link Choice} presents in the given list and {@link FixedVotes#INFORMAL_BALLOT}.
     */
    private static Map<String, Long> initializeResults(
        final Map<String, Long> results,
        final List<Choice> choices,
        final long votersNumber
    ) {
        checkResults(results, votersNumber, choices);
        final Map<String, Long> retResults = new HashMap<>();
        results.keySet().forEach(choice -> retResults.put(choice, results.get(choice)));
        choices.stream()
            .filter(choice -> !results.containsKey(choice.getChoice()))
            .forEach(choice -> retResults.put(choice.getChoice(), 0L));
        return Utils.getResultsWithBlankChoice(retResults);
    }

    /**
     * Throws an {@link IllegalArgumentException} with the message given.
     * @param message the message used to build the {@link IllegalArgumentException}.
     */
    private static void illegal(final String message) {
        throw new IllegalArgumentException(message);
    }

    /**
     * Check if the start {@link LocalDateTime} doesn't come after the end {@link LocalDateTime}.
     * @param start the start {@link LocalDateTime}.
     * @param end the end {@link LocalDateTime}.
     * @throws IllegalArgumentException if start comes after end.
     */
    private static void checkData(final LocalDateTime start, final LocalDateTime end) {
        if (start.isAfter(end) || start.isEqual(end)) {
            illegal("Invalid starting date " + start + " and ending date " + end);
        }
    }

    /**
     * Check if in case end {@link LocalDateTime} has already passed, the results contain at least a non-zero value.
     * @param end the end {@link LocalDateTime}.
     * @param results the {@link Map} to check.
     * @throws IllegalArgumentException in case the rule above isn't fit.
     */
    private static void checkDataAndResults(final LocalDateTime end, final Map<String, Long> results) {
        boolean alreadyClosedElection = LocalDateTime.now().isAfter(end);
        boolean resultsAreEmpty = results.equals(new HashMap<>())
                || results.values().stream().reduce(Long::sum).orElse(0L) == 0L;
        if (alreadyClosedElection && resultsAreEmpty) {
            illegal("Already close election and empty results");
        }
    }

    /**
     * Check if given results fits:
     * The sum of values is lower than the limit number,
     * All {@link Choice}s present in results are presents the {@link List} of {@link Choice} given.
     * @param results the {@link Map} to check.
     * @param votersNumber the number representing the limit of voters.
     * @param choices the {@link List} of {@link Choice} used to check if results contains all the {@link Choice} of {@link Election}.
     * @throws IllegalArgumentException if conditions above are not fit.
     */
    private static void checkResults(final Map<String, Long> results, final long votersNumber, final List<Choice> choices) {
        if (results.values().stream().reduce(Long::sum).orElse(0L) > votersNumber
                || results.values().stream().anyMatch(l -> l < 0)
                || !new HashSet<>(choices).containsAll(results.keySet().stream().map(Choice::new).collect(Collectors.toSet()))) {
            illegal("Invalid results " + results);
        }
    }

    /**
     * Check if the given {@link List} of {@link Choice} fits:
     * There are at least 2 different {@link Choice} that aren't {@link FixedVotes#INFORMAL_BALLOT},
     * {@link Choice} are not repeated.
     * @param choices the {@link List} of {@link Choice} to check.
     * @throws IllegalArgumentException if conditions above are not fit.
     */
    private static void checkChoices(final List<Choice> choices) {
        if (choices.stream().distinct()
                .filter(choice -> !choice.equals(FixedVotes.INFORMAL_BALLOT.getChoice()))
                .count() < 2
                || choices.size() != choices.stream().distinct().count()) {
            illegal("Invalid choices " + choices);
        }
    }

    /**
     * Check if voters limit is greater than 1.
     * @param voters the number to check.
     * @throws IllegalArgumentException if voters limit is lower than or equal to 1.
     */
    private static void checkVoters(final long voters) {
        if (voters <= 1) {
            illegal("Invalid voters " + voters);
        }
    }
}

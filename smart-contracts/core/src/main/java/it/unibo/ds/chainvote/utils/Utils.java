package it.unibo.ds.chainvote.utils;

import it.unibo.ds.chainvote.elections.Election;
import it.unibo.ds.chainvote.elections.ElectionInfo;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Implementation of some utils.
 */
public final class Utils {

    private Utils() { }

    /**
     * Check if {@link LocalDateTime} check is between {@link LocalDateTime} from and {@link LocalDateTime} to.
     * @param check the {@link LocalDateTime} to check.
     * @param from the {@link LocalDateTime} that defines the start of interval.
     * @param to the {@link LocalDateTime} that defines the end of interval.
     * @return if check is between from and to.
     */
    public static boolean isDateBetween(final LocalDateTime check, final LocalDateTime from, final LocalDateTime to) {
        return check.isAfter(from) && check.isBefore(to);
    }

    /**
     * Calculates the hashcode of an {@link Election}.
     * @param goal the {@link Election} goal.
     * @param start the {@link Election} starting {@link LocalDateTime}.
     * @param end the {@link Election} ending {@link LocalDateTime}.
     * @param choices the {@link Election} choices.
     * @return the hashcode without need to build it.
     */
    public static String calculateID(final String goal, final LocalDateTime start, final LocalDateTime end,
                                     final List<Choice> choices) {
        List<Choice> choicesToUse = new ArrayList<>(choices);
        if (!choicesToUse.contains(FixedVotes.INFORMAL_BALLOT.getChoice())) {
            choicesToUse.add(FixedVotes.INFORMAL_BALLOT.getChoice());
        }
        return String.valueOf(Objects.hash(goal, start, end, choicesToUse));
    }

    /**
     * Calculates the hashcode of an {@link Election}.
     * @param electionInfo the {@link ElectionInfo} used to calculate hashcode.
     * @return the hashcode without need to build it.
     */
    public static String calculateID(final ElectionInfo electionInfo) {
        return calculateID(electionInfo.getGoal(), electionInfo.getStartDate(),
                electionInfo.getEndDate(), electionInfo.getChoices());
    }

    /**
     * Return the given {@link Map} representing the results of an {@link Election} adding
     * the {@link FixedVotes#INFORMAL_BALLOT} if not present.
     * @param results the {@link Map} representing the results.
     * @return the {@link Map} containing the {@link FixedVotes#INFORMAL_BALLOT}.
     */
    public static Map<String, Long> getResultsWithBlankChoice(final Map<String, Long> results) {
        final Map<String, Long> retResults = new TreeMap<>();
        results.keySet().forEach(choice -> retResults.put(choice, results.get(choice)));
        if (!retResults.containsKey(FixedVotes.INFORMAL_BALLOT.getChoice().getChoice())) {
            retResults.put(FixedVotes.INFORMAL_BALLOT.getChoice().getChoice(), 0L);
        }
        return retResults;
    }
}

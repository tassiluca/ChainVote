package it.unibo.ds.chainvote.utils;

import it.unibo.ds.chainvote.assets.Election;
import it.unibo.ds.chainvote.assets.ElectionInfo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        return calculateID(electionInfo.getGoal(), electionInfo.getStartingDate(),
                electionInfo.getEndingDate(), electionInfo.getChoices());
    }
}

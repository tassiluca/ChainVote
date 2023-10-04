package it.unibo.ds.core.utils;

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
     * Calculates the hashcode of a {@link it.unibo.ds.core.assets.Election}.
     * @param goal the {@link it.unibo.ds.core.assets.Election} goal.
     * @param start the {@link it.unibo.ds.core.assets.Election} starting {@link LocalDateTime}.
     * @param end the {@link it.unibo.ds.core.assets.Election} ending {@link LocalDateTime}.
     * @param choices the {@link it.unibo.ds.core.assets.Election} choices.
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
}

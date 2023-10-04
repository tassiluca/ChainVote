package it.unibo.ds.core.utils;

/**
 * A collection of fixed {@link Choice}s.
 */
public enum FixedVotes {
    /**
     * Represents a {@link Choice} for blank vote.
     */
    INFORMAL_BALLOT(new Choice("BLANK VOTE"));

    private final Choice choice;

    FixedVotes(final Choice choice) {
        this.choice = choice;
    }

    /**
     * Return the {@link Choice}.
     * @return the {@link Choice}.
     */
    public Choice getChoice() {
        return this.choice;
    }
}

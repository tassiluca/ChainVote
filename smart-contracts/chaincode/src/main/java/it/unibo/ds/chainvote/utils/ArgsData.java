package it.unibo.ds.chainvote.utils;

/**
 * An enum containing all the inputs key of the parameters
 * TODO document better (now for just generate)
 */
public enum ArgsData {

    /** The election identifier key. */
    ELECTION_ID("electionId"),
    /** The goal key. */
    GOAL("goal"),
    /** The date key. */
    DATE("date"),
    /** The starting date key. */
    STARTING_DATE("startDate"),
    /** The ending date key. */
    ENDING_DATE("endDate"),
    /** The voters number key. */
    VOTERS("voters"),
    /** The choice key. */
    CHOICE("choice"),
    /** The choices key. */
    CHOICES("choices"),
    /** The results key. */
    RESULTS("results"),
    /** Election info key. */
    ELECTION_INFO("electionInfo"),
    /** Election key. */
    ELECTION("election");

    private final String key;

    ArgsData(final String key) {
        this.key = key;
    }

    /**
     * @return the json entry key for the argument.
     */
    public String getKey() {
        return key;
    }
}

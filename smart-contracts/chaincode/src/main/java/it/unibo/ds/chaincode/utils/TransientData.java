package it.unibo.ds.chaincode.utils;

/**
 * An enum describing the common transient data expected as transaction inputs.
 */
public enum TransientData {
    USER_ID("userId"),
    ELECTION_ID("electionId"),
    GOAL("goal"),
    CODE("code"),
    DATE("date"),
    STARTING_DATE("startDate"),
    ENDING_DATE("endDate"),
    VOTERS("voters"),
    CHOICE("choice"),
    LIST("list"),

    // Map elements
    RESULTS("results");

    private final String key;

    TransientData(final String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}

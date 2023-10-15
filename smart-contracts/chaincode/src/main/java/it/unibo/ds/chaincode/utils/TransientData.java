package it.unibo.ds.chaincode.utils;

/**
 * An enum describing the common transient data expected as transaction inputs.
 */
public enum TransientData {
    USER_ID("userId"),
    ELECTION_ID("electionId"),
    CODE("code"),
    // TODO refactor tests and remove RESULTS from here
    RESULTS("results");

    private final String key;

    TransientData(final String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}

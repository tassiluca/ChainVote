package it.unibo.ds.chainvote.contract;

/**
 * An enum describing the common transient data expected as transaction inputs.
 */
enum TransientData {
    USER_ID("userId"),
    ELECTION_ID("electionId"),
    CODE("code");

    private final String key;

    TransientData(final String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}

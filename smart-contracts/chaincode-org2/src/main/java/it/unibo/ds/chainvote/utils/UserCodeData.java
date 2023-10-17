package it.unibo.ds.chainvote.utils;

import java.util.Map;

/**
 * An enum describing the common transient data expected as transaction inputs.
 */
public enum UserCodeData {
    USER_ID("userId"),
    ELECTION_ID("electionId"),
    CODE("code");

    private final String key;

    UserCodeData(final String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static Pair<String, Long> getUserCodePairFrom(final Map<String, byte[]> transientData) {
        return new Pair<>(
            TransientUtils.getStringFromTransient(transientData, USER_ID.getKey()),
            TransientUtils.getLongFromTransient(transientData, CODE.getKey())
        );
    }
}

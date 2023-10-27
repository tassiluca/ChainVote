package it.unibo.ds.chainvote.utils;

import org.hyperledger.fabric.shim.ChaincodeException;

import java.util.Map;

/**
 * An enum describing the common transient data expected as transaction inputs.
 */
public enum UserCodeData {

    /** The user identifier entry key. */
    USER_ID("userId"),
    /** The one-time-code entry key. */
    CODE("code");

    private final String key;

    UserCodeData(final String key) {
        this.key = key;
    }

    /**
     * @return the key with which this entry is represented.
     */
    public String getKey() {
        return key;
    }

    /**
     * @param transientData the {@link Map} containing the transient data
     * @return a 2-dimensional tuple where the first element is the user identifier and the second the one-time-code.
     * @throws ChaincodeException if the given map not contains both {@link UserCodeData#CODE} and
     *         {@link UserCodeData#USER_ID} entries.
     */
    public static Pair<String, String> getUserCodePairFrom(final Map<String, byte[]> transientData) {
        return new Pair<>(
            TransientUtils.getStringFromTransient(transientData, USER_ID.getKey()),
            TransientUtils.getStringFromTransient(transientData, CODE.getKey())
        );
    }
}

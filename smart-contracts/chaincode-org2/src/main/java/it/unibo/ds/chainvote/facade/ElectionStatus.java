package it.unibo.ds.chainvote.facade;

import it.unibo.ds.chainvote.assets.Ballot;
import it.unibo.ds.chainvote.assets.Election;

/**
 * An enum representing the possible {@link Election} status when it's serialized as an {@link ElectionFacade}.
 */
public enum ElectionStatus {

    /** Represents an {@link Election} still open, in which {@link Ballot}s can still be cast. */
    OPEN("open"),
    /** Represents an {@link Election} closed, in which {@link Ballot}s can't be cast no more. */
    CLOSED("closed");

    private final String key;

    ElectionStatus(final String key) {
        this.key = key;
    }

    /**
     * @return the status key.
     */
    public String getKey() {
        return key;
    }
}

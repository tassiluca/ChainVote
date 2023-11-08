package it.unibo.ds.chainvote.facade;

/**
 * An enum representing the possible {@link it.unibo.ds.chainvote.assets.Election} status when it's serialized
 * as an {@link ElectionFacade} or {@link ElectionCompleteFacade}.
 */
public enum ElectionStatus {
    /**
     * Represents an {@link it.unibo.ds.chainvote.assets.Election} still open, in which {@link it.unibo.ds.chainvote.assets.Ballot}s
     * can still be cast.
     */
    OPEN("open"),
    /**
     * Represents an {@link it.unibo.ds.chainvote.assets.Election} closed, in which {@link it.unibo.ds.chainvote.assets.Ballot}s
     * can't be cast no more.
     */

    CLOSED("closed");

    private final String key;

    ElectionStatus(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}

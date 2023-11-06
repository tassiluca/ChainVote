package it.unibo.ds.chainvote.facades;

// TODO documentation
public enum ElectionStatus {
    OPEN("open"),
    CLOSED("closed");

    private final String key;

    ElectionStatus(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}

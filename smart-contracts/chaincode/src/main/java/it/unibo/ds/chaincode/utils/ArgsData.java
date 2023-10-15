package it.unibo.ds.chaincode.utils;

public enum ArgsData {
    ELECTION_ID("electionId"),
    GOAL("goal"),
    DATE("date"),
    STARTING_DATE("startDate"),
    ENDING_DATE("endDate"),
    VOTERS("voters"),
    CHOICE("choice"),
    CHOICES("choices"),
    RESULTS("results"),

    // The following are used in ToBuffer
    ELECTION_INFO("electionInfo"),
    ELECTION("election");

    private final String key;

    ArgsData(final String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}

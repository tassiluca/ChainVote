package it.unibo.ds.chainvote.contract;

enum TransientData {
    USER_ID("userId"),
    ELECTION_ID("electionId"),
    CODE("code");

    final String key;

    TransientData(final String key) {
        this.key = key;
    }
}
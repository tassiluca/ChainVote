package it.unibo.ds.chainvote.assets.presentation;

import it.unibo.ds.chainvote.assets.Election;
import it.unibo.ds.chainvote.assets.ElectionInfo;

import java.util.HashMap;
import java.util.Map;

// TODO documentation
public class ElectionWithResultsToReadImpl implements ElectionWithResultsToRead {

    private final ElectionToRead efs;
    private final Map<String, Long> results;

    public ElectionWithResultsToReadImpl(Election election, ElectionInfo info) {
        this.efs = new ElectionToReadImpl(election, info);
        this.results = this.efs.getStatus().equals(ElectionStatus.CLOSED) ? election.getResults() : new HashMap<>();
    }

    @Override
    public ElectionToRead getElectionToRead() {
        return this.efs;
    }

    @Override
    public Map<String, Long> getResults() {
        return this.results;
    }
}

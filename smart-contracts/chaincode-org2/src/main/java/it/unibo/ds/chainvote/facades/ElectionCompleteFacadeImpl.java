package it.unibo.ds.chainvote.facades;

import it.unibo.ds.chainvote.assets.Election;
import it.unibo.ds.chainvote.assets.ElectionInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * An {@link ElectionCompleteFacade} implementation.
 */
public class ElectionCompleteFacadeImpl implements ElectionCompleteFacade {

    private final ElectionFacade ef;
    private final Map<String, Long> results;

    public ElectionCompleteFacadeImpl(Election election, ElectionInfo info) {
        this.ef = new ElectionFacadeImpl(election, info);
        this.results = this.ef.getStatus().equals(ElectionStatus.CLOSED) ? election.getResults() : new HashMap<>();
    }

    @Override
    public ElectionFacade getElectionFacade() {
        return this.ef;
    }

    @Override
    public Map<String, Long> getResults() {
        return this.results;
    }
}

package it.unibo.ds.chainvote.facades;

import java.util.Map;

public interface ElectionCompleteFacade {

    ElectionFacade getElectionFacade();
    Map<String, Long> getResults();
}

package it.unibo.ds.chainvote.assets.presentation;

import java.util.Map;

public interface ElectionCompleteFacade {

    ElectionFacade getElectionFacade();
    Map<String, Long> getResults();
}

package it.unibo.ds.chainvote.assets.presentation;

import java.util.Map;

public interface ElectionWithResultsToRead {

    ElectionToRead getElectionToRead();
    Map<String, Long> getResults();
}

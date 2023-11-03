package it.unibo.ds.chainvote.assets.presentation;

import java.time.LocalDateTime;

// TODO documentation
public interface ElectionToRead {

    ElectionStatus getStatus();
    String getId();
    String getGoal();
    LocalDateTime getStartDate();
    LocalDateTime getEndDate();
    double getAffluence();
}

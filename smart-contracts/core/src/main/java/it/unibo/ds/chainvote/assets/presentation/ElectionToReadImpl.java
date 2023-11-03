package it.unibo.ds.chainvote.assets.presentation;

import it.unibo.ds.chainvote.assets.Election;
import it.unibo.ds.chainvote.assets.ElectionInfo;
import it.unibo.ds.chainvote.utils.Utils;

import java.time.LocalDateTime;

// TODO documentation
public class ElectionToReadImpl implements ElectionToRead {

    private final ElectionStatus status;
    private final String id;
    private final String goal;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final double affluence;

    public ElectionToReadImpl(Election election, ElectionInfo info) {
        this.status = info.isOpen() ? ElectionStatus.OPEN : ElectionStatus.CLOSED;
        this.id = Utils.calculateID(info);
        this.goal = info.getGoal();
        this.startDate = info.getStartDate();
        this.endDate = info.getEndDate();
        this.affluence = ((double) election.getBallots().size()) / info.getVotersNumber();
    }

    @Override
    public ElectionStatus getStatus() {
        return this.status;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getGoal() {
        return this.goal;
    }

    @Override
    public LocalDateTime getStartDate() {
        return this.startDate;
    }

    @Override
    public LocalDateTime getEndDate() {
        return this.endDate;
    }

    @Override
    public double getAffluence() {
        return this.affluence;
    }
}

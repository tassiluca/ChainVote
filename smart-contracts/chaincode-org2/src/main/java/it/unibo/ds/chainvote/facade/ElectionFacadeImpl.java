package it.unibo.ds.chainvote.facade;

import it.unibo.ds.chainvote.assets.Election;
import it.unibo.ds.chainvote.assets.ElectionInfo;
import it.unibo.ds.chainvote.utils.Utils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * An {@link ElectionFacade} implementation.
 */
public class ElectionFacadeImpl implements ElectionFacade {

    private final ElectionStatus status;
    private final String id;
    private final String goal;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final double affluence;
    private final Map<String, Long> results;

    public ElectionFacadeImpl(Election election, ElectionInfo info) {
        this.status = info.isOpen() ? ElectionStatus.OPEN : ElectionStatus.CLOSED;
        this.id = Utils.calculateID(info);
        this.goal = info.getGoal();
        this.startDate = info.getStartDate();
        this.endDate = info.getEndDate();
        this.affluence = ((double) election.getBallots().size()) / info.getVotersNumber();
        this.results = info.isOpen() ? new HashMap<>() : election.getResults();
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

    @Override
    public Map<String, Long> getResults() {
        return this.results;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ElectionFacadeImpl that = (ElectionFacadeImpl) o;
        return Double.compare(that.affluence, affluence) == 0 && status == that.status && Objects.equals(id, that.id) && Objects.equals(goal, that.goal) && Objects.equals(startDate, that.startDate) && Objects.equals(endDate, that.endDate) && Objects.equals(results, that.results);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, id, goal, startDate, endDate, affluence, results);
    }
}

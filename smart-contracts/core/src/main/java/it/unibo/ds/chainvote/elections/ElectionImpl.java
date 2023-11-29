package it.unibo.ds.chainvote.elections;

import it.unibo.ds.chainvote.utils.Choice;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * An {@link Election} implementation.
 */
public final class ElectionImpl implements Election {

    private final Map<String, Long> results;

    // Keep all the choices of the ballots (Not to query if the voter has already voted)
    private final List<Choice> ballots;

    private ElectionImpl(final Map<String, Long> results, final List<Choice> ballots) {
        this.results = results;
        this.ballots = ballots;
    }

    @Override
    public Map<String, Long> getResults() {
        return new TreeMap<>(this.results);
    }

    @Override
    public List<Choice> getBallots() {
        return List.copyOf(this.ballots);
    }

    @Override
    public boolean castVote(final Ballot ballot) {
        this.ballots.add(ballot.getChoice());
        long oldValue = this.results.get(ballot.getChoice().getChoice());
        return this.results.replace(ballot.getChoice().getChoice(), oldValue, oldValue + 1);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Election other = (Election) obj;
        return this.getBallots().equals(other.getBallots()) && this.getResults().equals(other.getResults());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode())
            + ", results=" + this.getResults() + ", ballots=" + this.getBallots() + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(results, ballots);
    }

    /**
     * An {@link ElectionBuilder}'s implementation.
     */
    public static final class Builder implements ElectionBuilder {

        private Optional<Map<String, Long>> results = Optional.empty();
        private Optional<List<Choice>> ballots = Optional.empty();

        private void check(final Object input) {
            Objects.requireNonNull(input);
        }

        @Override
        public ElectionBuilder results(final Map<String, Long> results) {
            this.check(results);
            this.results = Optional.of(results);
            return this;
        }

        @Override
        public ElectionBuilder ballots(final List<Choice> ballots) {
            this.check(ballots);
            this.ballots = Optional.of(ballots);
            return this;
        }

        @Override
        public Election build() {
            return new ElectionImpl(this.results.orElse(new TreeMap<>()), this.ballots.orElse(new ArrayList<>()));
        }
    }
}

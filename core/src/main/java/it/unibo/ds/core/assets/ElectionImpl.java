package it.unibo.ds.core.assets;

import it.unibo.ds.core.manager.ElectionManagerImpl;
import it.unibo.ds.core.utils.Choice;

import java.time.LocalDateTime;
import java.util.*;

import static it.unibo.ds.core.utils.Utils.isDateBetween;

/**
 * The {@link Election} implementation.
 */
public final class ElectionImpl implements Election {

    private static final boolean DEBUG = false;

    private final String goal;

    private final long votersNumber;

    private final LocalDateTime startingDate;

    private final LocalDateTime endingDate;

    private final List<Choice> choices;

    private final Map<Choice, Long> results;

    // Only for debug
    private Map<String, Choice> voteAccountability;

    // Keep all the choices of the ballots (Not to query if the voter has already voted)
    private final List<Choice> ballots;

    private ElectionImpl(final String goal, final long votersNumber, final LocalDateTime startingDate,
                         final LocalDateTime endingDate,  final List<Choice> choices) {
        this(goal, votersNumber, startingDate, endingDate, choices, new HashMap<>(), new ArrayList<>());
    }

    private List<Choice> initializeChoice(final List<Choice> choices) {
        return List.copyOf(ElectionManagerImpl.getInstance().initializeChoices(choices));
    }

    private Map<Choice, Long> initializeResults(final List<Choice> choices,
                                          final Map<Choice, Long> results,
                                                final long votersNumber) {
        return ElectionManagerImpl.getInstance().initializeResults(results, choices, votersNumber);
    }

    private List<Choice> initializeBallots(final List<Choice> choices,
                                          final Map<Choice, Long> results,
                                          final List<Choice> ballots,
                                           final long votersNumber) {
        return ElectionManagerImpl.getInstance().initializeBallots(ballots, choices, results, votersNumber);
    }

    private long checkVotersNumber(final long voters) {
        ElectionManagerImpl.getInstance().checkVoters(voters);
        return voters;
    }

    private ElectionImpl(final String goal, final long votersNumber, final LocalDateTime startingDate,
                         final LocalDateTime endingDate, final List<Choice> choices, final Map<Choice, Long> results,
                         final List<Choice> ballots) {
        this.goal = goal;
        ElectionManagerImpl.getInstance().checkVoters(votersNumber);
        this.votersNumber = votersNumber;
        ElectionManagerImpl.getInstance().checkData(startingDate, endingDate);
        this.startingDate = startingDate;
        this.endingDate = endingDate;

        this.choices = this.initializeChoice(choices);
        this.results = this.initializeResults(choices, Objects.isNull(results) ? new HashMap<>() : results,
                votersNumber);
        this.ballots = this.initializeBallots(choices, Objects.isNull(results) ? new HashMap<>() : results,
                Objects.isNull(ballots) ? new ArrayList<>() : ballots, votersNumber);
        if (DEBUG) {
            this.voteAccountability = new HashMap<>();
        }
    }

    @Override
    public String getElectionID() {
        return String.valueOf(this.hashCode());
    }

    @Override
    public String getGoal() {
        return this.goal;
    }

    @Override
    public long getVotersNumber() {
        return this.votersNumber;
    }

    @Override
    public LocalDateTime getStartingDate() {
        return this.startingDate;
    }

    @Override
    public LocalDateTime getEndingDate() {
        return this.endingDate;
    }

    @Override
    public List<Choice> getChoices() {
        return List.copyOf(this.choices);
    }

    @Override
    public Map<Choice, Long> getResults() {
        return Map.copyOf(this.results);
    }

    @Override
    public List<Choice> getBallots() {
        return List.copyOf(this.ballots);
    }

    @Override
    public Optional<Map<String, Choice>> getAccountability() {
        return DEBUG ? Optional.of(Map.copyOf(this.voteAccountability)) : Optional.empty();
    }

    @Override
    public boolean castVote(final Ballot ballot) {
        ElectionManagerImpl.getInstance().checkBallot(this, ballot);
        this.ballots.add(ballot.getChoice());
        if (DEBUG) {
            this.voteAccountability.put(ballot.getVoterID(), ballot.getChoice());
        }
        long oldValue = this.results.get(ballot.getChoice());
        return this.results.replace(ballot.getChoice(), oldValue, oldValue + 1);
    }

    @Override
    public boolean isOpen() {
        return isDateBetween(LocalDateTime.now(), this.startingDate, this.endingDate);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        Election other = (Election) obj;

        return getElectionID().equals(other.getElectionID())
                && getGoal().equals(other.getGoal())
                && getVotersNumber() == other.getVotersNumber()
                && Objects.deepEquals(
                        new LocalDateTime[] {getStartingDate(), getEndingDate()},
                        new LocalDateTime[] {other.getStartingDate(), other.getEndingDate()});
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGoal(), getStartingDate(), getEndingDate(), getChoices());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode())
                + ", goal=" + this.goal + ", voters=" + this.votersNumber + ", starting="
                + this.startingDate + ", ending="
                + this.endingDate + ", choices=" + this.choices + "]";
    }

    /**
     * An {@link ElectionBuilder}'s implementation.
     */
    public static final class Builder implements ElectionBuilder {

        private Optional<String> goal;
        private Optional<Long> votersNumber;
        private Optional<LocalDateTime> startingDate;
        private Optional<LocalDateTime> endingDate;
        private Optional<List<Choice>> choices;
        private Optional<Map<Choice, Long>> results = Optional.empty();
        private Optional<List<Choice>> ballots = Optional.empty();

        private void check(final Object input) {
            Objects.requireNonNull(input);
        }
        private void checkString(final String input) {
            if (input.equals("")) {
                throw new IllegalArgumentException("Invalid " + input);
            }
        }

        @Override
        public ElectionBuilder goal(final String goal) {
            this.checkString(goal);
            this.goal = Optional.of(goal);
            return this;
        }

        @Override
        public ElectionBuilder voters(final long number) {
            this.votersNumber = Optional.of(number);
            return this;
        }

        @Override
        public ElectionBuilder start(final LocalDateTime start) {
            this.check(start);
            this.startingDate = Optional.of(start);
            return this;
        }

        @Override
        public ElectionBuilder end(final LocalDateTime end) {
            this.check(end);
            this.endingDate = Optional.of(end);
            return this;
        }

        @Override
        public ElectionBuilder choices(final List<Choice> choices) {
            this.check(choices);
            this.choices = Optional.of(List.copyOf(choices));
            return this;
        }

        @Override
        public ElectionBuilder results(final Map<Choice, Long> results) {
            this.check(results);
            this.results = Optional.of(results);
            return this;
        }

        @Override
        public ElectionBuilder ballots(List<Choice> ballots) {
            this.check(ballots);
            this.ballots = Optional.of(ballots);
            return null;
        }

        @Override
        public Election build() {
            return new ElectionImpl(this.goal.orElseThrow(),
                    this.votersNumber.orElseThrow(),
                    this.startingDate.orElseThrow(),
                    this.endingDate.orElseThrow(),
                    this.choices.orElseThrow(),
                    this.results.orElse(null),
                    this.ballots.orElse(null));
        }
    }
}

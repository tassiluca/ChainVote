package it.unibo.ds.core.assets;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

import it.unibo.ds.core.manager.ElectionManagerImpl;
import it.unibo.ds.core.utils.Choice;
import it.unibo.ds.core.utils.FixedVotes;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.owlike.genson.annotation.JsonProperty;

import static it.unibo.ds.core.utils.Utils.isDateBetween;

/**
 * The {@link Election} implementation.
 */
@DataType()
public final class ElectionImpl implements Election {

    private static final boolean DEBUG = false;

    @Property()
    private final String electionID;

    @Property()
    private final String goal;

    @Property()
    private final long votersNumber;

    @Property()
    private final LocalDateTime startingDate;

    @Property()
    private final LocalDateTime endingDate;

    @Property()
    private final List<Choice> choices;

    @Property()
    private final Map<Choice, Long> results;

    // Only for debug
    @Property()
    private Map<String, Choice> voteAccountability;

    // Keep all the choices of the ballots (Not to query if the voter has already voted)
    @Property()
    private final List<Choice> ballots;

    private ElectionImpl(@JsonProperty("goal") final String goal,
                    @JsonProperty("votersNumber") final long votersNumber,
                         @JsonProperty("startingDate") final LocalDateTime startingDate,
                    @JsonProperty("endingDate") final LocalDateTime endingDate,
                         @JsonProperty("choices") final List<Choice> choices) {
        this.goal = goal;
        this.votersNumber = votersNumber;
        this.startingDate = startingDate;
        this.endingDate = endingDate;
        this.results = new HashMap<>();
        this.choices = List.copyOf(ElectionManagerImpl.getInstance().initializeChoice(choices));
        for (Choice choice: this.choices) {
            this.results.put(choice, (long) 0);
        }
        this.ballots = new ArrayList<>();
        if (DEBUG) {
            this.voteAccountability = new HashMap<>();
        }
        this.electionID = String.valueOf(this.hashCode());
    }

    @Override
    public String getElectionID() {
        return this.electionID;
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
        if (ElectionManagerImpl.getInstance().isBallotValid(this, ballot)) {
            this.ballots.add(ballot.getChoice());
            if (DEBUG) {
                this.voteAccountability.put(ballot.getVoterID(), ballot.getChoice());
            }
            long oldValue = this.results.get(ballot.getChoice());
            return this.results.replace(ballot.getChoice(), oldValue, oldValue + 1);
        }
        return false;
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
                + " [electionID=" + this.electionID
                + ", goal=" + this.goal + ", voters=" + this.votersNumber + ", starting="
                + this.startingDate + ", ending="
                + this.endingDate + ", choices=" + this.choices + "]";
    }

    /**
     * An {@link ElectionBuilder}'s implementation.
     */
    public static final class Builder implements ElectionBuilder {

        private static final long MIN_VOTERS = 1;
        private static final long MAX_VOTERS = 2_000_000_000;
        private Optional<String> goal;
        private Optional<Long> votersNumber;
        private Optional<LocalDateTime> startingDate;
        private Optional<LocalDateTime> endingDate;
        private Optional<List<Choice>> choices;

        private void check(final Object input) {
            Objects.requireNonNull(input);
        }
        private void checkString(final String input) {
            if (input.equals("")) {
                throw new IllegalArgumentException("Invalid goal " + input);
            }
        }

        private void checkNumber(final long input) {
            if (input < MIN_VOTERS || input > MAX_VOTERS) {
                throw new IllegalArgumentException("Invalid number of voters: input " + input
                        + " intervals accepted [" + MIN_VOTERS + ", " + MAX_VOTERS + "]");
            }
        }

        private void checkDates(final LocalDateTime start, final LocalDateTime end) {
            if (start.isAfter(end) || start.isEqual(end) || end.isAfter(LocalDateTime.now())) {
                throw new IllegalArgumentException("Invalid dates, start: " + start + " end: " + end
                        + "\nRequired a start before the end, and the end must be after now");
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
            this.checkNumber(number);
            this.votersNumber = Optional.of(number);
            return this;
        }

        @Override
        public ElectionBuilder start(final LocalDateTime start) {
            this.check(start);
            this.startingDate = Optional.of(start);
            this.endingDate.ifPresent(localDateTime -> this.checkDates(this.startingDate.get(), localDateTime));
            return this;
        }

        @Override
        public ElectionBuilder end(final LocalDateTime end) {
            this.check(end);
            this.endingDate = Optional.of(end);
            this.startingDate.ifPresent(localDateTime -> this.checkDates(localDateTime, this.endingDate.get()));
            return this;
        }

        private boolean areChoicesValidForBuilding(final List<Choice> choices) {
            return choices.stream().distinct()
                    .filter(choice -> !choice.equals(FixedVotes.INFORMAL_BALLOT.getChoice()))
                    .count() >= 2;
        }

        @Override
        public ElectionBuilder choices(final List<Choice> choices) {
            this.check(choices);
            if (areChoicesValidForBuilding(choices)) {
                this.choices = Optional.of(List.copyOf(choices));
            } else {
                throw new IllegalArgumentException("Invalid set of choices: " + choices
                        + "\nRequires at least 2 different choices without the blank choice");
            }
            return this;
        }

        @Override
        public Election build() {
            return new ElectionImpl(this.goal.orElseThrow(),
                    this.votersNumber.orElseThrow(),
                    this.startingDate.orElseThrow(),
                    this.endingDate.orElseThrow(),
                    this.choices.orElseThrow());
        }
    }
}

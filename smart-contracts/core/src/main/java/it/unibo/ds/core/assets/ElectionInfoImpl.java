package it.unibo.ds.core.assets;

import it.unibo.ds.core.utils.Choice;
import it.unibo.ds.core.utils.FixedVotes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static it.unibo.ds.core.utils.Utils.isDateBetween;

public class ElectionInfoImpl implements ElectionInfo {

    private final String goal;
    private final long votersNumber;
    private final LocalDateTime startingDate;
    private final LocalDateTime endingDate;
    private final List<Choice> choices;

    private ElectionInfoImpl(final String goal, final long votersNumber, final LocalDateTime startingDate,
                         final LocalDateTime endingDate, final List<Choice> choices) {
        this.goal = goal;
        this.votersNumber = votersNumber;
        this.startingDate = startingDate;
        this.endingDate = endingDate;
        this.choices = choices;
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
        return new ArrayList<>(this.choices);
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

        ElectionInfo other = (ElectionInfo) obj;

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
    public static final class Builder implements ElectionInfoBuilder {

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
                throw new IllegalArgumentException("Invalid " + input);
            }
        }

        @Override
        public ElectionInfoBuilder goal(final String goal) {
            this.checkString(goal);
            this.goal = Optional.of(goal);
            return this;
        }

        @Override
        public ElectionInfoBuilder voters(final long number) {
            this.votersNumber = Optional.of(number);
            return this;
        }

        @Override
        public ElectionInfoBuilder start(final LocalDateTime start) {
            this.check(start);
            this.startingDate = Optional.of(start);
            return this;
        }

        @Override
        public ElectionInfoBuilder end(final LocalDateTime end) {
            this.check(end);
            this.endingDate = Optional.of(end);
            return this;
        }

        @Override
        public ElectionInfoBuilder choices(final List<Choice> choices) {
            this.check(choices);
            this.choices = Optional.of(List.copyOf(choices));
            return this;
        }

        @Override
        public ElectionInfo build() {
            return new ElectionInfoImpl(this.goal.orElseThrow(),
                    this.votersNumber.orElseThrow(),
                    this.startingDate.orElseThrow(),
                    this.endingDate.orElseThrow(),
                    this.choices.orElseThrow());
        }
    }
}

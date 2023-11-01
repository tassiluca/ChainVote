package it.unibo.ds.chainvote.assets;

import it.unibo.ds.chainvote.utils.Choice;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link Ballot} implementation.
 */
public final class BallotImpl implements Ballot {

    private final String electionID;
    private final String voterCodeID;
    private final LocalDateTime date;
    private final Choice choice;

    private BallotImpl(
        final String electionID,
        final String voterCodeID,
        final LocalDateTime date,
       final Choice choice
    ) {
        this.electionID = electionID;
        this.voterCodeID = voterCodeID;
        this.date = date;
        this.choice = choice;
    }

    @Override
    public String getElectionId() {
        return this.electionID;
    }

    @Override
    public String getVoterId() {
        return this.voterCodeID;
    }

    @Override
    public LocalDateTime getDate() {
        return this.date;
    }

    @Override
    public Choice getChoice() {
        return this.choice;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Ballot other = (Ballot) obj;
        return getDate().equals(other.getDate())
            && getChoice().equals(other.getChoice())
            && Objects.deepEquals(
                new String[] {getElectionId(), getVoterId()},
                new String[] {other.getElectionId(), other.getVoterId()}
            );
    }

    @Override
    public int hashCode() {
        return Objects.hash(getElectionId(), getVoterId(), getDate(), getChoice());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + " [electionID="
            + this.electionID + ", voterID="
            + this.voterCodeID + ", date=" + this.date + ", choice=" + this.choice + "]";
    }

    /**
     * A {@link BallotBuilder}'s implementation.
     */
    public static final class Builder implements BallotBuilder {

        private Optional<String> electionId;
        private Optional<String> voterId;
        private Optional<LocalDateTime> date;
        private Optional<Choice> choice;

        private void check(final Object input) {
            Objects.requireNonNull(input);
        }

        private void checkString(final String input, final String inputValue) {
            if (input.isEmpty()) {
                throw new IllegalArgumentException("Invalid " + inputValue + ": " + input);
            }
        }

        @Override
        public BallotBuilder electionID(final String electionID) {
            checkString(electionID, "electionID");
            this.electionId = Optional.of(electionID);
            return this;
        }

        @Override
        public BallotBuilder voterID(final String voterID) {
            checkString(voterID, "voterID");
            this.voterId = Optional.of(voterID);
            return this;
        }

        @Override
        public BallotBuilder date(final LocalDateTime date) {
            check(date);
            this.date = Optional.of(date);
            return this;
        }

        @Override
        public BallotBuilder choice(final Choice choice) {
            check(choice);
            this.choice = Optional.of(choice);
            return this;
        }

        @Override
        public Ballot build() {
            return new BallotImpl(this.electionId.orElseThrow(),
                this.voterId.orElseThrow(),
                this.date.orElseThrow(),
                this.choice.orElseThrow());
        }
    }
}

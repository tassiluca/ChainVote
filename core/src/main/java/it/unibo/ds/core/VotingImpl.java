package it.unibo.ds.core;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A simple implementation of a {@link Voting}.
 */
public final class VotingImpl implements Voting {

    private final String name;
    private final String question;
    private final List<String> choices;
    private final LocalDateTime openingDate;
    private final LocalDateTime closingDate;

    private VotingImpl(final String name, final String question, final List<String> choices,
               final LocalDateTime openingDate, final LocalDateTime closingDate) {
        this.name = name;
        this.question = question;
        this.choices = choices;
        this.openingDate = openingDate;
        this.closingDate = closingDate;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String question() {
        return question;
    }

    @Override
    public List<String> choices() {
        return Collections.unmodifiableList(this.choices);
    }

    @Override
    public LocalDateTime openingDate() {
        return openingDate;
    }

    @Override
    public LocalDateTime closingDate() {
        return closingDate;
    }

    @Override
    public boolean isOpen() {
        final var now = LocalDateTime.now();
        return now.isAfter(openingDate) && now.isBefore(closingDate);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        } else if (o == null || getClass() != o.getClass()) {
            return false;
        } else {
            final VotingImpl voting = (VotingImpl) o;
            return Objects.equals(name, voting.name) && Objects.equals(question, voting.question)
                && Objects.equals(choices, voting.choices) && Objects.equals(openingDate, voting.openingDate)
                && Objects.equals(closingDate, voting.closingDate);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, question, choices, openingDate, closingDate);
    }

    @Override
    public String toString() {
        return "VotingImpl{"
            + "name='" + name + '\''
            + ", question='" + question + '\''
            + ", choices=" + choices
            + ", openingDate=" + openingDate
            + ", closingDate=" + closingDate
            + '}';
    }

    /**
     * A builder for {@link Voting} instances.
     */
    public static final class Builder {

        private String name;
        private String question;
        private final List<String> choices = new ArrayList<>();
        private LocalDateTime openingDate;
        private LocalDateTime closingDate;
        private boolean built;

        /**
         * Set the name of the voting.
         * @param name a string representing the name of the voting.
         * @return this builder instance.
         */
        public Builder name(final String name) {
            this.name = name;
            return this;
        }

        /**
         * Set the question of the voting.
         * @param question a string representing the question of the voting.
         * @return this builder instance.
         */
        public Builder question(final String question) {
            this.question = question;
            return this;
        }

        /**
         * Add a new choice option for this voting.
         * @param choice a string representing a choice option of the voting.
         * @return this builder instance.
         */
        public Builder choice(final String choice) {
            this.choices.add(requireIsLegal(choice));
            return this;
        }

        /**
         * Set the opening date of the voting.
         * @param openingDate a {@link java.time.LocalDateTime} representing the opening date of the voting.
         * @return this builder instance.
         */
        public Builder openAt(final LocalDateTime openingDate) {
            this.openingDate = openingDate;
            return this;
        }

        /**
         * Set the closing date of the voting.
         * @param closingDate a {@link java.time.LocalDateTime} representing the closing date of the voting.
         * @return this builder instance.
         */
        public Builder closeAt(final LocalDateTime closingDate) {
            this.closingDate = closingDate;
            return this;
        }

        /**
         * @return a new immutable instance of {@link Voting}.
         */
        public Voting build() {
            if (built) {
                throw new IllegalStateException("This builder can be used once!");
            } else if (requireIsLegal(closingDate).isBefore(openingDate) || requireIsLegal(closingDate).isEqual(openingDate)) {
                throw new IllegalArgumentException("The closing date precedes or is equal to the opening one.");
            } else if (choices.size() <= 1) {
                throw new IllegalArgumentException("No choices supplied.");
            } else if (choices.stream().map(String::trim).distinct().count() < choices.size()) {
                throw new IllegalArgumentException("Duplicate choices exists.");
            }
            built = true;
            return new VotingImpl(requireIsLegal(name), requireIsLegal(question), choices, openingDate, closingDate);
        }

        private <X> X requireIsLegal(final X data) {
            Objects.requireNonNull(data, "Some field is null!");
            if (data instanceof String && ((String) data).isBlank()) {
                throw new IllegalArgumentException("Some field is empty!");
            }
            return data;
        }
    }
}

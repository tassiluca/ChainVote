package it.unibo.ds.core;

import java.time.LocalDateTime;
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

    VotingImpl(final String name, final String question, final List<String> choices,
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
}

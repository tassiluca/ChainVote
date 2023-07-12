package it.unibo.ds.core;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * A simple factory to create a new {@link Voting}.
 */
public final class VotingFactory {

    /**
     * Creates a new instance of a {@link Voting}.
     * @param name the name of the voting
     * @param question the question of the voting
     * @param choices the possible choices of the voting.
     * @param openingDate the opening date of the voting.
     * @param closingDate the closing date of the voting.
     * @return a new immutable instance of a voting.
     */
    public Voting create(final String name, final String question, final List<String> choices,
                         final LocalDateTime openingDate, final LocalDateTime closingDate) {
        if (closingDate.isBefore(openingDate) || closingDate.isEqual(openingDate)) {
            throw new IllegalArgumentException("The closing date precedes or is equal to the opening one.");
        } else if (choices.size() <= 1) {
            throw new IllegalArgumentException("No choices supplied.");
        }
        return new VotingImpl(requireNonNull(name), requireNonNull(question),
                requireNonNull(choices), requireNonNull(openingDate), requireNonNull(closingDate));
    }

    private record VotingImpl(String name, String question, List<String> choices,
                              LocalDateTime openingDate, LocalDateTime closingDate) implements Voting {

        @Override
        public List<String> choices() {
            return Collections.unmodifiableList(this.choices);
        }

        @Override
        public boolean isOpen() {
            final var now = LocalDateTime.now();
            return now.isAfter(openingDate) && now.isBefore(closingDate);
        }
    }
}

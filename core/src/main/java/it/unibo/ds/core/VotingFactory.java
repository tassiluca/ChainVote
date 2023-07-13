package it.unibo.ds.core;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * A simple factory for {@link VotingImpl}.
 */
public class VotingFactory {

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
}

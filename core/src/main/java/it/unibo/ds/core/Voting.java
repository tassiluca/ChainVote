package it.unibo.ds.core;

import java.time.LocalDateTime;
import java.util.List;

/**
 * An interface modeling a voting.
 */
public interface Voting {

    /**
     * @return the name of the voting.
     */
    String name();

    /**
     * @return the question of the voting.
     */
    String question();

    /**
     * @return a list describing the possible choices.
     */
    List<String> choices();

    /**
     * @return the opening date.
     */
    LocalDateTime openingDate();

    /**
     * @return the closing date.
     */
    LocalDateTime closingDate();

    /**
     * @return if this voting is still open, i.e. a new vote can be cast, or not.
     */
    boolean isOpen();
}

package it.unibo.ds.core;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VotingTest {

    @Test
    void testCorrectSetup() {
        final String name = "test";
        final String question = "Who do you want to be the next representative?";
        final List<String> choices = List.of("A", "B", "C", "D");
        final LocalDateTime opening = LocalDateTime.now();
        final LocalDateTime closing = LocalDateTime.MAX;
        final var voting = new VotingFactory().create(name, question, choices, opening, closing);
        assertTrue(voting.isOpen());
    }

    @Test
    void testFailWithNoChoices() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new VotingFactory().create("testWithNoChoices", "a", List.of("b"), LocalDateTime.now(), LocalDateTime.MAX)
        );
    }

    @Test
    void testFailWithNoLegitimateDates() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new VotingFactory().create("testWithWrongDates", "a", List.of("b", "c"), LocalDateTime.MAX, LocalDateTime.now())
        );
    }

    @Test
    void testFailWithNullValues() {
        assertThrows(
            NullPointerException.class,
            () -> new VotingFactory().create("testWithNull", "a", null, LocalDateTime.now(), LocalDateTime.MAX)
        );
    }
}

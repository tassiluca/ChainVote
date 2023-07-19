package it.unibo.ds.core;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VotingTest {

    @Test
    void testCorrectSetup() {
        final var voting = new VotingImpl.Builder()
            .name("test")
            .question("Yes or No?")
            .choice("Yes")
            .choice("No")
            .openAt(LocalDateTime.now())
            .closeAt(LocalDateTime.MAX)
            .build();
        assertTrue(voting.isOpen());
    }

    @Test
    void testFailWithNoChoices() {
        assertThrows(IllegalArgumentException.class, () ->
            new VotingImpl.Builder()
                .name("testWithNoChoices")
                .question("a")
                .choice("b")
                .openAt(LocalDateTime.now())
                .closeAt(LocalDateTime.MAX)
                .build()
        );
    }

    @Test
    void testFailWithDuplicateChoices() {
        assertThrows(IllegalArgumentException.class, () ->
            new VotingImpl.Builder()
                .name("testWithDuplicateChoices")
                .question("a")
                .choice("A non trivial choice")
                .choice("A non trivial choice ")
                .openAt(LocalDateTime.now())
                .closeAt(LocalDateTime.MAX)
                .build()
        );
    }

    @Test
    void testFailWithNoLegitimateDates() {
        assertThrows(IllegalArgumentException.class, () ->
            new VotingImpl.Builder()
                .name("testWithWrongDates")
                .question("a")
                .choice("b")
                .choice("c")
                .openAt(LocalDateTime.MAX)
                .closeAt(LocalDateTime.now())
                .build()
        );
    }

    @Test
    void testFailWithEmptyValues() {
        assertThrows(IllegalArgumentException.class, () ->
            new VotingImpl.Builder()
                .name("")
                .question("")
                .choice("a")
                .choice("b")
                .openAt(LocalDateTime.now())
                .closeAt(LocalDateTime.MAX)
                .build()
        );
    }

    @Test
    void testFailWithNullValues() {
        assertThrows(NullPointerException.class, () ->
            new VotingImpl.Builder()
                .name("testWithNull")
                .question("a")
                .choice(null)
                .choice("c")
                .openAt(LocalDateTime.now())
                .closeAt(LocalDateTime.MAX)
                .build()
        );
    }

    @Test
    void testBuilderCanBeUsedOnce() {
        final var builder = new VotingImpl.Builder()
            .name("test")
            .question("Yes or No?")
            .choice("Yes")
            .choice("No")
            .openAt(LocalDateTime.now())
            .closeAt(LocalDateTime.MAX);
        builder.build();
        assertThrows(IllegalStateException.class, builder::build);
    }
}

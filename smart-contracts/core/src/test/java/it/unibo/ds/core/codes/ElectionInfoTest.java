package it.unibo.ds.core.codes;

import it.unibo.ds.core.assets.ElectionInfo;
import it.unibo.ds.core.factory.ElectionFactory;
import it.unibo.ds.core.utils.Choice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import it.unibo.ds.core.utils.FixedVotes;
import it.unibo.ds.core.utils.Utils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

final class ElectionInfoTest {

    private static final String GOAL = "prova";
    private static final long VOTERS = 1_000_000L;
    private static final Map<String, Integer> START_TIME_MAP = Map.of(
        "y", 2022,
        "M", 8,
        "d", 18,
        "h", 10,
        "m", 0,
        "s", 0
    );
    private static final Map<String, Integer> END_TIME_MAP = Map.of(
        "y", 2022,
        "M", 8,
        "d", 22,
        "h", 10,
        "m", 0,
        "s", 0
    );
    private static final LocalDateTime START_DATE = LocalDateTime.of(
        START_TIME_MAP.get("y"),
        START_TIME_MAP.get("M"),
        START_TIME_MAP.get("d"),
        START_TIME_MAP.get("h"),
        START_TIME_MAP.get("m"),
        START_TIME_MAP.get("s")
    );
    private static final LocalDateTime END_DATE = LocalDateTime.of(
        END_TIME_MAP.get("y"),
        END_TIME_MAP.get("M"),
        END_TIME_MAP.get("d"),
        END_TIME_MAP.get("h"),
        END_TIME_MAP.get("m"),
        END_TIME_MAP.get("s")
    );
    private static final List<Choice> CHOICES = new ArrayList<>(List.of(
        new Choice("test1"),
        new Choice("test2"),
        new Choice("test3"))
    );
    private static final ElectionInfo ELECTION_INFO = ElectionFactory
            .buildElectionInfo(GOAL, VOTERS, START_DATE, END_DATE, CHOICES);

    @Nested
    class TestBuild {

        @Test
        void testCorrectBuild() {
            assertEquals(Utils.calculateID(GOAL, START_DATE, END_DATE, CHOICES), ELECTION_INFO.getElectionID());
            assertEquals(GOAL, ELECTION_INFO.getGoal());
            assertEquals(START_DATE, ELECTION_INFO.getStartingDate());
            assertEquals(END_DATE, ELECTION_INFO.getEndingDate());
            List<Choice> choiceToUse = new ArrayList<>(CHOICES);
            if (!choiceToUse.contains(FixedVotes.INFORMAL_BALLOT.getChoice())) {
                choiceToUse.add(FixedVotes.INFORMAL_BALLOT.getChoice());
            }
            assertTrue(ELECTION_INFO.getChoices().contains(FixedVotes.INFORMAL_BALLOT.getChoice()));
            assertEquals(choiceToUse, ELECTION_INFO.getChoices());
            if (Utils.isDateBetween(LocalDateTime.now(), START_DATE, END_DATE)) {
                assertTrue(ELECTION_INFO.isOpen());
            } else {
                assertFalse(ELECTION_INFO.isOpen());
            }
        }

        @Test
        void testWrongBuildEmptyGoal() {
            String goal = "";
            assertThrows(IllegalArgumentException.class, () ->
                ElectionFactory.buildElectionInfo(goal, VOTERS, START_DATE, END_DATE, CHOICES)
            );
        }

        @Test
        void testWrongBuildZeroVoters() {
            long voters = 0;
            assertThrows(IllegalArgumentException.class, () ->
                ElectionFactory.buildElectionInfo(GOAL, voters, START_DATE, END_DATE, CHOICES)
            );
        }

        @Test
        void testWrongBuildInvalidDates() {
            assertThrows(NullPointerException.class, () ->
                ElectionFactory.buildElectionInfo(GOAL, VOTERS, null, END_DATE, CHOICES)
            );
            assertThrows(NullPointerException.class, () ->
                ElectionFactory.buildElectionInfo(GOAL, VOTERS, START_DATE, null, CHOICES));
            Map<String, Integer> startMapSame = Map.of(
                "y", 2022,
                "M", 8,
                "d", 22,
                "h", 10,
                "m", 0,
                "s", 0
            );
            Map<String, Integer> endMapSame = Map.of(
                "y", 2022,
                "M", 8,
                "d", 22,
                "h", 10,
                "m", 0,
                "s", 0
            );
            LocalDateTime startDateSame = LocalDateTime.of(
                startMapSame.get("y"),
                startMapSame.get("M"),
                startMapSame.get("d"),
                startMapSame.get("h"),
                startMapSame.get("m"),
                startMapSame.get("s")
            );
            LocalDateTime endDateSame = LocalDateTime.of(
                endMapSame.get("y"),
                endMapSame.get("M"),
                endMapSame.get("d"),
                endMapSame.get("h"),
                endMapSame.get("m"),
                endMapSame.get("s")
            );
            assertThrows(IllegalArgumentException.class, () ->
                ElectionFactory.buildElectionInfo(GOAL, VOTERS, startDateSame, endDateSame, CHOICES)
            );
            Map<String, Integer> startMapBigger = Map.of(
                "y", 2024,
                "M", 8,
                "d", 22,
                "h", 10,
                "m", 0,
                "s", 0
            );
            Map<String, Integer> endMapLower = Map.of(
                "y", 2022,
                "M", 8,
                "d", 22,
                "h", 10,
                "m", 0,
                "s", 0
            );
            LocalDateTime startDateBigger = LocalDateTime.of(
                startMapBigger.get("y"),
                startMapBigger.get("M"),
                startMapBigger.get("d"),
                startMapBigger.get("h"),
                startMapBigger.get("m"),
                startMapBigger.get("s")
            );
            LocalDateTime endDateLower = LocalDateTime.of(
                endMapLower.get("y"),
                endMapLower.get("M"),
                endMapLower.get("d"),
                endMapLower.get("h"),
                endMapLower.get("m"),
                endMapLower.get("s")
            );
            assertThrows(IllegalArgumentException.class, () ->
                ElectionFactory.buildElectionInfo(GOAL, VOTERS, startDateBigger, endDateLower, CHOICES)
            );
        }

        @Test
        void testWrongBuildInvalidChoices() {
            assertThrows(NullPointerException.class,
                () -> ElectionFactory.buildElectionInfo(GOAL, VOTERS, START_DATE, END_DATE, null)
            );
            List<Choice> onlyAChoice = new ArrayList<>(List.of(new Choice("test1")));
            assertThrows(IllegalArgumentException.class, () ->
                ElectionFactory.buildElectionInfo(GOAL, VOTERS, START_DATE, END_DATE, onlyAChoice)
            );
            List<Choice> onlyAChoiceAndABlank = new ArrayList<>(List.of(
                new Choice("test1"),
                FixedVotes.INFORMAL_BALLOT.getChoice())
            );
            assertThrows(IllegalArgumentException.class, () ->
                ElectionFactory.buildElectionInfo(GOAL, VOTERS, START_DATE, END_DATE, onlyAChoiceAndABlank)
            );
            List<Choice> duplicateChoices = new ArrayList<>(List.of(
                new Choice("test1"),
                new Choice("test2"),
                new Choice("test2"))
            );
            assertThrows(IllegalArgumentException.class, () ->
                ElectionFactory.buildElectionInfo(GOAL, VOTERS, START_DATE, END_DATE, duplicateChoices)
            );
            List<Choice> duplicateBlankChoices = new ArrayList<>(List.of(
                new Choice("test1"),
                FixedVotes.INFORMAL_BALLOT.getChoice(),
                FixedVotes.INFORMAL_BALLOT.getChoice(),
                new Choice("test2"))
            );
            assertThrows(IllegalArgumentException.class, () ->
                ElectionFactory.buildElectionInfo(GOAL, VOTERS, START_DATE, END_DATE, duplicateBlankChoices)
            );
        }
    }
}

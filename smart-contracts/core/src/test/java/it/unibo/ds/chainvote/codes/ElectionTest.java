package it.unibo.ds.chainvote.codes;

import it.unibo.ds.chainvote.assets.Ballot;
import it.unibo.ds.chainvote.assets.BallotImpl;
import it.unibo.ds.chainvote.assets.Election;
import it.unibo.ds.chainvote.assets.ElectionInfo;
import it.unibo.ds.chainvote.factory.ElectionFactory;
import it.unibo.ds.chainvote.manager.ElectionManagerImpl;
import it.unibo.ds.chainvote.utils.Choice;
import it.unibo.ds.chainvote.utils.Utils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class ElectionTest {

    private static final String GOAL = "prova";
    private static final long VOTERS = 1_000_000;
    private static final Map<String, Integer> START_TIME_MAP = Map.of(
        "y", LocalDateTime.now().getYear() - 1,
        "M", 8,
        "d", 18,
        "h", 10,
        "m", 0,
        "s", 0
    );
    private static final Map<String, Integer> END_TIME_MAP = Map.of(
        "y", LocalDateTime.now().getYear() + 1,
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
    static class TestBuild {

        @Test
        void testStandardCorrectBuild() {
            assertDoesNotThrow(() -> ElectionFactory.buildElection(ELECTION_INFO));
            Election election = ElectionFactory.buildElection(ELECTION_INFO);
            Function<Choice, Long> valueMapper = c -> 0L;
            Map<Choice, Long> resultsToCheck = ELECTION_INFO.getChoices().stream()
                .collect(Collectors.toMap(Function.identity(), valueMapper));
            assertEquals(resultsToCheck, election.getResults());
            assertEquals(new ArrayList<>(), election.getBallots());
        }

        @Test
        void testCorrectBuildWithResults() {
            int size = ELECTION_INFO.getChoices().size();
            Function<Choice, Long> valueMapper = c -> (long) (VOTERS / size);
            Map<Choice, Long> resultsToSet = ELECTION_INFO.getChoices().stream()
                .collect(Collectors.toMap(Function.identity(), valueMapper));
            assertDoesNotThrow(() -> ElectionFactory
                .buildElection(ELECTION_INFO, resultsToSet));
        }

        @Test
        void testCorrectBuildElectionClosedWithResults() {
            Map<String, Integer> endMap = Map.of(
                "y", START_TIME_MAP.get("y"),
                "M", START_TIME_MAP.get("M"),
                "d", START_TIME_MAP.get("d") + 1,
                "h", START_TIME_MAP.get("h"),
                "m", START_TIME_MAP.get("m"),
                "s", START_TIME_MAP.get("s")
            );
            LocalDateTime endDate = LocalDateTime.of(
                endMap.get("y"),
                endMap.get("M"),
                endMap.get("d"),
                endMap.get("h"),
                endMap.get("m"),
                endMap.get("s")
            );
            ElectionInfo electionInfo = ElectionFactory.buildElectionInfo(GOAL, VOTERS, START_DATE, endDate, CHOICES);
            int size = ELECTION_INFO.getChoices().size();
            Function<Choice, Long> valueMapper = c -> (long) (VOTERS / (size));
            Map<Choice, Long> resultsToSet = ELECTION_INFO.getChoices().stream()
                .collect(Collectors.toMap(Function.identity(), valueMapper));
            assertDoesNotThrow(() -> ElectionFactory.buildElection(electionInfo, resultsToSet));
        }

        @Test
        void testCorrectBuildWithResultsNotContainingAllChoicesInElectionChoices() {
            Function<Choice, Long> valueMapper = c -> 0L;
            Map<Choice, Long> resultsToSet = ELECTION_INFO.getChoices().stream()
                .collect(Collectors.toMap(Function.identity(), valueMapper));
            resultsToSet.remove(ELECTION_INFO.getChoices().get(0));
            assertDoesNotThrow(() -> ElectionFactory.buildElection(ELECTION_INFO, resultsToSet));
            Election election = ElectionFactory.buildElection(ELECTION_INFO, resultsToSet);
            assertEquals(new HashSet<>(ELECTION_INFO.getChoices()), election.getResults().keySet());
        }

        @Test
        void testWrongBuildWithResultsCountOverflow() {
            int size = ELECTION_INFO.getChoices().size();
            Function<Choice, Long> valueMapper = c -> (long) (VOTERS / (size - 1));
            Map<Choice, Long> resultsToSet = ELECTION_INFO.getChoices().stream()
                .collect(Collectors.toMap(Function.identity(), valueMapper));
            assertThrows(IllegalArgumentException.class, () -> ElectionFactory.buildElection(ELECTION_INFO, resultsToSet));
        }

        @Test
        void testWrongBuildWithResultsChoicesNotInElectionChoices() {
            Function<Choice, Long> valueMapper = c -> 0L;
            Map<Choice, Long> resultsToSet = ELECTION_INFO.getChoices().stream()
                .collect(Collectors.toMap(Function.identity(), valueMapper));
            resultsToSet.put(new Choice("Different choice"), 0L);
            assertThrows(IllegalArgumentException.class, () -> ElectionFactory.buildElection(ELECTION_INFO, resultsToSet));
        }

        @Test
        void testWrongBuildElectionClosedWithoutResults() {
            Map<String, Integer> endMap = Map.of(
                "y", START_TIME_MAP.get("y"),
                "M", START_TIME_MAP.get("M"),
                "d", START_TIME_MAP.get("d") + 1,
                "h", START_TIME_MAP.get("h"),
                "m", START_TIME_MAP.get("m"),
                "s", START_TIME_MAP.get("s")
            );
            LocalDateTime endDate = LocalDateTime.of(
                endMap.get("y"),
                endMap.get("M"),
                endMap.get("d"),
                endMap.get("h"),
                endMap.get("m"),
                endMap.get("s")
            );
            ElectionInfo electionInfo = ElectionFactory.buildElectionInfo(GOAL, VOTERS, START_DATE, endDate, CHOICES);
            assertThrows(IllegalArgumentException.class, () -> ElectionFactory.buildElection(electionInfo));
        }
    }

    @Nested
    static class TestCastVotes {

        @Test
        void testCorrectVote() {
            final Election election = ElectionFactory.buildElection(ELECTION_INFO);
            assertEquals(0L, election.getResults().values().stream().reduce(Long::sum).orElseThrow());
            final Choice castedChoice = ELECTION_INFO.getChoices().get(0);
            Ballot ballot = new BallotImpl.Builder()
                .electionID(Utils.calculateID(GOAL, START_DATE, END_DATE, CHOICES))
                .voterID("voter1")
                .date(LocalDateTime.now())
                .choice(castedChoice)
                .build();
            ElectionManagerImpl.getInstance().castVote(election, ELECTION_INFO, ballot);
            assertEquals(1L, election.getResults().values().stream().reduce(Long::sum).orElseThrow());
            assertEquals(1L, election.getResults().get(castedChoice));
            assertEquals(castedChoice, election.getBallots().get(0));
        }

        @Test
        void testWrongVoteChoiceNotInChoice() {
            final Election election = ElectionFactory.buildElection(ELECTION_INFO);
            Choice castedChoice = new Choice("Wrong choice");
            Ballot ballot = new BallotImpl.Builder()
                .electionID(Utils.calculateID(GOAL, START_DATE, END_DATE, CHOICES))
                .voterID("voter1")
                .date(LocalDateTime.now())
                .choice(castedChoice)
                .build();
            assertThrows(IllegalArgumentException.class, () ->
                ElectionManagerImpl.getInstance().castVote(election, ELECTION_INFO, ballot)
            );
        }

        @Test
        void testWrongVoteCastExceedVotersNumber() {
            final int size = ELECTION_INFO.getChoices().size();
            final Function<Choice, Long> valueMapper = c -> (long) (VOTERS / size);
            final Map<Choice, Long> resultsToSet = ELECTION_INFO.getChoices().stream()
                .collect(Collectors.toMap(Function.identity(), valueMapper));
            final Election election = ElectionFactory.buildElection(ELECTION_INFO, resultsToSet);
            int i = 0;
            while (election.getResults().values().stream().reduce(Long::sum).orElseThrow() < VOTERS) {
                Choice castedChoice = ELECTION_INFO.getChoices().get(0);
                Ballot ballot = new BallotImpl.Builder()
                    .electionID(Utils.calculateID(GOAL, START_DATE, END_DATE, CHOICES))
                    .voterID("voter" + i++)
                    .date(LocalDateTime.now())
                    .choice(castedChoice)
                    .build();
                ElectionManagerImpl.getInstance().castVote(election, ELECTION_INFO, ballot);
            }
            final Choice castedChoice = ELECTION_INFO.getChoices().get(0);
            final Ballot ballot = new BallotImpl.Builder()
                .electionID(Utils.calculateID(GOAL, START_DATE, END_DATE, CHOICES))
                .voterID("voter" + i)
                .date(LocalDateTime.now())
                .choice(castedChoice)
                .build();
            assertThrows(IllegalArgumentException.class, () ->
                ElectionManagerImpl.getInstance().castVote(election, ELECTION_INFO, ballot)
            );
        }
    }
}

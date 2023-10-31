package it.unibo.ds.chainvote.contract;

import it.unibo.ds.chainvote.utils.Choice;
import it.unibo.ds.chainvote.utils.FixedVotes;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class ElectionInfoContractTest {

    private static final String GOAL = "a test for election";
    private static final long VOTERS = 400_000_000L;
    private static final Map<String, Integer> START_TIME_MAP = Map.of(
        "y", 2022,
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
    private static final LocalDateTime START_DATE = LocalDateTime.of(START_TIME_MAP.get("y"), START_TIME_MAP.get("M"), START_TIME_MAP.get("d"),
        START_TIME_MAP.get("h"), START_TIME_MAP.get("m"), START_TIME_MAP.get("s"));
    private static final LocalDateTime END_DATE = LocalDateTime.of(END_TIME_MAP.get("y"), END_TIME_MAP.get("M"), END_TIME_MAP.get("d"),
        END_TIME_MAP.get("h"), END_TIME_MAP.get("m"), END_TIME_MAP.get("s"));
    private static final List<Choice> CHOICE_ELECTION = List.of(new Choice("test-choice-1"), new Choice("test-choice-2"),
        new Choice("test-choice-3"), new Choice("test-choice-4"), new Choice("test-choice-5"));
    private ElectionInfoContract ec;
    private Context context;
    private ChaincodeStub stub;

    @BeforeEach
    void setup() {
        context = mock(Context.class);
        stub = mock(ChaincodeStub.class);
        when(context.getStub()).thenReturn(stub);
    }

    @Nested
    class TestCreateElectionInfo {

        @BeforeEach
        void setup() {
            ec = mock(ElectionInfoContract.class);
        }

        @Nested
        class TestCorrectlyCreateElectionInfo {

            @Test
            void whenCreateElectionInfoCorrectly() {
                assertDoesNotThrow(() -> ec.createElectionInfo(context, GOAL, VOTERS, START_DATE.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), END_DATE.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), CHOICE_ELECTION));
            }
        }

        @Nested
        class TestFailToCreateElection {

            @Test
            void whenCreateElectionWithWrongDate() {
                ElectionInfoContract electionInfoContract = new ElectionInfoContract();
                assertThrows(ChaincodeException.class, () -> electionInfoContract.createElectionInfo(context, GOAL, VOTERS, START_DATE.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), START_DATE.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), CHOICE_ELECTION));
            }

            @Test
            void whenCreateElectionWithEmptyOrOnlyBlankChoice() {
                ElectionInfoContract electionInfoContract = new ElectionInfoContract();
                assertThrows(ChaincodeException.class, () -> electionInfoContract.createElectionInfo(context, GOAL, VOTERS, START_DATE.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), END_DATE.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), new ArrayList<Choice>()));
                assertThrows(ChaincodeException.class, () -> electionInfoContract.createElectionInfo(context, GOAL, VOTERS, START_DATE.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), END_DATE.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), new ArrayList<>(List.of(
                        FixedVotes.INFORMAL_BALLOT.getChoice(), FixedVotes.INFORMAL_BALLOT.getChoice(),
                        FixedVotes.INFORMAL_BALLOT.getChoice()))));
            }
        }
    }
}

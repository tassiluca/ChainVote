package it.unibo.ds.chainvote.contract;

import com.owlike.genson.Genson;
import it.unibo.ds.chaincode.utils.TransientData;
import it.unibo.ds.chainvote.assets.ElectionInfoAsset;
import it.unibo.ds.chainvote.presentation.GensonUtils;
import it.unibo.ds.core.factory.ElectionFactory;
import it.unibo.ds.core.utils.Choice;
import it.unibo.ds.core.utils.FixedVotes;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ElectionInfoContractTest {

    private static final String ELECTION_ID = "test-election";
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
        "y", LocalDateTime.now().getYear()+1,
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

    private static final ElectionInfoAsset ELECTION_INFO_ASSET = new ElectionInfoAsset(ELECTION_ID,
        ElectionFactory.buildElectionInfo(GOAL, VOTERS, START_DATE, END_DATE, CHOICE_ELECTION));
    private ElectionInfoContract ec;
    private final Genson genson = GensonUtils.create();
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
                assertDoesNotThrow(() -> ec.createElectionInfo(context, GOAL, VOTERS, START_DATE, END_DATE, CHOICE_ELECTION));
            }
        }

        @Nested
        class TestFailToCreateElection {

            @Test
            void whenCreateElectionWithWrongDate() {
                ElectionInfoContract electionInfoContract = new ElectionInfoContract();
                assertThrows(ChaincodeException.class, () -> electionInfoContract.createElectionInfo(context, GOAL, VOTERS, START_DATE, START_DATE, CHOICE_ELECTION));
            }

            @Test
            void whenCreateElectionWithEmptyOrOnlyBlankChoice() {
                ElectionInfoContract electionInfoContract = new ElectionInfoContract();
                assertThrows(ChaincodeException.class, () -> electionInfoContract.createElectionInfo(context, GOAL, VOTERS, START_DATE, END_DATE, new ArrayList<Choice>()));
                assertThrows(ChaincodeException.class, () -> electionInfoContract.createElectionInfo(context, GOAL, VOTERS, START_DATE, END_DATE, new ArrayList<>(List.of(
                        FixedVotes.INFORMAL_BALLOT.getChoice(), FixedVotes.INFORMAL_BALLOT.getChoice(),
                        FixedVotes.INFORMAL_BALLOT.getChoice()))));
            }
        }
    }


}

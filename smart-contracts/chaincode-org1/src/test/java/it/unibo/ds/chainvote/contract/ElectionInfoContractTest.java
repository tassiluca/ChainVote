package it.unibo.ds.chainvote.contract;

import com.owlike.genson.Genson;
import it.unibo.ds.chainvote.SerializersUtils;
import it.unibo.ds.chainvote.assets.ElectionInfo;
import it.unibo.ds.chainvote.factory.ElectionFactory;
import it.unibo.ds.chainvote.utils.Choice;
import it.unibo.ds.chainvote.utils.FixedVotes;
import it.unibo.ds.chainvote.utils.Utils;
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

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    private final ElectionInfoContract ELECTION_INFO_CONTRACT = spy(new ElectionInfoContract());
    private Context context;
    private ChaincodeStub stub;
    private final Genson genson = SerializersUtils.gensonInstance();

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
            when(stub.getStringState(Utils.calculateID(GOAL, START_DATE, END_DATE, CHOICE_ELECTION))).thenReturn(null);
        }

        @Nested
        class TestCorrectlyCreateElectionInfo {

            @Test
            void whenCreateElectionInfoCorrectly() {
                assertDoesNotThrow(() -> ELECTION_INFO_CONTRACT.createElectionInfo(context, GOAL, VOTERS, START_DATE.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), END_DATE.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), CHOICE_ELECTION));
            }
        }

        @Nested
        class TestFailToCreateElection {

            @Test
            void whenCreateElectionWithEmptyGoal() {
                final Throwable thrown = catchThrowable(() -> ELECTION_INFO_CONTRACT.createElectionInfo(context, "", VOTERS, START_DATE.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), START_DATE.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), CHOICE_ELECTION));
                assertThat(thrown)
                        .isInstanceOf(ChaincodeException.class);
                assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("INVALID_ARGUMENT".getBytes(UTF_8));
            }

            @Test
            void whenCreateElectionWithWrongDate() {
                final Throwable thrown = catchThrowable(() -> ELECTION_INFO_CONTRACT.createElectionInfo(context, GOAL, VOTERS, START_DATE.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), START_DATE.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), CHOICE_ELECTION));
                assertThat(thrown)
                        .isInstanceOf(ChaincodeException.class);
                assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("INVALID_ARGUMENT".getBytes(UTF_8));
            }

            @Test
            void whenCreateElectionWithInvalidChoices() {
                Throwable thrown;

                // Empty choices
                thrown = catchThrowable(() -> ELECTION_INFO_CONTRACT.createElectionInfo(context, GOAL, VOTERS, START_DATE.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), END_DATE.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), new ArrayList<>()));
                assertThat(thrown)
                        .isInstanceOf(ChaincodeException.class);
                assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("INVALID_ARGUMENT".getBytes(UTF_8));
                // Only blank choices
                thrown = catchThrowable(() -> ELECTION_INFO_CONTRACT.createElectionInfo(context, GOAL, VOTERS, START_DATE.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), END_DATE.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), new ArrayList<>(List.of(
                        FixedVotes.INFORMAL_BALLOT.getChoice(), FixedVotes.INFORMAL_BALLOT.getChoice(),
                        FixedVotes.INFORMAL_BALLOT.getChoice()))
                ));
                assertThat(thrown)
                        .isInstanceOf(ChaincodeException.class);
                assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("INVALID_ARGUMENT".getBytes(UTF_8));
                // Duplicate choices
                thrown = catchThrowable(() -> ELECTION_INFO_CONTRACT.createElectionInfo(context, GOAL, VOTERS, START_DATE.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), END_DATE.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), new ArrayList<>(List.of(
                                new Choice("test-choice-1"), new Choice("test-choice-1"),
                                FixedVotes.INFORMAL_BALLOT.getChoice()))
                        )
                );
                assertThat(thrown)
                        .isInstanceOf(ChaincodeException.class);
                assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("INVALID_ARGUMENT".getBytes(UTF_8));
            }
        }
    }

    @Nested
    class TestReadElectionInfo {

        private final ElectionInfo ei = ElectionFactory.buildElectionInfo(GOAL, VOTERS, START_DATE, END_DATE, CHOICE_ELECTION);

        @BeforeEach
        void setup() {
            // Put ElectionInfo inside state
            when(stub.getStringState(Utils.calculateID(GOAL, START_DATE, END_DATE, CHOICE_ELECTION))).thenReturn(genson.serialize(ei));
        }

        @Nested
        class TestCorrectlyReadElectionInfo {
            @Test
            void whenReadElectionInfoCorrectly() {
                assertDoesNotThrow(() -> ELECTION_INFO_CONTRACT.readElectionInfo(context, Utils.calculateID(ei)));
                assertEquals(ei, ELECTION_INFO_CONTRACT.readElectionInfo(context, Utils.calculateID(ei)));
            }
        }

        @Nested
        class TestFailToReadElectionInfo {
            @Test
            void whenReadElectionInfoWithWrongId() {
                String wrongId = Utils.calculateID(ei) + "fail";
                assertThrows(ChaincodeException.class, () -> ELECTION_INFO_CONTRACT.readElectionInfo(context, wrongId));
                final Throwable thrown = catchThrowable(() -> ELECTION_INFO_CONTRACT.readElectionInfo(context, wrongId));
                assertThat(thrown)
                        .isInstanceOf(ChaincodeException.class);
                assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("ELECTION_INFO_NOT_FOUND".getBytes(UTF_8));
            }
        }
    }

    @Nested
    class TestDeleteElectionInfo {

        private final ElectionInfo ei = ElectionFactory.buildElectionInfo(GOAL, VOTERS, START_DATE, END_DATE, CHOICE_ELECTION);

        @BeforeEach
        void setup() {
            // Put ElectionInfo inside state
            when(stub.getStringState(Utils.calculateID(GOAL, START_DATE, END_DATE, CHOICE_ELECTION))).thenReturn(genson.serialize(ei));
        }

        @Nested
        class TestCorrectlyDeleteElectionInfo {
            @Test
            void whenDeleteElectionInfoCorrectly() {
                assertDoesNotThrow(() -> ELECTION_INFO_CONTRACT.deleteElectionInfo(context, Utils.calculateID(ei)));
                assertEquals(ei, ELECTION_INFO_CONTRACT.deleteElectionInfo(context, Utils.calculateID(ei)));
            }
        }

        @Nested
        class TestFailToDeleteElectionInfo {
            @Test
            void whenDeleteElectionInfoWithWrongId() {
                String wrongId = Utils.calculateID(ei) + "fail";
                assertThrows(ChaincodeException.class, () -> ELECTION_INFO_CONTRACT.deleteElectionInfo(context, wrongId));
                final Throwable thrown = catchThrowable(() -> ELECTION_INFO_CONTRACT.deleteElectionInfo(context, wrongId));
                assertThat(thrown)
                        .isInstanceOf(ChaincodeException.class);
                assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("ELECTION_INFO_NOT_FOUND".getBytes(UTF_8));
            }
        }
    }
}

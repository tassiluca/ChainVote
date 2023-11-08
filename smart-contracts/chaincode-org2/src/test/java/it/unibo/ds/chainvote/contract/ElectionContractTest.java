package it.unibo.ds.chainvote.contract;

import com.owlike.genson.Genson;
import it.unibo.ds.chainvote.Response;
import it.unibo.ds.chainvote.SerializersUtils;
import it.unibo.ds.chainvote.assets.Election;
import it.unibo.ds.chainvote.assets.ElectionInfo;
import it.unibo.ds.chainvote.facade.ElectionFacadeImpl;
import it.unibo.ds.chainvote.factory.ElectionFactory;
import it.unibo.ds.chainvote.utils.Choice;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.Chaincode;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

final class ElectionContractTest {

    private static final String ELECTION_ID = "test-election";
    private static final String GOAL = "a test for election";
    private static final String CHANNEL_INFO_NAME_CH1 = "ch1";
    private static final String CHAINCODE_INFO_NAME_CH1 = "chaincode-org1";
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
    private static final List<Choice> CHOICE_ELECTION = List.of(
        new Choice("test-choice-1"),
        new Choice("test-choice-2"),
        new Choice("test-choice-3"),
        new Choice("test-choice-4"),
        new Choice("test-choice-5")
    );
    private static final ElectionInfo ELECTION_INFO =
        ElectionFactory.buildElectionInfo(GOAL, VOTERS, START_DATE, END_DATE, CHOICE_ELECTION);
    private static final Map<String, Long> RESULTS_FULL = CHOICE_ELECTION.stream()
            .collect(Collectors.toMap(Choice::getChoice, choice -> (long) CHOICE_ELECTION.indexOf(choice)));
    private static final Map<String, Long> RESULTS_EMPTY = new HashMap<>();
    private static final Election ELECTION_RESULTS_EMPTY = ElectionFactory.buildElection(ELECTION_INFO, RESULTS_EMPTY);
    private static final Election ELECTION_RESULTS_FULL = ElectionFactory.buildElection(ELECTION_INFO, RESULTS_FULL);
    private final Genson genson = SerializersUtils.gensonInstance();
    private Context context;
    private ChaincodeStub stub;
    private final ElectionContract ELECTION_CONTRACT = spy(new ElectionContract());

    @BeforeEach
    void setup() {
        context = mock(Context.class);
        stub = mock(ChaincodeStub.class);
        when(context.getStub()).thenReturn(stub);
    }

    @Nested
    class TestCreateElection {

        @Nested
        class TestCorrectlyCreateElection {
            @BeforeEach
            void setup() {
                when(context.getStub().invokeChaincodeWithStringArgs(
                        CHAINCODE_INFO_NAME_CH1,
                        List.of("ElectionInfoContract:readElectionInfo", ELECTION_ID),
                        CHANNEL_INFO_NAME_CH1
                )).thenReturn(
                        new Chaincode.Response(Chaincode.Response.Status.SUCCESS, "", genson.serialize(new Response<>(ELECTION_INFO)).getBytes(StandardCharsets.UTF_8))
                );
            }

            @Test
            void whenCreateElectionCorrectly() {
                // empty results
                assertDoesNotThrow(() -> ELECTION_CONTRACT.createElection(context, ELECTION_ID, RESULTS_EMPTY));
                assertEquals(ELECTION_RESULTS_EMPTY, ELECTION_CONTRACT.createElection(context, ELECTION_ID, RESULTS_EMPTY));
                // with full results
                assertDoesNotThrow(() -> ELECTION_CONTRACT.createElection(context, ELECTION_ID, RESULTS_FULL));
                assertEquals(ELECTION_RESULTS_FULL, ELECTION_CONTRACT.createElection(context, ELECTION_ID, RESULTS_FULL));
            }
        }

        @Nested
        class TestFailToCreateElection {

            @BeforeEach
            void setup() {
                when(context.getStub().invokeChaincodeWithStringArgs(
                        CHAINCODE_INFO_NAME_CH1,
                        List.of("ElectionInfoContract:readElectionInfo", ELECTION_ID),
                        CHANNEL_INFO_NAME_CH1
                )).thenReturn(
                        new Chaincode.Response(Chaincode.Response.Status.SUCCESS, "", genson.serialize(new Response<>(ELECTION_INFO)).getBytes(StandardCharsets.UTF_8))
                );
            }

            @Test
            void whenCreateElectionThatAlreadyExistsId() {
                doReturn(true).when(ELECTION_CONTRACT).electionExists(context, ELECTION_ID);
                assertThrows(ChaincodeException.class, () -> ELECTION_CONTRACT.createElection(context, ELECTION_ID, RESULTS_EMPTY));
                assertThrows(ChaincodeException.class, () -> ELECTION_CONTRACT.createElection(context, ELECTION_ID, RESULTS_FULL));

                final Throwable thrown = catchThrowable(() -> ELECTION_CONTRACT.createElection(context, ELECTION_ID, RESULTS_EMPTY));
                assertThat(thrown)
                        .isInstanceOf(ChaincodeException.class);
                assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("ELECTION_ALREADY_EXISTS".getBytes(UTF_8));
            }

            @Test
            void whenCreateElectionWithWrongId() {
                String wrongId = ELECTION_ID + "fail";
                // must be done like this since readElectionInfo is private and the exception has to be thrown there
                when(context.getStub().invokeChaincodeWithStringArgs(
                        CHAINCODE_INFO_NAME_CH1,
                        List.of("ElectionInfoContract:readElectionInfo", wrongId),
                        CHANNEL_INFO_NAME_CH1
                )).thenThrow(ChaincodeException.class);
                // empty results
                assertThrows(ChaincodeException.class, () -> ELECTION_CONTRACT.createElection(context, wrongId, RESULTS_EMPTY));
                // with full results
                assertThrows(ChaincodeException.class, () -> ELECTION_CONTRACT.createElection(context, wrongId, RESULTS_FULL));
            }

            @Test
            void whenCreateElectionWithWrongResults() {
                // Results with wrong choice
                final Map<String, Long> resultsWrongChoice = CHOICE_ELECTION.stream()
                    .collect(Collectors.toMap(Choice::getChoice, choice -> (long) CHOICE_ELECTION.indexOf(choice)));
                resultsWrongChoice.put(new Choice("wrong").getChoice(), 0L);
                assertThrows(ChaincodeException.class, () -> ELECTION_CONTRACT.createElection(context, ELECTION_ID, resultsWrongChoice));

                Throwable thrown = catchThrowable(() -> ELECTION_CONTRACT.createElection(context, ELECTION_ID, resultsWrongChoice));
                assertThat(thrown)
                        .isInstanceOf(ChaincodeException.class);
                assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("ELECTION_INVALID_BUILD_ARGUMENT".getBytes(UTF_8));

                // Results with negative value for choice
                final Map<String, Long> resultsNegativeValue = CHOICE_ELECTION.stream()
                        .collect(Collectors.toMap(Choice::getChoice, choice -> -1L));
                assertThrows(ChaincodeException.class, () -> ELECTION_CONTRACT.createElection(context, ELECTION_ID, resultsNegativeValue));

                thrown = catchThrowable(() -> ELECTION_CONTRACT.createElection(context, ELECTION_ID, resultsNegativeValue));
                assertThat(thrown)
                        .isInstanceOf(ChaincodeException.class);
                assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("ELECTION_INVALID_BUILD_ARGUMENT".getBytes(UTF_8));

                // Results with values overflow votersNumber choice
                final Map<String, Long> resultsWithOverflowValues = CHOICE_ELECTION.stream()
                        .collect(Collectors.toMap(Choice::getChoice, choice -> VOTERS));
                assertThrows(ChaincodeException.class, () -> ELECTION_CONTRACT.createElection(context, ELECTION_ID, resultsWithOverflowValues));

                thrown = catchThrowable(() -> ELECTION_CONTRACT.createElection(context, ELECTION_ID, resultsWithOverflowValues));
                assertThat(thrown)
                        .isInstanceOf(ChaincodeException.class);
                assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("ELECTION_INVALID_BUILD_ARGUMENT".getBytes(UTF_8));
            }
        }
    }

    @Nested
    class TestReadElection {

        @Nested
        class TestCorrectlyReadElection {
            @BeforeEach
            void setup() {
                when(context.getStub().invokeChaincodeWithStringArgs(
                        CHAINCODE_INFO_NAME_CH1,
                        List.of("ElectionInfoContract:readElectionInfo", ELECTION_ID),
                        CHANNEL_INFO_NAME_CH1
                )).thenReturn(
                        new Chaincode.Response(Chaincode.Response.Status.SUCCESS, "", genson.serialize(new Response<>(ELECTION_INFO)).getBytes(StandardCharsets.UTF_8))
                );
            }

            @Test
            void whenReadElectionCorrectly() {
                doReturn(true).when(ELECTION_CONTRACT).electionExists(context, ELECTION_ID);
                when(stub.getStringState(ELECTION_ID)).thenReturn(genson.serialize(ELECTION_RESULTS_EMPTY));
                assertDoesNotThrow(() -> ELECTION_CONTRACT.readElection(context, ELECTION_ID));
                assertEquals(new ElectionFacadeImpl(ELECTION_RESULTS_EMPTY, ELECTION_INFO),
                        ELECTION_CONTRACT.readElection(context, ELECTION_ID)
                );
            }
        }

        @Nested
        class TestWrongReadElection {
            @Test
            void whenReadElectionThatDoesntExist() {
                doReturn(false).when(ELECTION_CONTRACT).electionExists(context, ELECTION_ID);
                assertThrows(ChaincodeException.class, () -> ELECTION_CONTRACT.readElection(context, ELECTION_ID));

                final Throwable thrown = catchThrowable(() -> ELECTION_CONTRACT.readElection(context, ELECTION_ID));
                assertThat(thrown)
                        .isInstanceOf(ChaincodeException.class);
                assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("ELECTION_NOT_FOUND".getBytes(UTF_8));
            }
        }
    }
/*
    @Nested
    class TestCastVote {

        @Nested
        class TestCorrectlyCastVote {
            @BeforeEach
            void setup() {
                when(context.getStub().invokeChaincodeWithStringArgs(
                        CHAINCODE_INFO_NAME_CH1,
                        List.of("ElectionInfoContract:readElectionInfo", ELECTION_ID),
                        CHANNEL_INFO_NAME_CH1
                )).thenReturn(
                        new Chaincode.Response(Chaincode.Response.Status.SUCCESS, "", genson.serialize(new Response<>(ELECTION_INFO)).getBytes(StandardCharsets.UTF_8))
                );

                String userId = "user-test";
                String code = "code-test";
                when(context.getStub().getTransient()).thenReturn(Map.of(
                        "userId", userId.getBytes(UTF_8),
                        "code", code.getBytes(UTF_8))
                );
                CodesManagerContract codesManagerContract = mock(CodesManagerContract.class);
                when(codesManagerContract.isValid(context, ELECTION_ID)).thenReturn(true);
                when(codesManagerContract.invalidate(context, ELECTION_ID)).thenReturn(true);
            }
        }

        @Nested
        class TestFailCastVote {
        }
    }
 */
    @Nested
    class TestDeleteElection {

        @Nested
        class TestCorrectlyDeleteElection {
            @Test
            void whenDeleteElectionCorrectly() {
                doReturn(true).when(ELECTION_CONTRACT).electionExists(context, ELECTION_ID);
                when(stub.getStringState(ELECTION_ID)).thenReturn(genson.serialize(ELECTION_RESULTS_EMPTY));
                assertDoesNotThrow(() -> ELECTION_CONTRACT.deleteElection(context, ELECTION_ID));
            }
        }

        @Nested
        class TestWrongDeleteElection {
            @Test
            void whenDeleteElectionThatDoesntExist() {
                doReturn(false).when(ELECTION_CONTRACT).electionExists(context, ELECTION_ID);
                assertThrows(ChaincodeException.class, () -> ELECTION_CONTRACT.deleteElection(context, ELECTION_ID));

                final Throwable thrown = catchThrowable(() -> ELECTION_CONTRACT.deleteElection(context, ELECTION_ID));
                assertThat(thrown)
                        .isInstanceOf(ChaincodeException.class);
                assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("ELECTION_NOT_FOUND".getBytes(UTF_8));
            }
        }
    }
}

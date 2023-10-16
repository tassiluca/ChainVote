package it.unibo.ds.chainvote.contract;

import com.owlike.genson.Genson;
import it.unibo.ds.chainvote.assets.ElectionAsset;
import it.unibo.ds.chainvote.assets.ElectionInfoAsset;
import it.unibo.ds.chainvote.presentation.GensonUtils;
import it.unibo.ds.chainvote.utils.UserCodeData;
import it.unibo.ds.core.factory.ElectionFactory;
import it.unibo.ds.core.utils.Choice;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.Chaincode;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.CompositeKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static it.unibo.ds.chainvote.contract.CodesManagerContract.CODES_COLLECTION;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ElectionContractTest {

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
    private static final ElectionInfoAsset ELECTION_INFO_ASSET = new ElectionInfoAsset(
        ELECTION_ID,
        ElectionFactory.buildElectionInfo(GOAL, VOTERS, START_DATE, END_DATE, CHOICE_ELECTION)
    );
    private static final ElectionAsset ELECTION_ASSET = new ElectionAsset(
        ELECTION_ID,
        ElectionFactory.buildElection(ELECTION_INFO_ASSET.getAsset())
    );
    private static final Map<Choice, Long> RESULTS_FULL = CHOICE_ELECTION.stream()
        .collect(Collectors.toMap(Function.identity(), choice -> (long) CHOICE_ELECTION.indexOf(choice)));
    private static final Map<Choice, Long> RESULTS_EMPTY = new HashMap<>();
    private final Genson genson = GensonUtils.create();
    private Context context;
    private ChaincodeStub stub;
    private ElectionContract ec;
    private static final String CHANNEL_INFO_NAME = "ch1";
    private static final String CHAINCODE_INFO_NAME = "chaincode-org1";

    @BeforeEach
    void setup() {
        context = mock(Context.class);
        stub = mock(ChaincodeStub.class);
        when(context.getStub()).thenReturn(stub);
    }

    @Nested
    class TestCreateElection {

        @BeforeEach
        void setup() {
            ec = mock(ElectionContract.class);
            when(ec.readElectionInfo(context, ELECTION_ID)).thenReturn(ELECTION_INFO_ASSET.getAsset());
        }

        @Nested
        class TestCorrectlyCreateElection {
            @Test
            void whenCreateElectionWithResults() {
                assertDoesNotThrow(() -> ec.createElection(context, ELECTION_ID, RESULTS_FULL));
            }

            @Test
            void whenCreateElectionWithEmptyResults() {
                assertDoesNotThrow(() -> ec.createElection(context, ELECTION_ID, RESULTS_EMPTY));
            }
        }

        @Nested
        class TestFailToCreateElection {

            @Test
            void whenCreateElectionWithoutElectionInfo() {
                ElectionContract electionContract = new ElectionContract();
                when(context.getStub().invokeChaincodeWithStringArgs(
                    CHAINCODE_INFO_NAME,
                    List.of("ElectionInfoContract:readElectionInfo"),
                    CHANNEL_INFO_NAME
                )).thenReturn(
                    new Chaincode.Response(500, "", "".getBytes(UTF_8))
                );
                assertThrows(ChaincodeException.class, () -> electionContract.createElection(context, ELECTION_ID, RESULTS_FULL));
            }

            @Test
            void whenCreateElectionWithWrongResults() {
                ElectionContract electionContract = new ElectionContract();
                Map<Choice, Long> wrongResults = new HashMap<>(RESULTS_FULL);
                wrongResults.put(new Choice("test-wrong-choice"), 0L);
                when(context.getStub().invokeChaincodeWithStringArgs(
                    CHAINCODE_INFO_NAME,
                    List.of("ElectionInfoContract:readElectionInfo", ELECTION_ID),
                    CHANNEL_INFO_NAME
                )).thenReturn(
                    new Chaincode.Response(200, "", genson.serialize(ELECTION_INFO_ASSET.getAsset()).getBytes(UTF_8))
                );
                final Throwable thrown = catchThrowable(() -> electionContract.createElection(context, ELECTION_ID, wrongResults));
                assertThat(thrown).isInstanceOf(ChaincodeException.class);
                System.out.println(new String(((ChaincodeException) thrown).getPayload(), UTF_8));
                assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("ELECTION_INVALID_ARGUMENT".getBytes(UTF_8));
            }
        }
    }

    @Nested
    class TestReadAsset {

        @BeforeEach
        void setup() {
            ec = new ElectionContract();
            when(context.getStub().invokeChaincodeWithStringArgs(
                CHAINCODE_INFO_NAME,
                List.of("ElectionInfoContract:readElectionInfo"),
                CHANNEL_INFO_NAME
            )).thenReturn(
                new Chaincode.Response(200, "", genson.serialize(ELECTION_INFO_ASSET.getAsset()).getBytes(UTF_8))
            );
        }

        @Nested
        class TestWrongReadAsset {

            @Test
            void whenReadANonExistingAsset() {
                assertThrows(ChaincodeException.class, () -> ec.readElectionAsset(context, ELECTION_ID));
                final Throwable thrown = catchThrowable(() -> ec.readElectionAsset(context, ELECTION_ID));
                assertThat(thrown).isInstanceOf(ChaincodeException.class);
                assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("ELECTION_NOT_FOUND".getBytes(UTF_8));
            }
        }

        @Nested
        class TestCastVote {

            private ElectionContract electionContract;
            private static final String VOTER_ID = "test-voterID-123";

            @BeforeEach
            void setup() {
                electionContract = new ElectionContract();
                when(context.getStub().invokeChaincodeWithStringArgs(
                    CHAINCODE_INFO_NAME,
                    List.of("ElectionInfoContract:readElectionInfo"),
                    CHANNEL_INFO_NAME
                )).thenReturn(
                    new Chaincode.Response(200, "", genson.serialize(ELECTION_INFO_ASSET.getAsset()).getBytes(UTF_8))
                );
                when(stub.getTransient()).thenReturn(
                    Map.of(
                        UserCodeData.ELECTION_ID.getKey(), ELECTION_ID.getBytes(UTF_8),
                        UserCodeData.RESULTS.getKey(), genson.serialize(RESULTS_EMPTY).getBytes(UTF_8)
                    )
                );
                when(stub.getStringState(ELECTION_ID)).thenReturn(
                    genson.serialize(ELECTION_ASSET.getAsset())
                );
            }

            @Nested
            class TestCorrectlyCastVote {

                // without bring up the network is not possible to check if the vote is correctly registered

                @Test
                void whenCastVoteWithValidBallot() {
                    final CodesManagerContract cmc = new CodesManagerContract();
                    when(stub.getTransient()).thenReturn(
                        Map.of(UserCodeData.USER_ID.getKey(), VOTER_ID.getBytes(UTF_8))
                    );
                    final Long otc = cmc.generateFor(context, ELECTION_ID);
                    when(stub.getTransient()).thenReturn(
                        Map.of(
                            UserCodeData.USER_ID.getKey(), VOTER_ID.getBytes(UTF_8),
                            UserCodeData.CODE.getKey(), genson.serialize(otc).getBytes(UTF_8)
                        )
                    );
                    assertDoesNotThrow(() -> electionContract.castVote(context, CHOICE_ELECTION.get(0), ELECTION_ID));
                }
            }

            @Nested
            class TestWrongCastVote {

                @Test
                void whenCastVoteWithInvalidBallotChoice() {
                    final CodesManagerContract cmc = new CodesManagerContract();
                    when(stub.getTransient()).thenReturn(
                        Map.of(UserCodeData.USER_ID.getKey(), VOTER_ID.getBytes(UTF_8))
                    );
                    when(stub.getPrivateData(CODES_COLLECTION, new CompositeKey(ELECTION_ID, VOTER_ID).toString())).thenReturn(new byte[0]);
                    final Long otc = cmc.generateFor(context, ELECTION_ID);
                    when(stub.getTransient()).thenReturn(
                        Map.of(
                            UserCodeData.USER_ID.getKey(), VOTER_ID.getBytes(UTF_8),
                            UserCodeData.CODE.getKey(), genson.serialize(otc).getBytes(UTF_8)
                        )
                    );
                    assertThrows(ChaincodeException.class, () -> electionContract.castVote(context, new Choice("test-wrong-choice"), ELECTION_ID));
                    final Throwable thrown = catchThrowable(() -> electionContract.castVote(context, new Choice("test-wrong-choice"), ELECTION_ID));
                    assertThat(thrown).isInstanceOf(ChaincodeException.class);
                    assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("ELECTION_INVALID_BALLOT_ARGUMENT".getBytes(UTF_8));
                }

                @Test
                void whenCastVoteWithInvalidBallotElectionId() {
                    final CodesManagerContract cmc = new CodesManagerContract();
                    when(stub.getTransient()).thenReturn(
                        Map.of(UserCodeData.USER_ID.getKey(), VOTER_ID.getBytes(UTF_8))
                    );
                    when(stub.getPrivateData(CODES_COLLECTION, new CompositeKey(ELECTION_ID, VOTER_ID).toString())).thenReturn(new byte[0]);
                    final Long otc = cmc.generateFor(context, ELECTION_ID);
                    when(stub.getTransient()).thenReturn(
                        Map.of(
                            UserCodeData.USER_ID.getKey(), VOTER_ID.getBytes(UTF_8),
                            UserCodeData.CODE.getKey(), genson.serialize(otc).getBytes(UTF_8)
                        )
                    );
                    assertThrows(ChaincodeException.class, () -> electionContract.castVote(context, CHOICE_ELECTION.get(0), ELECTION_ID + "wrong"));
                    final Throwable thrown = catchThrowable(() -> electionContract.castVote(context, CHOICE_ELECTION.get(0), ELECTION_ID + "wrong"));
                    assertThat(thrown).isInstanceOf(ChaincodeException.class);
                    assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("ELECTION_NOT_FOUND".getBytes(UTF_8));
                }

                @Test
                void whenCastVoteWithRandomCode() {
                    when(stub.getTransient()).thenReturn(
                        Map.of(
                            UserCodeData.USER_ID.getKey(), VOTER_ID.getBytes(UTF_8),
                            UserCodeData.CODE.getKey(), genson.serialize(123456789L).getBytes(UTF_8)
                        )
                    );
                    assertThrows(ChaincodeException.class, () -> electionContract.castVote(context, CHOICE_ELECTION.get(0), ELECTION_ID));
                }
            }
        }

        @Nested
        class TestDeleteElection {

            private ElectionContract electionContract;

            @BeforeEach
            void setup() {
                electionContract = new ElectionContract();
                final String CHANNEL_INFO_NAME = "ch1";
                final String CHAINCODE_INFO_NAME = "chaincode-org1";
                when(context.getStub().invokeChaincodeWithStringArgs(
                    CHAINCODE_INFO_NAME,
                    List.of("ElectionInfoContract:readElectionInfo"),
                    CHANNEL_INFO_NAME
                )).thenReturn(
                    new Chaincode.Response(200, "", genson.serialize(ELECTION_INFO_ASSET.getAsset()).getBytes(UTF_8))
                );
            }

            @Nested
            class TestCorrectlyDelete {

            }

            @Nested
            class TestWrongDelete {

                @Test
                void whenDeleteWithInvalidElectionId() {
                    assertThrows(ChaincodeException.class, () -> electionContract.deleteAsset(context, ELECTION_ID + "wrong"));
                    final Throwable thrown = catchThrowable(() -> electionContract.deleteAsset(context, ELECTION_ID + "wrong"));
                    assertThat(thrown).isInstanceOf(ChaincodeException.class);
                    assertThat(thrown).isInstanceOf(ChaincodeException.class);
                    assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("ELECTION_NOT_FOUND".getBytes(UTF_8));
                }
            }
        }
    }
}

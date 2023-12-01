package it.unibo.ds.chainvote.contract;

import com.owlike.genson.Genson;
import it.unibo.ds.chainvote.SerializersUtils;
import it.unibo.ds.chainvote.asset.OneTimeCodeAsset;
import it.unibo.ds.chainvote.codes.OneTimeCodeImpl;
import it.unibo.ds.chainvote.factory.ElectionFactory;
import it.unibo.ds.chainvote.utils.Choice;
import it.unibo.ds.chainvote.utils.UserCodeData;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.CompositeKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static it.unibo.ds.chainvote.contract.CodesManagerContract.CODES_COLLECTION;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class CodesManagerContractTest {

    private static final String ELECTION_ID = "test-election";
    private static final String USER_ID = "mrossi";
    private static final String CODE = "123";
    private static final String SEED = Integer.toString(new Random().nextInt());
    private static final String KEY = new CompositeKey(ELECTION_ID, USER_ID).toString();

    private final Genson genson = SerializersUtils.gensonInstance();
    private final CodesManagerContract contract = new CodesManagerContract();
    private Context context;
    private ChaincodeStub stub;

    @BeforeEach
    void setup() {
        context = mock(Context.class);
        stub = mock(ChaincodeStub.class);
        when(context.getStub()).thenReturn(stub);
        assertEquals(stub, context.getStub());
    }

    @Nested
    class TestCodeGeneration {

        @BeforeEach
        void setup() {
            when(stub.getTransient()).thenReturn(
                Map.of(
                    UserCodeData.USER_ID.getKey(), USER_ID.getBytes(UTF_8),
                    UserCodeData.SEED.getKey(), SEED.getBytes(UTF_8)
                )
            );
            // by default suppose the election already exists: here is created a sample one!
            when(context.getStub().getStringState(ELECTION_ID)).thenReturn(genson.serialize(
                ElectionFactory.buildElection(
                    ElectionFactory.buildElectionInfo(
                        "test-election",
                        100,
                        LocalDateTime.now(),
                        LocalDateTime.MAX,
                        List.of(new Choice("yes"), new Choice("no"))
                    )
                )
            ));
        }

        @Test
        void whenNotAlreadyRequested() {
            when(stub.getPrivateData(CODES_COLLECTION, KEY)).thenReturn(new byte[0]);
            final String generatedCode = contract.generateCodeFor(context, ELECTION_ID);
            assertNotNull(generatedCode);
            verify(stub).putPrivateData(CODES_COLLECTION, KEY, genson.serialize(
                new OneTimeCodeAsset(ELECTION_ID, USER_ID, new OneTimeCodeImpl(generatedCode))
            ));
        }

        @Test
        void whenAlreadyExists() {
            final byte[] mockedCode = genson.serialize(
                new OneTimeCodeAsset(ELECTION_ID, USER_ID, new OneTimeCodeImpl("0"))
            ).getBytes(UTF_8);
            when(stub.getPrivateData(CODES_COLLECTION, KEY)).thenReturn(mockedCode);
            final Throwable thrown = catchThrowable(() -> contract.generateCodeFor(context, ELECTION_ID));
            assertThat(thrown)
                .isInstanceOf(ChaincodeException.class)
                .hasMessage("A one-time-code for the given election and user has already been generated");
            assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("ALREADY_GENERATED_CODE".getBytes(UTF_8));
        }

        @Test
        void whenElectionDoesNotExists() {
            when(context.getStub().getStringState(ELECTION_ID)).thenReturn("");
            when(stub.getPrivateData(CODES_COLLECTION, KEY)).thenReturn(new byte[0]);
            final Throwable thrown = catchThrowable(() -> contract.generateCodeFor(context, ELECTION_ID));
            assertThat(thrown)
                .isInstanceOf(ChaincodeException.class)
                .hasMessage("The given election doesn't exists");
            assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("INCORRECT_INPUT".getBytes(UTF_8));
        }

        @Test
        void whenSeedIsEmpty() {
            when(stub.getPrivateData(CODES_COLLECTION, KEY)).thenReturn(new byte[0]);
            when(stub.getTransient()).thenReturn(
                Map.of(
                    UserCodeData.USER_ID.getKey(), USER_ID.getBytes(UTF_8),
                    UserCodeData.SEED.getKey(), " ".getBytes(UTF_8)
                )
            );
            final Throwable thrown = catchThrowable(() -> contract.generateCodeFor(context, ELECTION_ID));
            assertThat(thrown)
                .isInstanceOf(ChaincodeException.class)
                .hasMessage("Seed cannot be blank");
            assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("INCORRECT_INPUT".getBytes(UTF_8));
        }
    }

    @Nested
    class TestCodeVerification {

        @BeforeEach
        void setup() {
            when(stub.getTransient()).thenReturn(
                Map.of(
                    "userId", USER_ID.getBytes(UTF_8),
                    "electionId", ELECTION_ID.getBytes(UTF_8),
                    "code", CODE.getBytes(UTF_8)
                )
            );
        }

        @Test
        void whenCodeIsCorrect() {
            final byte[] mockedCode = genson.serialize(
                new OneTimeCodeAsset(ELECTION_ID, USER_ID, new OneTimeCodeImpl(CODE))
            ).getBytes(UTF_8);
            when(stub.getPrivateData(CODES_COLLECTION, KEY)).thenReturn(mockedCode);
            assertTrue(contract.verifyCodeOwner(context, ELECTION_ID));
        }

        @Test
        void whenCodeIsIncorrect() {
            final byte[] wrongCode = genson.serialize(
                new OneTimeCodeAsset(ELECTION_ID, USER_ID, new OneTimeCodeImpl("0"))
            ).getBytes(UTF_8);
            when(stub.getPrivateData(CODES_COLLECTION, KEY)).thenReturn(wrongCode);
            assertFalse(contract.verifyCodeOwner(context, ELECTION_ID));
        }
    }

    @Nested
    class TestCodeInvalidation {

        @BeforeEach
        void setup() {
            when(stub.getTransient()).thenReturn(
                Map.of(
                    "userId", USER_ID.getBytes(UTF_8),
                    "electionId", ELECTION_ID.getBytes(UTF_8),
                    "code", CODE.getBytes(UTF_8)
                )
            );
        }

        @Test
        void whenCodeStillValid() {
            when(stub.getPrivateData(CODES_COLLECTION, KEY)).thenReturn(
                genson.serialize(
                    new OneTimeCodeAsset(ELECTION_ID, USER_ID, new OneTimeCodeImpl(CODE))
                ).getBytes(UTF_8)
            );
            assertTrue(contract.isValid(context, ELECTION_ID));
        }

        @Test
        void whenCodeIsIncorrect() {
            final byte[] wrongCode = genson.serialize(
                new OneTimeCodeAsset(ELECTION_ID, USER_ID, new OneTimeCodeImpl("0"))
            ).getBytes(UTF_8);
            when(stub.getPrivateData(CODES_COLLECTION, KEY)).thenReturn(wrongCode);
            assertFalse(contract.isValid(context, ELECTION_ID));
        }

        @Test
        void whenCodeIsInvalid() {
            final var code = new OneTimeCodeImpl(CODE);
            assertDoesNotThrow(code::consume);
            final byte[] invalidCode = genson.serialize(
                new OneTimeCodeAsset(ELECTION_ID, USER_ID, code)
            ).getBytes(UTF_8);
            when(stub.getPrivateData(CODES_COLLECTION, KEY)).thenReturn(invalidCode);
            assertFalse(contract.isValid(context, ELECTION_ID));
        }

        @Test
        void whenInvalidate() {
            final var code = new OneTimeCodeAsset(ELECTION_ID, USER_ID, new OneTimeCodeImpl(CODE));
            when(stub.getPrivateData(CODES_COLLECTION, KEY)).thenReturn(genson.serialize(code).getBytes(UTF_8));
            assertTrue(contract.invalidate(context, ELECTION_ID));
            final var consumedCode = code.getCode();
            assertDoesNotThrow(consumedCode::consume);
            final var consumedAsset = new OneTimeCodeAsset(ELECTION_ID, USER_ID, consumedCode);
            verify(stub).putPrivateData(CODES_COLLECTION, KEY, genson.serialize(consumedAsset));
        }

        @Test
        void whenAttemptToInvalidateMultipleTimes() {
            final var code = new OneTimeCodeImpl(CODE);
            assertDoesNotThrow(code::consume);
            final byte[] invalidCode = genson.serialize(
                new OneTimeCodeAsset(ELECTION_ID, USER_ID, code)
            ).getBytes(UTF_8);
            when(stub.getPrivateData(CODES_COLLECTION, KEY)).thenReturn(invalidCode);
            final Throwable thrown = catchThrowable(() -> contract.invalidate(context, ELECTION_ID));
            assertThat(thrown)
                .isInstanceOf(ChaincodeException.class)
                .hasMessage("The code has already been consumed");
            assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("ALREADY_INVALIDATED_CODE".getBytes(UTF_8));
        }
    }
}

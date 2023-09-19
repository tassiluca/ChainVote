package it.unibo.ds.chainvote.contract;

import com.owlike.genson.Genson;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unibo.ds.chainvote.assets.OneTimeCodeAsset;
import it.unibo.ds.chainvote.presentation.GensonUtils;
import it.unibo.ds.core.codes.OneTimeCodeImpl;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.CompositeKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static it.unibo.ds.chainvote.contract.CodesManagerContract.CODES_COLLECTION;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class CodeManagerContractTest {

    private static final String ELECTION_ID = "test-election";
    private static final String USER_ID = "mrossi";
    private static final Long CODE = 123L;
    private static final String KEY = new CompositeKey(ELECTION_ID, USER_ID).toString();

    private final Genson genson = GensonUtils.create();
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
                    "userId", USER_ID.getBytes(UTF_8),
                    "electionId", ELECTION_ID.getBytes(UTF_8)
                )
            );
        }

        @Test
        void whenNotAlreadyRequested() {
            when(stub.getPrivateData(CODES_COLLECTION, KEY)).thenReturn(new byte[0]);
            final long generatedCode = contract.generateFor(context);
            verify(stub).putPrivateData(CODES_COLLECTION, KEY, genson.serialize(
                new OneTimeCodeAsset(ELECTION_ID, USER_ID, new OneTimeCodeImpl(generatedCode))
            ));
        }

        @Test
        @SuppressFBWarnings(value = "BC", justification = "Before casting is checked the exception is of that type")
        void whenAlreadyExists() {
            final byte[] mockedCode = genson.serialize(
                new OneTimeCodeAsset(ELECTION_ID, USER_ID, new OneTimeCodeImpl(0L))
            ).getBytes(UTF_8);
            when(stub.getPrivateData(CODES_COLLECTION, KEY)).thenReturn(mockedCode);
            final Throwable thrown = catchThrowable(() -> contract.generateFor(context));
            assertThat(thrown)
                .isInstanceOf(ChaincodeException.class)
                .hasMessage("A one-time-code for the given election and user has already been generated");
            assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("ALREADY_GENERATED_CODE".getBytes(UTF_8));
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
                    "code", Long.toString(CODE).getBytes(UTF_8)
                )
            );
        }

        @Test
        void whenCodeIsCorrect() {
            final byte[] mockedCode = genson.serialize(
                new OneTimeCodeAsset(ELECTION_ID, USER_ID, new OneTimeCodeImpl(CODE))
            ).getBytes(UTF_8);
            when(stub.getPrivateData(CODES_COLLECTION, KEY)).thenReturn(mockedCode);
            assertTrue(contract.verifyCodeOwner(context));
        }

        @Test
        void whenCodeIsIncorrect() {
            final byte[] wrongCode = genson.serialize(
                new OneTimeCodeAsset(ELECTION_ID, USER_ID, new OneTimeCodeImpl(0L))
            ).getBytes(UTF_8);
            when(stub.getPrivateData(CODES_COLLECTION, KEY)).thenReturn(wrongCode);
            assertFalse(contract.verifyCodeOwner(context));
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
                    "code", Long.toString(CODE).getBytes(UTF_8)
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
            assertTrue(contract.isValid(context));
        }

        @Test
        void whenCodeIsIncorrect() {
            final byte[] wrongCode = genson.serialize(
                new OneTimeCodeAsset(ELECTION_ID, USER_ID, new OneTimeCodeImpl(0L))
            ).getBytes(UTF_8);
            when(stub.getPrivateData(CODES_COLLECTION, KEY)).thenReturn(wrongCode);
            assertFalse(contract.isValid(context));
        }

        @Test
        void whenCodeIsInvalid() {
            final var code = new OneTimeCodeImpl(CODE);
            assertDoesNotThrow(code::consume);
            final byte[] invalidCode = genson.serialize(
                new OneTimeCodeAsset(ELECTION_ID, USER_ID, code)
            ).getBytes(UTF_8);
            when(stub.getPrivateData(CODES_COLLECTION, KEY)).thenReturn(invalidCode);
            assertFalse(contract.isValid(context));
        }

        @Test
        void whenInvalidate() {
            final var code = new OneTimeCodeAsset(ELECTION_ID, USER_ID, new OneTimeCodeImpl(CODE));
            when(stub.getPrivateData(CODES_COLLECTION, KEY)).thenReturn(genson.serialize(code).getBytes(UTF_8));
            contract.invalidate(context);
            final var consumedCode = code.getCode();
            assertDoesNotThrow(consumedCode::consume);
            final var consumedAsset = new OneTimeCodeAsset(ELECTION_ID, USER_ID, consumedCode);
            verify(stub).putPrivateData(CODES_COLLECTION, KEY, genson.serialize(consumedAsset));
        }

        @Test
        @SuppressFBWarnings(value = "BC", justification = "Before casting is checked the exception is of that type")
        void whenAttemptToInvalidateMultipleTimes() {
            final var code = new OneTimeCodeImpl(CODE);
            assertDoesNotThrow(code::consume);
            final byte[] invalidCode = genson.serialize(
                new OneTimeCodeAsset(ELECTION_ID, USER_ID, code)
            ).getBytes(UTF_8);
            when(stub.getPrivateData(CODES_COLLECTION, KEY)).thenReturn(invalidCode);
            final Throwable thrown = catchThrowable(() -> contract.invalidate(context));
            assertThat(thrown)
                .isInstanceOf(ChaincodeException.class)
                .hasMessage("The code has already been consumed");
            assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("ALREADY_INVALIDATED_CODE".getBytes(UTF_8));
        }
    }
}

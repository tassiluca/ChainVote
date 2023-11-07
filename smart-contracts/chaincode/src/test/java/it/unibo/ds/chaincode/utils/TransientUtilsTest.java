package it.unibo.ds.chaincode.utils;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static it.unibo.ds.chainvote.TransientUtils.getLongFromTransient;
import static it.unibo.ds.chainvote.TransientUtils.getStringFromTransient;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

final class TransientUtilsTest {

    @Nested
    static class TestCorrectlyGettingFromTransient {

        @Test
        void whenGetStringFromTransient() {
            final String key = "string";
            final String goal = "goal-test";
            final Map<String, byte[]> transientData = Map.of(key, goal.getBytes(UTF_8));
            assertDoesNotThrow(() -> getStringFromTransient(transientData, key));
            assertEquals(goal, getStringFromTransient(transientData, key));
        }

        @Test
        void whenGetLongFromTransient() {
            final String key = "code";
            final long goal = 100L;
            final String goalToString = String.valueOf(goal);
            final Map<String, byte[]> transientData = Map.of(key, goalToString.getBytes(UTF_8));
            assertDoesNotThrow(() -> getLongFromTransient(transientData, key));
            assertEquals(goal, getLongFromTransient(transientData, key));
        }
    }

    @Nested
    static class TestFailGettingFromTransient {

        @Test
        @SuppressFBWarnings(value = "BC", justification = "Before casting is checked the exception is of that type")
        void whenTransientInputNotExists() {
            final Map<String, byte[]> transientData = Map.of();
            final String key = "non-existing-key";
            final Throwable thrown = catchThrowable(() -> getStringFromTransient(transientData, key));
            assertThat(thrown)
                .isInstanceOf(ChaincodeException.class)
                .hasMessage("An entry with key `" + key + "` was expected in the transient map.");
            assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("INCOMPLETE_INPUT".getBytes(UTF_8));
        }

        @Test
        @SuppressFBWarnings(value = "BC", justification = "Before casting is checked the exception is of that type")
        void whenTransientInputIsEmpty() {
            final Map<String, byte[]> transientData = Map.of("key", "".getBytes(UTF_8));
            final String key = "key";
            final Throwable thrown = catchThrowable(() -> getStringFromTransient(transientData, key));
            assertThat(thrown)
                .isInstanceOf(ChaincodeException.class)
                .hasMessage("An entry with key `" + key + "` was expected in the transient map.");
            assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("INCOMPLETE_INPUT".getBytes(UTF_8));
        }

        @Test
        @SuppressFBWarnings(value = "BC", justification = "Before casting is checked the exception is of that type")
        void whenTransientInputIsWrong() {
            final Map<String, byte[]> transientData = Map.of("code", "abcd".getBytes(UTF_8));
            final String key = "code";
            final Throwable thrown = catchThrowable(() -> getLongFromTransient(transientData, key));
            assertThat(thrown)
                .isInstanceOf(ChaincodeException.class)
                .hasMessageStartingWith("The `" + key + "`s input was expected to be a Long.");
            assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("WRONG_INPUT".getBytes(UTF_8));
        }
    }
}

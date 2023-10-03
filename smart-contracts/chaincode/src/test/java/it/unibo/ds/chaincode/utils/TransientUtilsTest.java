package it.unibo.ds.chaincode.utils;

import com.owlike.genson.Genson;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unibo.ds.chainvote.presentation.GensonUtils;
import it.unibo.ds.core.utils.Choice;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static it.unibo.ds.chaincode.utils.TransientUtils.getChoiceFromTransient;
import static it.unibo.ds.chaincode.utils.TransientUtils.getDateFromTransient;
import static it.unibo.ds.chaincode.utils.TransientUtils.getListFromTransient;
import static it.unibo.ds.chaincode.utils.TransientUtils.getLongFromTransient;
import static it.unibo.ds.chaincode.utils.TransientUtils.getStringFromTransient;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TransientUtilsTest {


    static class TestCorrectlyGetFromTransient {

        @Test
        @SuppressFBWarnings(value = "BC", justification = "Before casting is checked the exception is of that type")
        void whenGetStringFromTransient() {
            final String key = "string";
            final String goal = "prova";
            final Map<String, byte[]> transientData = Map.of(key, goal.getBytes(UTF_8));
            assertDoesNotThrow(() -> getStringFromTransient(transientData, key));
            assertEquals(goal, getStringFromTransient(transientData, key));
        }

        @Test
        @SuppressFBWarnings(value = "BC", justification = "Before casting is checked the exception is of that type")
        void whenGetLongFromTransient() {
            final String key = "long";
            final long goal = 100L;
            final String goalToString = String.valueOf(goal);
            final Map<String, byte[]> transientData = Map.of(key, goalToString.getBytes(UTF_8));
            assertDoesNotThrow(() -> getLongFromTransient(transientData, key));
            assertEquals(goal, getLongFromTransient(transientData, key));
        }

        @Test
        @SuppressFBWarnings(value = "BC", justification = "Before casting is checked the exception is of that type")
        void whenGetDateFromTransient() {
            final String key = "date";
            final Map<String, Integer> TIME_MAP = Map.of(
                "y", 2023,
                "M", 8,
                "d", 20,
                "h", 10,
                "m", 0,
                "s", 0
            );
            final LocalDateTime goal = LocalDateTime.of(TIME_MAP.get("y"), TIME_MAP.get("M"), TIME_MAP.get("d"),
                TIME_MAP.get("h"), TIME_MAP.get("m"), TIME_MAP.get("s"));
            final Genson genson = GensonUtils.create();
            final String goalToString = genson.serialize(goal);
            final Map<String, byte[]> transientData = Map.of(key, goalToString.getBytes(UTF_8));
            assertDoesNotThrow(() -> getDateFromTransient(transientData, key));
            assertEquals(goal, getDateFromTransient(transientData, key));
        }

        @Test
        @SuppressFBWarnings(value = "BC", justification = "Before casting is checked the exception is of that type")
        void whenGetChoiceFromTransient() {
            final String key = "choice";
            final Choice goal = new Choice("prova");

            final Genson genson = GensonUtils.create();

            final String goalToString = genson.serialize(goal);
            final Map<String, byte[]> transientData = Map.of(key, goalToString.getBytes(UTF_8));
            assertDoesNotThrow(() -> getChoiceFromTransient(transientData, key));
            assertEquals(goal, getChoiceFromTransient(transientData, key));
        }

        @Test
        @SuppressFBWarnings(value = "BC", justification = "Before casting is checked the exception is of that type")
        void whenGetListFromTransient() {
            final String key = "choice";
            final List<Choice> goal = List.of(new Choice("prova1"), new Choice("prova2"), new Choice("prova3"), new Choice("prova4"));

            final Genson genson = GensonUtils.create();

            final String goalToString = genson.serialize(goal);
            final Map<String, byte[]> transientData = Map.of(key, goalToString.getBytes(UTF_8));
            assertDoesNotThrow(() -> getListFromTransient(transientData, key));
            assertEquals(goal, getListFromTransient(transientData, key));
        }

        @Test
        @SuppressFBWarnings(value = "BC", justification = "Before casting is checked the exception is of that type")
        void whenGetMapFromTransient() {
            final String key = "map";
            final List<Choice> goal = List.of(new Choice("prova1"), new Choice("prova2"), new Choice("prova3"), new Choice("prova4"));

            final Genson genson = GensonUtils.create();

            final String goalToString = genson.serialize(goal);
            final Map<String, byte[]> transientData = Map.of(key, goalToString.getBytes(UTF_8));
            assertDoesNotThrow(() -> getListFromTransient(transientData, key));
            assertEquals(goal, getListFromTransient(transientData, key));
        }
    }

    static class TestCorrectlyFailGetFromTransient {

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
        void whenTransientInputIsWrong() {
            final Map<String, byte[]> transientData = Map.of("code", "abcd".getBytes(UTF_8));
            final String key = "code";
            final Throwable thrown = catchThrowable(() -> getLongFromTransient(transientData, key));
            assertThat(thrown)
                .isInstanceOf(ChaincodeException.class)
                .hasMessage("The `" + key + "`s input was expected to be a Long.");
            assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("WRONG_INPUT".getBytes(UTF_8));
        }
    }
}

package it.unibo.ds.chainvote.contract;

import org.hyperledger.fabric.shim.ChaincodeException;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static it.unibo.ds.chainvote.utils.TransientUtils.getLongFromTransient;
import static it.unibo.ds.chainvote.utils.TransientUtils.getStringFromTransient;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;

public class TransientUtilsTest {

    @Test
    void whenTransientInputNotExists() {
        final Map<String, byte[]> transientData = Map.of();
        final String key = "non-existing-key";
        final Throwable thrown = catchThrowable(() -> getStringFromTransient(transientData, key));
        assertThat(thrown)
            .isInstanceOf(ChaincodeException.class)
            .hasMessage("An entry with key `" + key +"` was expected in the transient map.");
        assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("INCOMPLETE_INPUT".getBytes(UTF_8));
    }

    @Test
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

package it.unibo.ds.chainvote.presentation;

import com.owlike.genson.Genson;
import com.owlike.genson.JsonBindingException;
import it.unibo.ds.core.codes.OneTimeCode;
import it.unibo.ds.core.codes.OneTimeCodeImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OneTimeCodeSerializationTest {

    private final Genson genson = GensonUtils.create();
    private static final Long CODE = 123L;

    @Test
    void testSerialization() {
        final var serialized = genson.serialize(getOTC());
        assertEquals(getSerialized(), serialized);
    }

    @Test
    void testDeserialization() {
        final var deserialized = genson.deserialize(getSerialized(), OneTimeCode.class);
        assertEquals(getOTC(), deserialized);
    }

    @Test
    void testDeserializationWithWrongType() {
        final var wrong = "{\"otc\":\"wrong\"}";
        assertThrows(JsonBindingException.class, () -> genson.deserialize(wrong, OneTimeCode.class));
    }

    @Test
    void testDeserializationWithMissingValue() {
        final var wrong = "{\"other\": \"wrong\"}";
        assertThrows(JsonBindingException.class, () -> genson.deserialize(wrong, OneTimeCode.class));
    }

    private OneTimeCode getOTC() {
        return new OneTimeCodeImpl(CODE);
    }

    private String getSerialized() {
        return "{\"otc\":\"" + CODE + "\"}";
    }
}

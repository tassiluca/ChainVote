package it.unibo.ds.chainvote;

import com.owlike.genson.Genson;
import com.owlike.genson.JsonBindingException;
import it.unibo.ds.chainvote.codes.InvalidCodeException;
import it.unibo.ds.chainvote.codes.OneTimeCode;
import it.unibo.ds.chainvote.codes.OneTimeCodeImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OneTimeCodeSerializationTest {

    private final Genson genson = GensonUtils.create();
    private static final String CODE = "123";

    @Test
    void testSerialization() {
        final var serialized = genson.serialize(getOTC());
        System.out.println(serialized);
        assertEquals(getSerialized(), serialized);
    }

    @Test
    void testConsumedSerialization() {
        final var consumedSerialized = genson.serialize(getConsumedOTC());
        assertEquals(getConsumedSerialized(), consumedSerialized);
    }

    @Test
    void testDeserialization() {
        final var deserialized = genson.deserialize(getSerialized(), OneTimeCode.class);
        assertEquals(getOTC(), deserialized);
    }

    @Test
    void testConsumedDeserialization() {
        final var consumedDeserialized = genson.deserialize(getConsumedSerialized(), OneTimeCode.class);
        assertEquals(getConsumedOTC(), consumedDeserialized);
    }

    @Test
    void testDeserializationWithMissingValue() {
        final var wrong = "{\"other\": \"wrong\"}";
        assertThrows(JsonBindingException.class, () -> genson.deserialize(wrong, OneTimeCode.class));
    }

    @Test
    void testIncompleteDeserialization() {
        final var wrong = "{\"consumed\":\"false\"}";
        assertThrows(JsonBindingException.class, () -> genson.deserialize(wrong, OneTimeCode.class));
    }

    private OneTimeCode getOTC() {
        return new OneTimeCodeImpl(CODE);
    }

    private OneTimeCode getConsumedOTC() {
        final var code = new OneTimeCodeImpl(CODE);
        try { code.consume(); } catch (InvalidCodeException ignored) { }
        return code;
    }

    private String getSerialized() {
        return "{\"otc\":\"" + CODE + "\",\"consumed\":\"false\"}";
    }

    private String getConsumedSerialized() {
        return "{\"otc\":\"" + CODE + "\",\"consumed\":\"true\"}";
    }
}

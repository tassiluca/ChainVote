package it.unibo.ds.chainvote.presentation;

import com.owlike.genson.Genson;
import com.owlike.genson.JsonBindingException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LocalDateTimeSerializationTest {

    private final Genson genson = GensonUtils.create();
    private static final Map<String, Integer> TIME_MAP = Map.of(
            "y", 2022,
            "M", 8,
            "d", 20,
            "h", 10,
            "m", 0,
            "s", 0
    );
    private static final LocalDateTime DATE = LocalDateTime.of(TIME_MAP.get("y"), TIME_MAP.get("M"), TIME_MAP.get("d"),
            TIME_MAP.get("h"), TIME_MAP.get("m"), TIME_MAP.get("s"));

    private String getSerialized() {
        return "{\"year\":\"" + TIME_MAP.get("y") + "\",\"month\":\"" + TIME_MAP.get("M") + "\",\"day\":\""
                + TIME_MAP.get("d") + "\",\"hour\":\"" + TIME_MAP.get("h") + "\",\"minute\":\""
                + TIME_MAP.get("m") + "\",\"second\":\"" + TIME_MAP.get("s") + "\"}";
    }

    @Test
    void testSerialization() {
        final var serialized = genson.serialize(DATE);
        System.out.println(serialized);
        assertEquals(getSerialized(), serialized);
    }

    @Test
    void testDeserialization() {
        final var deserialized = genson.deserialize(genson.serialize(DATE), LocalDateTime.class);
        assertEquals(DATE, deserialized);
    }

    @Test
    void testDeserializationWithWrongValues() {
        final var wrong = "{\"day\":\"50\",\"month\":\"50\",\"year\":\"50\",\"hour\":\"50\",\"minute\":\"50\",\"second\":\"50\"}";
        assertThrows(JsonBindingException.class, () -> genson.deserialize(wrong, LocalDateTime.class));
    }

    @Test
    void testDeserializationWithMissingValue() {
        final var wrong = "{\"day\":\"1\",\"year\":\"2000\",\"hour\":\"0\",\"minute\":\"0\",\"second\":\"0\"}";
        assertThrows(JsonBindingException.class, () -> genson.deserialize(wrong, LocalDateTime.class));
    }
}

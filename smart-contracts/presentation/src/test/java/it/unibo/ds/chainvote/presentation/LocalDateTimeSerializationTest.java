package it.unibo.ds.chainvote.presentation;

import com.owlike.genson.Genson;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LocalDateTimeSerializationTest {

    private final Genson genson = GensonUtils.create();
    private static final Map<String, Integer> TIME_MAP = Map.of(
            "y", 2023,
            "M", 8,
            "d", 20,
            "h", 10,
            "m", 0,
            "s", 0
    );
    private static final LocalDateTime DATE = LocalDateTime.of(TIME_MAP.get("y"), TIME_MAP.get("M"), TIME_MAP.get("d"),
            TIME_MAP.get("h"), TIME_MAP.get("m"), TIME_MAP.get("s"));

    private String getExpected() {
        return "\"2023-08-20T10:00:00\"";
    }

    @Test
    void testSerialization() {
        final var serialized = genson.serialize(DATE);
        assertEquals(getExpected(), serialized);
    }

    @Test
    void testDeserialization() {
        final var deserialized = genson.deserialize(genson.serialize(DATE), LocalDateTime.class);
        assertEquals(DATE, deserialized);
    }
}

package it.unibo.ds.chainvote;

import com.owlike.genson.GenericType;
import com.owlike.genson.Genson;
import it.unibo.ds.chainvote.utils.Choice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MapOfStringLongSerializationTest {

    private final Genson genson = GensonUtils.create();
    private static final Choice CHOICE1 = new Choice("prova1");
    private static final Long LONG1 = 1L;
    private static final Choice CHOICE2 = new Choice("prova2");
    private static final Long LONG2 = 2L;
    private static final Choice CHOICE3 = new Choice("prova3");
    private static final Long LONG3 = 3L;
    private static final Choice CHOICE4 = new Choice("prova4");
    private static final Long LONG4 = 4L;
    private static final Map<String, Long> MAP = new HashMap<>();

    @BeforeEach
    void setUp() {
        MAP.put(CHOICE1.getChoice(), LONG1);
        MAP.put(CHOICE2.getChoice(), LONG2);
        MAP.put(CHOICE3.getChoice(), LONG3);
        MAP.put(CHOICE4.getChoice(), LONG4);
    }

    @Test
    void testDeserialization() {
        final var deserialized = genson.deserialize(genson.serialize(MAP), new GenericType<>() { });
        assertEquals(MAP, deserialized);
    }
}

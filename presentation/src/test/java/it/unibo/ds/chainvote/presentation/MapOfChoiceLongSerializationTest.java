package it.unibo.ds.chainvote.presentation;

import com.owlike.genson.GenericType;
import com.owlike.genson.Genson;
import com.owlike.genson.JsonBindingException;
import it.unibo.ds.core.utils.Choice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MapOfChoiceLongSerializationTest {

    private final Genson genson = GensonUtils.create();
    private static final Choice CHOICE1 = new Choice("prova1");
    private static final Long LONG1 = 1L;
    private static final Choice CHOICE2 = new Choice("prova2");
    private static final Long LONG2 = 2L;
    private static final Choice CHOICE3 = new Choice("prova3");
    private static final Long LONG3 = 3L;
    private static final Choice CHOICE4 = new Choice("prova4");
    private static final Long LONG4 = 4L;
    private static final Map<Choice, Long> MAP = new HashMap<>();

    @BeforeEach
    void setUp() {
        MAP.put(CHOICE1, LONG1);
        MAP.put(CHOICE2, LONG2);
        MAP.put(CHOICE3, LONG3);
        MAP.put(CHOICE4, LONG4);
    }

    private String getSerialized() {
        return "{\"value\":{\"Choice{choice='prova2'}\":{\"value\":2},\"Choice{choice='prova1'}\":{\"value\":1},\"Choice{choice='prova4'}\":{\"value\":4},\"Choice{choice='prova3'}\":{\"value\":3}}}";
    }

    @Test
    void testSerialization() {
        final var serialized = genson.serialize(MAP);
        assertEquals(getSerialized(), serialized);
    }

    @Test
    void testDeserialization() {
        final var deserialized = genson.deserialize(genson.serialize(MAP), new GenericType<Map<Choice, Long>>() {});
        assertEquals(MAP, deserialized);
    }

    @Test
    void testDeserializationWithWrongValues() {
        final var wrong = "{\"va\":[{\"choice\":\"prova1\"},{\"choice\":\"prova2\"},{\"choice\":\"prova3\"},{\"choice\":\"prova4\"}]}";
        assertThrows(JsonBindingException.class, () -> genson.deserialize(wrong, new GenericType<List<Choice>>() {}));
    }

    @Test
    void testDeserializationWithMissingValue() {
        final var wrong = "[{\"choice\":\"prova1\"},{\"choice\":\"prova2\"},{\"choice\":\"prova3\"},{\"choice\":\"prova4\"}]";
        assertThrows(JsonBindingException.class, () -> genson.deserialize(wrong, new GenericType<List<Choice>>() {}));
    }
}

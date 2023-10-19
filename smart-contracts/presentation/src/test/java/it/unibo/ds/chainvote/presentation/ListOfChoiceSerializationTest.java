package it.unibo.ds.chainvote.presentation;

import com.owlike.genson.GenericType;
import com.owlike.genson.Genson;
import it.unibo.ds.core.utils.Choice;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ListOfChoiceSerializationTest {
    private final Genson genson = GensonUtils.create();
    private static final Choice CHOICE1 = new Choice("prova1");
    private static final Choice CHOICE2 = new Choice("prova2");
    private static final Choice CHOICE3 = new Choice("prova3");
    private static final Choice CHOICE4 = new Choice("prova4");
    private static final List<Choice> LIST = new ArrayList<>(Arrays.asList(CHOICE1, CHOICE2, CHOICE3, CHOICE4));

    private String getExpected() {
        return "[{\"choice\":\"prova1\"},{\"choice\":\"prova2\"},{\"choice\":\"prova3\"},{\"choice\":\"prova4\"}]";
    }

    @Test
    void testSerialization() {
        final var serialized = genson.serialize(LIST);
        assertEquals(getExpected(), serialized);
    }

    @Test
    void testDeserialization() {
        final var deserialized = genson.deserialize(genson.serialize(LIST), new GenericType<List<Choice>>() {});
        assertEquals(LIST, deserialized);
    }
}

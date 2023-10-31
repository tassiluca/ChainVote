package it.unibo.ds.chainvote;

import com.owlike.genson.GenericType;
import com.owlike.genson.Genson;
import it.unibo.ds.chainvote.utils.Choice;
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

    @Test
    void testDeserialization() {
        final var deserialized = genson.deserialize(genson.serialize(LIST), new GenericType<List<Choice>>() { });
        assertEquals(LIST, deserialized);
    }
}

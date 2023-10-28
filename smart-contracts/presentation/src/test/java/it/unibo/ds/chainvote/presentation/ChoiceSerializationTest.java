package it.unibo.ds.chainvote.presentation;

import com.owlike.genson.Genson;
import it.unibo.ds.core.utils.Choice;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChoiceSerializationTest {

    private final Genson genson = GensonUtils.create();
    private static final String CHOICE_TO_CAST = "123prova";
    private static final Choice CHOICE = new Choice(CHOICE_TO_CAST);

    @Test
    void testDeserialization() {
        final var deserialized = genson.deserialize(genson.serialize(CHOICE), Choice.class);
        assertEquals(CHOICE, deserialized);
    }
}

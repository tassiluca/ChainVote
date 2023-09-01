package it.unibo.ds.chainvote.presentation;

import com.owlike.genson.GenericType;
import com.owlike.genson.Genson;
import com.owlike.genson.JsonBindingException;
import it.unibo.ds.core.assets.Ballot;
import it.unibo.ds.core.assets.BallotImpl;
import it.unibo.ds.core.utils.Choice;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ListOfChoiceSerializationTest {
    private final Genson genson = GensonUtils.create();
    private static final Choice CHOICE1 = new Choice("prova1");
    private static final Choice CHOICE2 = new Choice("prova2");
    private static final Choice CHOICE3 = new Choice("prova3");
    private static final Choice CHOICE4 = new Choice("prova4");
    private static final List<Choice> LIST = new ArrayList<>(Arrays.asList(CHOICE1, CHOICE2, CHOICE3, CHOICE4));

    private String getSerialized() {
        return "";
    }

    @Test
    void testSerialization() {
        final var serialized = genson.serialize(LIST);
        System.out.println(serialized);
        //assertEquals(getSerialized(), serialized);
    }

    @Test
    void testDeserialization() {
        System.out.println(genson.deserialize(genson.serialize(LIST), new GenericType<List<Choice>>() {}));
        final var deserialized = genson.deserialize(genson.serialize(LIST), new GenericType<List<Choice>>() {});

        /*
        TypeToken<List<Choice>> typeToken = new TypeToken<List<Choice>>() {};



        // JSON da deserializzare (sostituisci con il tuo JSON effettivo)
        String json = "[{\"prop1\":\"val1\",\"prop2\":\"val2\"}, {\"prop1\":\"val3\",\"prop2\":\"val4\"}]";



        // Deserializza la lista di Choice utilizzando il TypeToken
        List<Choice> choices = genson.deserialize(json, typeToken);



        // Ora hai una lista di oggetti Choice deserializzati
        for (Choice choice : choices) {
            System.out.println(choice);
        }

         */
        System.out.println(deserialized);

        //assertEquals(BALLOT, deserialized);
    }

    @Test
    void testDeserializationWithWrongValues() {
        final var wrong = "{\"electionID\":\"123prova\",\"voterID\":\"prova123\",\"date\":\"{\\\"day\\\":\\\"32\\\",\\\"month\\\":\\\"8\\\",\\\"year\\\":\\\"2022\\\",\\\"hour\\\":\\\"10\\\",\\\"minute\\\":\\\"0\\\",\\\"second\\\":\\\"0\\\"}\",\"choice\":\"123prova123\"}";
        assertThrows(JsonBindingException.class, () -> genson.deserialize(wrong, Ballot.class));
    }

    @Test
    void testDeserializationWithMissingValue() {
        final var wrong = "{\"electionID\":\"123prova\",\"date\":\"{\\\"day\\\":\\\"32\\\",\\\"month\\\":\\\"8\\\",\\\"year\\\":\\\"2022\\\",\\\"hour\\\":\\\"10\\\",\\\"minute\\\":\\\"0\\\",\\\"second\\\":\\\"0\\\"}\",\"choice\":\"123prova123\"}";
        assertThrows(JsonBindingException.class, () -> genson.deserialize(wrong, LocalDateTime.class));
    }
}

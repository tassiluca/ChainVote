package it.unibo.ds.chainvote.presentation;

import com.owlike.genson.Genson;
import com.owlike.genson.JsonBindingException;
import it.unibo.ds.core.Voting;
import it.unibo.ds.core.VotingFactory;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TestVotingSerialization {

    private final Genson genson = GensonUtils.create();
    private static final String NAME = "test";
    private static final String QUESTION = "Yes or No?";
    private static final String CHOICE1 = "Yes";
    private static final String CHOICE2 = "No";
    private static final String OPENING_DATE = "2023-07-13T09:00:00";
    private static final String CLOSING_DATE = "2023-08-13T09:00:00";

    @Test
    void testSerialization() {
        final var serialized = genson.serialize(getVoting());
        assertEquals(getSerialized(), serialized);
    }

    @Test
    void testDeserialization() {
        final var deserialized = genson.deserialize(getSerialized(), Voting.class);
        assertEquals(getVoting(), deserialized);
    }

    @Test
    void testDeserializationWithMissingValue() {
        final var partial = "{\"name\":\"test\",\"question\":\"Yes or No?\",\"choices\":[\"Yes\",\"No\"]}";
        assertThrows(
            JsonBindingException.class,
            () -> genson.deserialize(partial, Voting.class)
        );
    }

    @Test
    void testDeserializationWithWrongValue() {
        final var wrong = "{\"name\":\"test\",\"question\":\"Yes or No?\",\"choices\":[\"Yes\",\"No\"],"
            + "\"openingDate\":\"2023-07-13T09:00:00\",\"closingDate\":\"2023-07-13T09:00:00\"}";
        assertThrows(
            JsonBindingException.class,
            () -> genson.deserialize(wrong, Voting.class)
        );
    }

    private Voting getVoting() {
        return new VotingFactory().create(
            NAME,
            QUESTION,
            List.of(CHOICE1, CHOICE2),
            LocalDateTime.parse(OPENING_DATE),
            LocalDateTime.parse(CLOSING_DATE)
        );
    }

    private String getSerialized() {
        return "{\"name\":\"" + NAME + "\",\"question\":\"" + QUESTION + "\","
            + "\"choices\":[\"" + CHOICE1 + "\",\"" + CHOICE2 + "\"],"
            + "\"openingDate\":\"" + OPENING_DATE + "\",\"closingDate\":\"" + CLOSING_DATE + "\"}";
    }
}

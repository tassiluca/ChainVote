package it.unibo.ds.chainvote.presentation;

import com.owlike.genson.Genson;
import com.owlike.genson.JsonBindingException;
import it.unibo.ds.core.assets.Ballot;
import it.unibo.ds.core.assets.BallotImpl;
import it.unibo.ds.core.utils.Choice;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BallotSerializationTest {

    private final Genson genson = GensonUtils.create();
    private static final String ELECTION_ID = "123prova";
    private static final String VOTER_ID = "prova123";
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
    private static final Choice CHOICE = new Choice("123prova123");
    private static final Ballot BALLOT = new BallotImpl.Builder()
            .electionID(ELECTION_ID)
            .voterID(VOTER_ID)
            .date(DATE)
            .choice(CHOICE)
            .build();

    private String getSerialized() {
        return "{\"electionID\":\"" + ELECTION_ID + "\",\"voterID\":\"" + VOTER_ID + "\",\"date\":\"" + genson.serialize(DATE) + "\",\"choice\":\""
                + CHOICE.getChoice() + "\"}";
    }

    @Test
    void testSerialization() {
        final var serialized = genson.serialize(BALLOT).replace("\\", "");
        assertEquals(getSerialized(), serialized);
    }

    @Test
    void testDeserialization() {
        final var deserialized = genson.deserialize(genson.serialize(BALLOT), Ballot.class);
        assertEquals(BALLOT, deserialized);
    }

    @Test
    void testDeserializationWithWrongValues() {
        final var wrong = "{\"electionID\":\"123prova\",\"voterID\":\"prova123\",\"date\":\"{\\\"day\\\":\\\"32\\\"," +
                "\\\"month\\\":\\\"8\\\",\\\"year\\\":\\\"2022\\\",\\\"hour\\\":\\\"10\\\",\\\"minute\\\":\\\"0\\\"," +
                "\\\"second\\\":\\\"0\\\"}\",\"choice\":\"123prova123\"}";
        assertThrows(JsonBindingException.class, () -> genson.deserialize(wrong, Ballot.class));
    }

    @Test
    void testDeserializationWithMissingValue() {
        final var wrong = "{\"electionID\":\"123prova\",\"date\":\"{\\\"day\\\":\\\"30\\\",\\\"month\\\":\\\"8\\\"," +
                "\\\"year\\\":\\\"2022\\\",\\\"hour\\\":\\\"10\\\",\\\"minute\\\":\\\"0\\\",\\\"second\\\":\\\"0\\\"}" +
                "\",\"choice\":\"123prova123\"}";
        assertThrows(JsonBindingException.class, () -> genson.deserialize(wrong, Ballot.class));
    }
}

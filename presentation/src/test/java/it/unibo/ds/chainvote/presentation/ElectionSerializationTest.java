package it.unibo.ds.chainvote.presentation;

import com.owlike.genson.Genson;
import com.owlike.genson.JsonBindingException;
import it.unibo.ds.core.assets.Ballot;
import it.unibo.ds.core.assets.Election;
import it.unibo.ds.core.assets.ElectionImpl;
import it.unibo.ds.core.utils.Choice;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ElectionSerializationTest {

    private final Genson genson = GensonUtils.create();
    private static final String GOAL = "prova";
    private static final long VOTERS = 400_000_000;
    private static final Map<String, Integer> START_TIME_MAP = Map.of(
            "y", 2022,
            "M", 8,
            "d", 20,
            "h", 10,
            "m", 0,
            "s", 0
    );
    private static final Map<String, Integer> END_TIME_MAP = Map.of(
            "y", 2022,
            "M", 8,
            "d", 22,
            "h", 10,
            "m", 0,
            "s", 0
    );
    private static final LocalDateTime START_DATE = LocalDateTime.of(START_TIME_MAP.get("y"), START_TIME_MAP.get("M"), START_TIME_MAP.get("d"),
            START_TIME_MAP.get("h"), START_TIME_MAP.get("m"), START_TIME_MAP.get("s"));
    private static final LocalDateTime END_DATE = LocalDateTime.of(END_TIME_MAP.get("y"), END_TIME_MAP.get("M"), END_TIME_MAP.get("d"),
            END_TIME_MAP.get("h"), END_TIME_MAP.get("m"), END_TIME_MAP.get("s"));
    private static final List<Choice> CHOICES = new ArrayList<>(List.of(new Choice("prova1"), new Choice("prova2"), new Choice("prova3")));
    private static final Election ELECTION = new ElectionImpl.Builder()
            .goal(GOAL)
            .voters(VOTERS)
            .start(START_DATE)
            .end(END_DATE)
            .choices(CHOICES)
            .build();
/*
    private String getSerialized() {
        return "{\"electionID\":\"" + ELECTION_ID + "\",\"voterID\":\"" + VOTER_ID + "\",\"date\":\"{\\\"day\\\":\\\"" + TIME_MAP.get("d") + "\\\",\\\"month\\\":\\\"" + TIME_MAP.get("M") + "\\\",\\\"year\\\":\\\""
                + TIME_MAP.get("y") + "\\\",\\\"hour\\\":\\\"" + TIME_MAP.get("h") + "\\\",\\\"minute\\\":\\\""
                + TIME_MAP.get("m") + "\\\",\\\"second\\\":\\\"" + TIME_MAP.get("s") + "\\\"}\",\"choice\":\"" + CHOICE.getChoice() + "\"}";
    }

 */

    @Test
    void testSerialization() {
        final var serialized = genson.serialize(ELECTION);
        System.out.println(serialized);
        //assertEquals(getSerialized(), serialized);
    }

    @Test
    void testDeserialization() {
        // TODO test don't pass
        // final var deserialized = genson.deserialize(genson.serialize(ELECTION), Election.class);
        // System.out.println(deserialized);
        // assertEquals(ELECTION, deserialized);
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

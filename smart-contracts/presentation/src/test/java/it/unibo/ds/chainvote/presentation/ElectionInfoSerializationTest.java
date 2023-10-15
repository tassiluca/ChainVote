package it.unibo.ds.chainvote.presentation;

import com.owlike.genson.Genson;
import it.unibo.ds.core.assets.*;
import it.unibo.ds.core.factory.ElectionFactory;
import it.unibo.ds.core.utils.Choice;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ElectionInfoSerializationTest {

    private final Genson genson = GensonUtils.create();
    private static final String GOAL = "prova";
    private static final long VOTERS = 400_000_000L;
    private static final Map<String, Integer> START_TIME_MAP = Map.of(
            "y", 2022,
            "M", 8,
            "d", 18,
            "h", 10,
            "m", 0,
            "s", 0
    );
    private static final Map<String, Integer> END_TIME_MAP = Map.of(
            "y", LocalDateTime.now().getYear()+1,
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
    private static final ElectionInfo ELECTION_INFO = ElectionFactory.buildElectionInfo(GOAL, VOTERS, START_DATE, END_DATE, CHOICES);

    private String getSerialized() {
        return "{\"goal\":\"" + GOAL + "\",\"voters\":\"" + VOTERS + "\",\"startingDate\":\""
                + genson.serialize(START_DATE) + "\",\"endingDate\":\"" + genson.serialize(END_DATE)
                + "\",\"choices\":\"" + genson.serialize(ELECTION_INFO.getChoices()) + "\"}";
    }

    @Test
    void testSerialization() {
        final var serialized = genson.serialize(ELECTION_INFO);
        System.out.println(serialized);
        assertEquals(getSerialized(), serialized.replace("\\", ""));
    }

    @Test
    void testDeserialization() {
        var prova = genson.deserialize("{\"goal\":\"prova\",\"voters\":\"100\",\"startingDate\":\"{\\\"year\\\":\\\"2023\\\",\\\"month\\\":\\\"8\\\",\\\"day\\\":\\\"20\\\",\\\"hour\\\":\\\"10\\\",\\\"minute\\\":\\\"0\\\",\\\"second\\\":\\\"0\\\"}\",\"endingDate\":\"{\\\"year\\\":\\\"2024\\\",\\\"month\\\":\\\"8\\\",\\\"day\\\":\\\"20\\\",\\\"hour\\\":\\\"10\\\",\\\"minute\\\":\\\"0\\\",\\\"second\\\":\\\"0\\\"}\",\"choices\":\"{\\\"value\\\":[{\\\"choice\\\":\\\"prova1\\\"},{\\\"choice\\\":\\\"prova2\\\"},{\\\"choice\\\":\\\"prova3\\\"},{\\\"choice\\\":\\\"prova4\\\"},{\\\"choice\\\":\\\"BLANK VOTE\\\"}]}\"}", ElectionInfo.class);
        System.out.println(prova);
        /*
        final var deserialized = genson.deserialize(genson.serialize(ELECTION_INFO), ElectionInfo.class);
        assertEquals(ELECTION_INFO, deserialized);

         */
    }
}

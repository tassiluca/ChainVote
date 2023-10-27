package it.unibo.ds.chainvote.presentation;

import com.owlike.genson.GenericType;
import com.owlike.genson.Genson;
import it.unibo.ds.core.assets.ElectionInfo;
import it.unibo.ds.core.factory.ElectionFactory;
import it.unibo.ds.core.utils.Choice;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ListOfElectionInfoSerializationTest {

    private final Genson genson = GensonUtils.create();
    private static final String GOAL_1 = "prova1";
    private static final long VOTERS_1 = 400_000_000L;
    private static final Map<String, Integer> START_TIME_MAP_1 = Map.of(
            "y", 2022,
            "M", 8,
            "d", 18,
            "h", 10,
            "m", 0,
            "s", 0
    );
    private static final Map<String, Integer> END_TIME_MAP_1 = Map.of(
            "y", LocalDateTime.now().getYear()+1,
            "M", 8,
            "d", 22,
            "h", 10,
            "m", 0,
            "s", 0
    );
    private static final LocalDateTime START_DATE_1 = LocalDateTime.of(START_TIME_MAP_1.get("y"), START_TIME_MAP_1.get("M"), START_TIME_MAP_1.get("d"),
            START_TIME_MAP_1.get("h"), START_TIME_MAP_1.get("m"), START_TIME_MAP_1.get("s"));
    private static final LocalDateTime END_DATE_1 = LocalDateTime.of(END_TIME_MAP_1.get("y"), END_TIME_MAP_1.get("M"), END_TIME_MAP_1.get("d"),
            END_TIME_MAP_1.get("h"), END_TIME_MAP_1.get("m"), END_TIME_MAP_1.get("s"));
    private static final List<Choice> CHOICES_1 = new ArrayList<>(List.of(new Choice("prova1"), new Choice("prova2"), new Choice("prova3")));
    private static final ElectionInfo ELECTION_INFO_1 = ElectionFactory.buildElectionInfo(GOAL_1, VOTERS_1, START_DATE_1, END_DATE_1, CHOICES_1);

    private static final String GOAL_2 = "prova2";
    private static final long VOTERS_2 = 400_000_000L;
    private static final Map<String, Integer> START_TIME_MAP_2 = Map.of(
            "y", 2022,
            "M", 8,
            "d", 18,
            "h", 10,
            "m", 0,
            "s", 0
    );
    private static final Map<String, Integer> END_TIME_MAP_2 = Map.of(
            "y", LocalDateTime.now().getYear()+1,
            "M", 8,
            "d", 22,
            "h", 10,
            "m", 0,
            "s", 0
    );
    private static final LocalDateTime START_DATE_2 = LocalDateTime.of(START_TIME_MAP_2.get("y"), START_TIME_MAP_2.get("M"), START_TIME_MAP_2.get("d"),
            START_TIME_MAP_2.get("h"), START_TIME_MAP_2.get("m"), START_TIME_MAP_2.get("s"));
    private static final LocalDateTime END_DATE_2 = LocalDateTime.of(END_TIME_MAP_2.get("y"), END_TIME_MAP_2.get("M"), END_TIME_MAP_2.get("d"),
            END_TIME_MAP_2.get("h"), END_TIME_MAP_2.get("m"), END_TIME_MAP_2.get("s"));
    private static final List<Choice> CHOICES_2 = new ArrayList<>(List.of(new Choice("prova1"), new Choice("prova2"), new Choice("prova3")));
    private static final ElectionInfo ELECTION_INFO_2 = ElectionFactory.buildElectionInfo(GOAL_2, VOTERS_2, START_DATE_2, END_DATE_2, CHOICES_2);
    private static final List<ElectionInfo> LIST = new ArrayList<>(Arrays.asList(ELECTION_INFO_1, ELECTION_INFO_2));

    @Test
    void testDeserialization() {
        final var deserialized = genson.deserialize(genson.serialize(LIST), new GenericType<List<ElectionInfo>>() {});
        assertEquals(LIST, deserialized);
    }
}

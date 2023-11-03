package it.unibo.ds.chainvote;

import com.owlike.genson.Genson;
import it.unibo.ds.chainvote.assets.Election;
import it.unibo.ds.chainvote.assets.ElectionInfo;
import it.unibo.ds.chainvote.assets.presentation.ElectionCompleteFacade;
import it.unibo.ds.chainvote.assets.presentation.ElectionCompleteFacadeImpl;
import it.unibo.ds.chainvote.factory.ElectionFactory;
import it.unibo.ds.chainvote.utils.Choice;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ElectionCompleteFacadeSerializationTest {
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
            "y", LocalDateTime.now().getYear() + 1,
            "M", 8,
            "d", 22,
            "h", 10,
            "m", 0,
            "s", 0
    );
    private static final LocalDateTime START_DATE = LocalDateTime.of(
            START_TIME_MAP.get("y"),
            START_TIME_MAP.get("M"),
            START_TIME_MAP.get("d"),
            START_TIME_MAP.get("h"),
            START_TIME_MAP.get("m"),
            START_TIME_MAP.get("s")
    );
    private static final LocalDateTime END_DATE = LocalDateTime.of(
            END_TIME_MAP.get("y"),
            END_TIME_MAP.get("M"),
            END_TIME_MAP.get("d"),
            END_TIME_MAP.get("h"),
            END_TIME_MAP.get("m"),
            END_TIME_MAP.get("s")
    );
    private static final List<Choice> CHOICES =
            List.of(new Choice("prova1"), new Choice("prova2"), new Choice("prova3"));
    private static final ElectionInfo ELECTION_INFO =
            ElectionFactory.buildElectionInfo(GOAL, VOTERS, START_DATE, END_DATE, CHOICES);
    private static final Election ELECTION = ElectionFactory.buildElection(ELECTION_INFO);

    @Test
    void testSerialization() {
        ElectionCompleteFacade etr = new ElectionCompleteFacadeImpl(ELECTION, ELECTION_INFO);
        System.out.println(genson.serialize(etr));
    }
}

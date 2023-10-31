package it.unibo.ds.chainvote;

import com.owlike.genson.GenericType;
import com.owlike.genson.Genson;
import it.unibo.ds.chainvote.assets.Ballot;
import it.unibo.ds.chainvote.assets.BallotImpl;
import it.unibo.ds.chainvote.utils.Choice;
import it.unibo.ds.chainvote.utils.Pair;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class ResultSerializationTest {

    private final Genson genson = GensonUtils.create();

    @Test
    void testSimpleResult() {
        final Pair<Result<String>, String> expectedResult = getSimpleResult();
        final var serialized = genson.serialize(expectedResult.first());
        assertEquals(expectedResult.second(), serialized);
        final var deserialized = genson.deserialize(serialized, Result.class);
        assertEquals(expectedResult.first(), deserialized);
    }

    @Test
    void testComplexResult() {
        final Pair<Result<Ballot>, String> expectedResult = getComplexResult();
        final var serialized = genson.serialize(expectedResult.first());
        assertEquals(expectedResult.second(), serialized);
        final Result<Ballot> deserialized = genson.deserialize(serialized, new GenericType<>() { });
        assertEquals(expectedResult.first(), deserialized);
    }

    @Test
    void testErrorResult() {
        final Pair<Result<Object>, String> expectedResult = getErrorResult();
        final var serialized = genson.serialize(expectedResult.first());
        assertEquals(expectedResult.second(), serialized);
        final Result<Object> deserialized = genson.deserialize(serialized, new GenericType<>() { });
        assertEquals(expectedResult.first(), deserialized);
    }

    private Pair<Result<String>, String> getSimpleResult() {
        final Result<String> result = Result.success("simple-result");
        return new Pair<>(
            result,
            "{\"error\":null,\"result\":\"simple-result\",\"success\":true}"
        );
    }

    private Pair<Result<Ballot>, String> getComplexResult() {
        final Ballot ballot = new BallotImpl.Builder()
            .electionID("test-election-id")
            .voterID("test-voter-id")
            .date(LocalDateTime.parse("2023-08-20T10:00:00"))
            .choice(new Choice("yes"))
            .choice(new Choice("no"))
            .build();
        final Result<Ballot> result = Result.success(ballot);
        return new Pair<>(
            result,
            "{\"error\":null,\"result\":" + genson.serialize(ballot) + ",\"success\":true}"
        );
    }

    private Pair<Result<Object>, String> getErrorResult() {
        final Result<Object> result = Result.error("Error message test");
        return new Pair<>(
            result,
            "{\"error\":\"Error message test\",\"result\":null,\"success\":false}"
        );
    }
}

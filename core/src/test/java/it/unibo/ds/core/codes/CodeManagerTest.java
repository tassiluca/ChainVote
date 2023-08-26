package it.unibo.ds.core.codes;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CodeManagerTest {

    private static final String ELECTION_ID = "test-election";
    private static final String USER_ID = "mrossi";
    private final CodeManager<Void> localManager = new CodeManagerImpl<>(new CodeRepository<>() {

        private final Map<String, Set<OneTimeCode>> electionCodes = new HashMap<>();
        private final Map<String, Map<String, OneTimeCode>> codes = new HashMap<>();

        @Override
        public Optional<OneTimeCode> get(final Void context, final String electionId, final String userId) {
            return Optional.ofNullable(codes.getOrDefault(electionId, Map.of()).get(userId));
        }

        @Override
        public void put(final Void context, final String electionId, final String userId, final OneTimeCode code) {
            put(context, electionId, code);
            codes.putIfAbsent(electionId, new HashMap<>());
            codes.get(electionId).put(userId, code);
        }

        @Override
        public void put(final Void context, final String electionId, final OneTimeCode code) {
            electionCodes.putIfAbsent(electionId, new HashSet<>());
            electionCodes.get(electionId).add(code);
        }

        @Override
        public void replace(final Void context, final String electionId, final OneTimeCode code) {
            final var searchedEntry = codes.get(electionId).entrySet().stream()
                .filter(e -> e.getValue().equals(code))
                .findFirst()
                .orElseThrow();
            codes.get(electionId).replace(searchedEntry.getKey(), code);
        }

        @Override
        public Set<OneTimeCode> getAllOf(final Void context, final String electionId) {
            return Collections.unmodifiableSet(electionCodes.getOrDefault(electionId, Set.of()));
        }
    });

    @Test
    void testGenerate() {
        final var code = localManager.generateFor(ELECTION_ID, USER_ID);
        assertNotNull(code);
    }

    @Test
    void testGenerateMultipleTimes() {
        localManager.generateFor(ELECTION_ID, USER_ID);
        assertThrows(IllegalStateException.class, () -> localManager.generateFor(ELECTION_ID, USER_ID));
    }

    @Test
    void testGenerateAll() {
        final int codesToGenerated = 1_000;
        final var codes = localManager.generateAllFor(ELECTION_ID, codesToGenerated);
        assertEquals(codesToGenerated, codes.size());
        assertEquals(codesToGenerated, codes.stream().distinct().count());
    }

    @Test
    void testGenerateAllMultipleTimes() {
        localManager.generateAllFor(ELECTION_ID, 1);
        assertThrows(IllegalStateException.class, () -> localManager.generateAllFor(ELECTION_ID, 1));
    }

    @Test
    void testCodeValidity() {
        final OneTimeCode code = localManager.generateFor(ELECTION_ID, USER_ID);
        assertTrue(localManager.isValid(ELECTION_ID, code));
    }

    @Test
    void testUnknownCodeValidity() {
        assertFalse(localManager.isValid(ELECTION_ID, new OneTimeCodeImpl(0L)));
    }

    @Test
    void testCodeInvalidation() {
        final OneTimeCode code = localManager.generateFor(ELECTION_ID, USER_ID);
        localManager.invalidate(ELECTION_ID, code);
        assertFalse(localManager.isValid(ELECTION_ID, code));
    }

    @Test
    void testAttemptInvalidationOnUnknownCode() {
        assertThrows(IllegalStateException.class, () -> localManager.invalidate(ELECTION_ID, new OneTimeCodeImpl(0L)));
    }

    @Test
    void testVerifyCodeOwner() {
        final OneTimeCode code = localManager.generateFor(ELECTION_ID, USER_ID);
        assertTrue(localManager.verifyCodeOwner(ELECTION_ID, USER_ID, code));
    }
}

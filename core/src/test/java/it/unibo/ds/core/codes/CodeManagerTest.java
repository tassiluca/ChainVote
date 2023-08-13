package it.unibo.ds.core.codes;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CodeManagerTest {

    private static final String ELECTION_ID = "test-election";
    private static final String USER_ID = "mrossi";
    private final CodeManager<Void> localManager = new CodeManagerImpl<>(new CodeRepository<>() {
        private final Map<String, Map<String, OneTimeCode>> codes = new HashMap<>();

        @Override
        public Optional<OneTimeCode> get(final Void context, final String electionId, final String userId) {
            return Optional.ofNullable(codes.getOrDefault(electionId, Map.of()).get(userId));
        }

        @Override
        public void put(final Void context, final String electionId, final String userId, final OneTimeCode code) {
            codes.putIfAbsent(electionId, new HashMap<>());
            codes.get(electionId).put(userId, code);
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
            return new HashSet<>(codes.getOrDefault(electionId, Map.of()).values());
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

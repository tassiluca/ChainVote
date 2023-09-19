package it.unibo.ds.core.codes;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        public void replace(final Void context, final String electionId, final String userId, final OneTimeCode code) {
            codes.get(electionId).replace(userId, code);
        }
    });

    @Test
    void testGenerate() throws AlreadyGeneratedCodeException {
        final var code = localManager.generateFor(ELECTION_ID, USER_ID);
        assertNotNull(code);
    }

    @Test
    void testGenerateMultipleTimes() throws AlreadyGeneratedCodeException {
//        localManager.generateFor(ELECTION_ID, USER_ID);
//        assertThrows(IllegalStateException.class, () -> localManager.generateFor(ELECTION_ID, USER_ID));
    }

    @Test
    void testCodeValidity() throws AlreadyGeneratedCodeException {
        final OneTimeCode code = localManager.generateFor(ELECTION_ID, USER_ID);
        assertTrue(localManager.isValid(ELECTION_ID, USER_ID, code));
    }

    @Test
    void testUnknownCodeValidity() {
        assertFalse(localManager.isValid(ELECTION_ID, USER_ID, new OneTimeCodeImpl(0L)));
    }

    @Test
    void testCodeInvalidation() throws AlreadyGeneratedCodeException, AlreadyConsumedCodeException, NotValidCodeException {
        final OneTimeCode code = localManager.generateFor(ELECTION_ID, USER_ID);
        localManager.invalidate(ELECTION_ID, USER_ID, code);
        assertFalse(localManager.isValid(ELECTION_ID, USER_ID, code));
    }

    @Test
    void testCodeInvalidationMultipleTimes() throws AlreadyGeneratedCodeException, AlreadyConsumedCodeException, NotValidCodeException {
//        final OneTimeCode code = localManager.generateFor(ELECTION_ID, USER_ID);
//        localManager.invalidate(ELECTION_ID, USER_ID, code);
//        assertThrows(IllegalStateException.class, () -> localManager.invalidate(ELECTION_ID, USER_ID, code));
    }

    @Test
    void testAttemptInvalidationOnUnknownCode() {
//        assertThrows(
//            IllegalStateException.class,
//            () -> localManager.invalidate(ELECTION_ID, USER_ID, new OneTimeCodeImpl(0L))
//        );
    }

    @Test
    void testVerifyCodeOwner() throws AlreadyGeneratedCodeException {
        final OneTimeCode code = localManager.generateFor(ELECTION_ID, USER_ID);
        assertTrue(localManager.verifyCodeOwner(ELECTION_ID, USER_ID, code));
    }
}

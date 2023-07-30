package it.unibo.ds.core.codes;

import java.security.SecureRandom;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * An implementation of {@link CodeGenerator} that uses {@link java.security.SecureRandom}.
 */
public final class SecureRandomGenerator implements CodeGenerator {

    private final SecureRandom rand = new SecureRandom();

    @Override
    public OneTimeCode generateCode(final Predicate<OneTimeCode> alreadyGeneratedPredicate) {
        return Stream.generate(rand::nextLong)
            .map(OneTimeCodeImpl::new)
            .filter(r -> !alreadyGeneratedPredicate.test(r))
            .findAny()
            .orElseThrow();
    }
}

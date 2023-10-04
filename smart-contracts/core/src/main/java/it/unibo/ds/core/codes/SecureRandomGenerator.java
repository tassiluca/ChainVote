package it.unibo.ds.core.codes;

import java.security.SecureRandom;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * An implementation of {@link CodeGeneratorStrategy} that uses {@link java.security.SecureRandom}.
 */
final class SecureRandomGenerator implements CodeGeneratorStrategy {

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

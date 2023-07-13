package it.unibo.ds.core;

import java.security.SecureRandom;
import java.util.function.Predicate;

/**
 * An implementation of {@link CodeGenerator} that uses {@link java.security.SecureRandom}.
 */
public class SecureRandomGenerator implements CodeGenerator {

    final SecureRandom rand = new SecureRandom();

    @Override
    public Long generateCode(Predicate<Long> alreadyGeneratedPredicate) {
        long generated;
        do {
            generated = rand.nextLong();
        } while (alreadyGeneratedPredicate.test(generated));
        return generated;
    }
}

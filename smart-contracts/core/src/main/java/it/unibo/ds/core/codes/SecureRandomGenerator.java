package it.unibo.ds.core.codes;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * An implementation of {@link CodeGeneratorStrategy} that uses {@link java.security.SecureRandom}.
 */
public final class SecureRandomGenerator implements CodeGeneratorStrategy {

    @Override
    public OneTimeCode generateCode(final Predicate<OneTimeCode> alreadyGenerated) {
        return generate(new SecureRandom(), alreadyGenerated);
    }

    /**
     * Generates a new, not already generated, code.
     * @param alreadyGenerated a {@link Predicate} that returns true if
     *                         the tested code was already generated, false otherwise
     * @param arg a string argument as seed in the generation phase.
     * @return a
     */
    @Override
    public OneTimeCode generateCode(final Predicate<OneTimeCode> alreadyGenerated, final String arg) {
        return generate(new SecureRandom(arg.getBytes(StandardCharsets.UTF_8)), alreadyGenerated);
    }

    private OneTimeCode generate(final SecureRandom rand, final Predicate<OneTimeCode> alreadyGenerated) {
        return Stream.generate(rand::nextLong)
            .map(l -> Long.toString(l))
            .map(OneTimeCodeImpl::new)
            .filter(r -> !alreadyGenerated.test(r))
            .findAny()
            .orElseThrow();
    }
}

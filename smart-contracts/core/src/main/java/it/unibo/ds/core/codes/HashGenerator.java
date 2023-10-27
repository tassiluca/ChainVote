package it.unibo.ds.core.codes;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.util.function.Predicate;

/**
 * An implementation of {@link CodeGeneratorStrategy} that uses a hash function.
 * In order to be used an initial argument must be provided.
 */
public final class HashGenerator implements CodeGeneratorStrategy {

    private static final int CODE_LENGTH = 10;

    @Override
    public OneTimeCode generateCode(final Predicate<OneTimeCode> alreadyGenerated) {
        throw new UnsupportedOperationException(
            "No hash function based code can be generated without an argument. \n" +
            "See `OneTimeCode generateCode(Predicate<OneTimeCode> alreadyGenerated, String arg)`."
        );
    }

    /**
     * Generates a new, not already generated, code.
     * @param alreadyGenerated a {@link Predicate} that returns true if
     *                         the tested code was already generated, false otherwise
     * @param arg a string used as an argument to the hash function!
     * @return the new generated code.
     */
    @Override
    public OneTimeCode generateCode(final Predicate<OneTimeCode> alreadyGenerated, final String arg) {
        return new OneTimeCodeImpl(
            Hashing.sha256()
                .hashString(arg, StandardCharsets.UTF_8)
                .toString()
                .substring(0, CODE_LENGTH)
        );
    }
}

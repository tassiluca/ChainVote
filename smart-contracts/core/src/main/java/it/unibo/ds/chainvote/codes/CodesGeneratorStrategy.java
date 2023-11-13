package it.unibo.ds.chainvote.codes;

import java.util.Set;
import java.util.function.Predicate;

/**
 * An interface modeling a {@link OneTimeCode} generator strategy.
 */
public interface CodesGeneratorStrategy {

    /**
     * Generates a new, not already generated, code.
     * @param alreadyGenerated a {@link Predicate} that returns true if
     *                         the tested code was already generated, false otherwise.
     * @return the new generated code.
     */
    OneTimeCode generateCode(Predicate<OneTimeCode> alreadyGenerated);

    /**
     * Generates a new, not already generated, code.
     * @param alreadyGenerated a {@link Predicate} that returns true if
     *                         the tested code was already generated, false otherwise
     * @param arg a string argument to be used in the generation phase. Note this is implementation dependant!
     * @return the new generated code.
     */
    OneTimeCode generateCode(Predicate<OneTimeCode> alreadyGenerated, String arg);

    /**
     * Generates a new code different from those given in input.
     * @param alreadyGenerated a {@link Set} containing the already generated codes.
     * @return a new generated code.
     */
    default OneTimeCode generateCode(final Set<OneTimeCode> alreadyGenerated) {
        return generateCode(alreadyGenerated::contains);
    }

    /**
     * Generates a new code different from those given in input.
     * @param alreadyGenerated a {@link Set} containing the already generated codes.
     * @param arg a string argument to be used in the generation phase. Note this is implementation dependant!
     * @return the new generated code.
     */
    default OneTimeCode generateCode(final Set<OneTimeCode> alreadyGenerated, final String arg) {
        return generateCode(alreadyGenerated::contains, arg);
    }

    /**
     * Generates a new code, no matter if it has already been generated.
     * @return a new generated code.
     */
    default OneTimeCode generateCode() {
        return generateCode(Set.of());
    }

    /**
     * Generates a new code, no matter if it has already been generated.
     * @param arg a string argument to be used in the generation phase. Note this is implementation dependant!
     * @return a new generated code.
     */
    default OneTimeCode generateCode(final String arg) {
        return generateCode(Set.of(), arg);
    }
}

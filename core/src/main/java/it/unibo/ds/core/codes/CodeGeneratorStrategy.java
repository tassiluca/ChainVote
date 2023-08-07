package it.unibo.ds.core.codes;

import java.util.Set;
import java.util.function.Predicate;

/**
 * An interface modeling a {@link OneTimeCode} generator strategy.
 */
@FunctionalInterface
interface CodeGeneratorStrategy {

    /**
     * Generate a new (not already generated) code.
     * @param alreadyGeneratedPredicate a {@link Predicate} that returns true if
     * the tested code was already generated, false otherwise.
     * @return a new generated code.
     */
    OneTimeCode generateCode(Predicate<OneTimeCode> alreadyGeneratedPredicate);

    /**
     * Generate a new code different from those given in input.
     * @param alreadyGenerated a {@link Set} containing the already generated codes.
     * @return a new generated code.
     */
    default OneTimeCode generateCode(final Set<OneTimeCode> alreadyGenerated) {
        return generateCode(alreadyGenerated::contains);
    }
}

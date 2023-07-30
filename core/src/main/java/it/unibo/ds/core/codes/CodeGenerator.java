package it.unibo.ds.core.codes;

import java.util.Set;
import java.util.function.Predicate;

/**
 * A generator of {@link OneTimeCode} used by the clients to cast a new vote.
 */
@FunctionalInterface
interface CodeGenerator {

    /**
     * Generate a new (not already generated) code.
     * @param alreadyGeneratedPredicate a {@link Predicate} used to
     * ensure that a new not already generated code is provided.
     * @return a new generated code.
     */
    OneTimeCode generateCode(Predicate<OneTimeCode> alreadyGeneratedPredicate);

    /**
     * Generate a new code different from those given.
     * @param alreadyGenerated a {@link Set} containing the already generated codes.
     * @return a new generated code.
     */
    default OneTimeCode generateCode(final Set<OneTimeCode> alreadyGenerated) {
        return generateCode(alreadyGenerated::contains);
    }
}

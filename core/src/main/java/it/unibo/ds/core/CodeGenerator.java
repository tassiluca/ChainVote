package it.unibo.ds.core;

import java.util.Set;
import java.util.function.Predicate;

/**
 * A generator of codes used by the clients to cast a new vote.
 */
@FunctionalInterface
public interface CodeGenerator {

    /**
     * Generate a new (not already generated) code.
     * @param alreadyGeneratedPredicate a {@link Predicate} used to
     * ensure that a new not already generated code is provided.
     * @return a new generated code.
     */
    Long generateCode(Predicate<Long> alreadyGeneratedPredicate);

    /**
     * Generate a new code different from those given.
     * @param alreadyGenerated a {@link Set} containing the already generated codes.
     * @return a new generated code.
     */
    default Long generateCode(Set<Long> alreadyGenerated) {
        return generateCode(alreadyGenerated::contains);
    }
}

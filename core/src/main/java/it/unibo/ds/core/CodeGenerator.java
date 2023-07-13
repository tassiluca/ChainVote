package it.unibo.ds.core;

import java.util.function.Predicate;

/**
 * A generator of codes used by the clients to cast a new vote.
 */
@FunctionalInterface
public interface CodeGenerator {

    /**
     * Generate a new code.
     * @param alreadyGeneratedPredicate a {@link Predicate} used to
     * ensure that a new not already generated code is provided.
     * @return a new generated code.
     */
    Long generateCode(Predicate<Long> alreadyGeneratedPredicate);
}

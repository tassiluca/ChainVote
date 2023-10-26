package it.unibo.ds.core.codes;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class CodeGeneratorStrategiesTest {

    /* NOTE: here are not tested the method which guarantees to not generate already generated codes
     *       cause their non-deterministic nature and difficult reproducibility. */

    @Test
    void testRandomGeneration() {
        final var strategy = new SecureRandomGenerator();
        assertNotNull(strategy.generateCode());
    }

    @Test
    void testHashBasedGeneration() {
        final HashGenerator strategy = new HashGenerator();
        assertThrows(UnsupportedOperationException.class, strategy::generateCode);
        assertNotNull(strategy.generateCode(Integer.toString(new Random().nextInt())));
    }
}

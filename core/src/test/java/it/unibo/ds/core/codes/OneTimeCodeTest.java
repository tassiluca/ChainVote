package it.unibo.ds.core.codes;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SuppressFBWarnings(
    value = { "UwF" },
    justification = "`code` is initialized before every test function inside the setup @BeforeEach method."
)
class OneTimeCodeTest {

    private final CodeGeneratorStrategy codeGenerator = new SecureRandomGenerator();
    private OneTimeCode code;

    @BeforeEach
    void setup() {
        this.code = codeGenerator.generateCode(Set.of());
        assertFalse(this.code.consumed());
    }

    @Test
    void testConsume() {
        this.code.consume();
        assertTrue(this.code.consumed());
    }

    @Test
    void testConsumeMultipleTimes() {
        this.code.consume();
        assertThrows(IllegalStateException.class, () -> this.code.consume());
    }
}

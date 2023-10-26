package it.unibo.ds.core.codes;

import java.util.Objects;

/**
 * A simple {@link OneTimeCode} implementation.
 */
public final class OneTimeCodeImpl implements OneTimeCode {

    private final String code;
    private boolean consumed;

    /**
     * Creates a new one time code.
     * @param code the generated random code.
     */
    public OneTimeCodeImpl(final String code) {
        this.code = code;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public void consume() throws InvalidCodeException {
        if (this.consumed) {
            throw new InvalidCodeException("The code has already been consumed");
        }
        this.consumed = true;
    }

    @Override
    public boolean consumed() {
        return this.consumed;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        } else if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final OneTimeCodeImpl that = (OneTimeCodeImpl) o;
        return Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return "OneTimeCodeImpl{code=" + code + ", consumed=" + consumed + '}';
    }
}

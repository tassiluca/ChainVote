package it.unibo.ds.chainvote.utils;

import java.util.Objects;

/**
 * A simple class modeling a two-dimensional tuple.
 * @param <X> the type of the first element
 * @param <Y> the type of the second element
 */
public final class Pair<X, Y> {

    private final X x;
    private final Y y;

    /**
     * Builds a new Pair.
     * @param x the element in the first position
     * @param y the element in the second position
     */
    public Pair(final X x, final Y y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @return the first component of this tuple
     */
    public X _1() {
        return getX();
    }

    /**
     * @return the first component of this tuple
     */
    public X first() {
        return getX();
    }

    /**
     * @return the first component of this tuple
     */
    public X getX() {
        return x;
    }

    /**
     * @return the second component of this tuple
     */
    public Y _2() {
        return getY();
    }

    /**
     * @return the second component of this tuple
     */
    public Y second() {
        return getY();
    }

    /**
     * @return the second component of this tuple
     */
    public Y getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(x, pair.x) && Objects.equals(y, pair.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Pair{ x=" + x + ", y=" + y + '}';
    }
}

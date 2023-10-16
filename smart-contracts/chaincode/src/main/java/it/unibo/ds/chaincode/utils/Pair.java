package it.unibo.ds.chaincode.utils;

import java.util.Objects;

/**
 * A simple class modeling a two-dimensional tuple.
 * @param <X> the type of the first element
 * @param <Y> the type of the second element
 */
public class Pair<X, Y> {

    private final X x;
    private final Y y;

    public Pair(final X x, final Y y) {
        this.x = x;
        this.y = y;
    }

    public X _1() {
        return getX();
    }

    public X first() {
        return getX();
    }

    public X getX() {
        return x;
    }

    public Y _2() {
        return getY();
    }

    public Y second() {
        return getY();
    }

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

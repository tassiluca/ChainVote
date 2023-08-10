package it.unibo.ds.core.utils;

import java.util.Objects;

/**
 * An implementation of a choice in a {@link it.unibo.ds.core.assets.Election}.
 */
public final class Choice {

    private final String choice;

    /**
     * Constructor with {@link Choice} given as String.
     * @param choice the {@link Choice}.
     */
    public Choice(final String choice) {
        this.choice = choice;
    }

    /**
     * Constructor with {@link Choice} given as {@link Choice}.
     * @param choice the {@link Choice}.
     */
    public Choice(final Choice choice) {
        this.choice = choice.getChoice();
    }

    /**
     * Return the Choice.
     * @return the {@link Choice}.
     */
    public String getChoice() {
        return this.choice;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Choice choice1 = (Choice) o;
        return Objects.equals(choice, choice1.choice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(choice);
    }
}

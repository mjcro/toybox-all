package io.github.mjcro.toybox.templates;

import io.github.mjcro.interfaces.tuples.OptionalPair;
import io.github.mjcro.toybox.api.Label;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Iterator;

/**
 * Empty enumeration values implementation used as the default
 * for {@link Databind#enumerationProvider()}. Always reports no elements.
 */
public final class VoidEnumerationValues implements Iterable<OptionalPair<?, Label>>, Iterator<OptionalPair<?, Label>> {
    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull Iterator<OptionalPair<?, Label>> iterator() {
        return this;
    }

    /**
     * Always returns {@code false} since this enumeration is empty.
     *
     * @return {@code false}
     */
    @Override
    public boolean hasNext() {
        return false;
    }

    /**
     * Always returns {@code null} since this enumeration is empty.
     *
     * @return {@code null}
     */
    @Override
    public @Nullable EnumerationValue<?> next() {
        return null;
    }
}

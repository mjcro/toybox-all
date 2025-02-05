package io.github.mjcro.toybox.templates;

import io.github.mjcro.interfaces.tuples.OptionalPair;
import io.github.mjcro.toybox.api.Label;

import java.util.Iterator;

public final class VoidEnumerationValues implements Iterable<OptionalPair<?, Label>>, Iterator<OptionalPair<?, Label>> {
    @Override
    public Iterator<OptionalPair<?, Label>> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public EnumerationValue<?> next() {
        return null;
    }
}

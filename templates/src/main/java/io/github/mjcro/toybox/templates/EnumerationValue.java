package io.github.mjcro.toybox.templates;

import io.github.mjcro.interfaces.tuples.OptionalPair;
import io.github.mjcro.toybox.api.Label;
import io.github.mjcro.toybox.api.Labeled;

import java.util.Objects;
import java.util.Optional;

/**
 * Represents enumeration value, where keys can be null.
 *
 * @param <T> Enumeration key type.
 */
public class EnumerationValue<T> implements OptionalPair<T, Label> {
    private final T key;
    private final Label label;

    /**
     * Construct new enumeration value.
     *
     * @param key  Enumeration key, nullable.
     * @param text Text to display.
     */
    public EnumerationValue(T key, String text) {
        this(key, Label.ofName(text));
    }

    /**
     * Construct new enumeration value.
     *
     * @param key   Enumeration key, nullable.
     * @param label Label to display.
     */
    public EnumerationValue(T key, Label label) {
        this.key = key;
        this.label = Objects.requireNonNull(label, "label");
    }

    /**
     * @return Enumeration value key, nullable.
     */
    public Optional<T> getKey() {
        return Optional.ofNullable(key);
    }

    /**
     * Checks if enumeration value has same key as given one.
     *
     * @param candidate Object to compare enumeration key with.
     * @return True if enumeration value key equal to given candidate.
     */
    public boolean hasKey(Object candidate) {
        return Objects.equals(key, candidate);
    }

    @Override
    public Object get(int i) {
        if (i == 0) {
            return key;
        } else if (i == 1) {
            return label;
        } else {
            throw new IndexOutOfBoundsException("" + i);
        }
    }

    @Override
    public Optional<T> getFirst() {
        return Optional.ofNullable(key);
    }

    @Override
    public Optional<Label> getSecond() {
        return Optional.of(label);
    }

    @Override
    public String toString() {
        return getSecond().map(Labeled::getName).orElse("");
    }
}

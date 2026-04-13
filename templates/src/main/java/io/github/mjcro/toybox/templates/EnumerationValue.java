package io.github.mjcro.toybox.templates;

import io.github.mjcro.interfaces.tuples.OptionalPair;
import io.github.mjcro.toybox.api.Label;
import io.github.mjcro.toybox.api.Labeled;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

/**
 * Represents an enumeration value whose key may be {@code null}.
 *
 * @param <T> the enumeration key type
 */
public class EnumerationValue<T> implements OptionalPair<T, Label> {
    private final @Nullable T key;
    private final @NonNull Label label;

    /**
     * Constructs a new enumeration value with a plain text label.
     *
     * @param key  the enumeration key, may be {@code null}
     * @param text the text to display
     */
    public EnumerationValue(@Nullable T key, @NonNull String text) {
        this(key, Label.ofName(text));
    }

    /**
     * Constructs a new enumeration value with the given label.
     *
     * @param key   the enumeration key, may be {@code null}
     * @param label the label to display
     */
    public EnumerationValue(@Nullable T key, @NonNull Label label) {
        this.key = key;
        this.label = Objects.requireNonNull(label, "label");
    }

    /**
     * Returns the enumeration key wrapped in an optional.
     *
     * @return the key, or empty if {@code null}
     */
    public @NonNull Optional<T> getKey() {
        return Optional.ofNullable(key);
    }

    /**
     * Checks whether this enumeration value has the same key as the given candidate.
     *
     * @param candidate the object to compare with
     * @return {@code true} if the key equals the candidate
     */
    public boolean hasKey(@Nullable Object candidate) {
        return Objects.equals(key, candidate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable Object get(int i) {
        if (i == 0) {
            return key;
        } else if (i == 1) {
            return label;
        } else {
            throw new IndexOutOfBoundsException("" + i);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull Optional<T> getFirst() {
        return Optional.ofNullable(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull Optional<@NonNull Label> getSecond() {
        return Optional.of(label);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull String toString() {
        return getSecond().map(Labeled::getName).orElse("");
    }
}

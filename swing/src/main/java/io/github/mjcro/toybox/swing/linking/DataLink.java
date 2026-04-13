package io.github.mjcro.toybox.swing.linking;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

/**
 * Two-way data binding interface for getting and setting a value.
 *
 * @param <V> value type
 */
public interface DataLink<V> {
    /**
     * Sets the current value.
     *
     * @param value value to set, may be {@code null}
     */
    void setValue(@Nullable V value);

    /**
     * Returns the current value wrapped in an {@link Optional}.
     *
     * @return optional containing the current value
     */
    @NonNull Optional<@Nullable V> getValue();
}

package io.github.mjcro.toybox.api;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Defines an application setting.
 *
 * <p>Each setting must be a concrete class; the namespace and name are used
 * for informational and display purposes only.</p>
 */
public interface Setting {
    /**
     * Returns the namespace this setting belongs to.
     *
     * @return setting namespace, never {@code null}
     */
    @NonNull String getNamespace();

    /**
     * Returns the human-readable name of this setting.
     *
     * @return setting name, never {@code null}
     */
    @NonNull String getName();

    /**
     * Returns the current value of this setting.
     *
     * @return setting value, may be {@code null}
     */
    @Nullable Object getValue();

    /**
     * Prepares the value for rendering in the settings viewer.
     * Implementations should mask sensitive data.
     *
     * @return display-safe string representation of the value, never {@code null}
     */
    default @NonNull String getDisplayValue() {
        @Nullable Object v = getValue();
        return v == null ? "null" : v.toString();
    }

    /**
     * @return True if setting is sensitive, false otherwise
     */
    default boolean isSensitive() {
        return true;
    }
}

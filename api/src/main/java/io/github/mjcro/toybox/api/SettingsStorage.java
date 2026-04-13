package io.github.mjcro.toybox.api;

import org.jspecify.annotations.NonNull;

import java.util.Optional;

/**
 * Defines a storage mechanism for application settings.
 *
 * <p>Implementations provide persistence for {@link Setting} instances
 * and support lookup, insertion, and removal by setting class.</p>
 */
public interface SettingsStorage extends Iterable<@NonNull Setting> {
    /**
     * Fetches a setting from storage by its class.
     *
     * @param clazz the setting class to look up
     * @param <T>   the setting type
     * @return an {@link Optional} containing the matched setting, or empty if not found
     */
    <T extends Setting> @NonNull Optional<@NonNull T> get(@NonNull Class<@NonNull T> clazz);

    /**
     * Stores a setting value, replacing any previous value of the same class.
     *
     * @param setting the setting to store
     */
    void put(@NonNull Setting setting);

    /**
     * Removes a setting from storage by its class.
     *
     * @param clazz the setting class to remove
     */
    void remove(@NonNull Class<? extends @NonNull Setting> clazz);
}

package io.github.mjcro.toybox.api;

import java.util.Optional;

/**
 * Defines setting storage.
 */
public interface SettingsStorage extends Iterable<Setting> {
    /**
     * Fetches setting from storage.
     *
     * @param clazz Setting class.
     * @return Matched setting if any.
     */
    <T extends Setting> Optional<T> get(Class<T> clazz);

    /**
     * Places setting value to storage.
     *
     * @param setting Setting to place.
     */
    void put(Setting setting);

    /**
     * Removes setting value from storage.
     *
     * @param clazz Setting class.
     */
    void remove(Class<? extends Setting> clazz);
}

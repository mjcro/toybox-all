package io.github.mjcro.toybox.app.settings.storage;

import io.github.mjcro.toybox.api.Setting;
import io.github.mjcro.toybox.api.SettingsStorage;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory settings storage backed by a {@link ConcurrentHashMap}.
 * Settings are not persisted and are lost when the application exits.
 */
public class MemorySettingsStorage implements SettingsStorage {
    private final @NonNull ConcurrentHashMap<@NonNull Class<? extends @NonNull Setting>, @NonNull Setting> memory = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Setting> @NonNull Optional<@NonNull T> get(@NonNull Class<@NonNull T> clazz) {
        return Optional.ofNullable((T) memory.get(clazz));
    }

    @Override
    public void put(@Nullable Setting setting) {
        if (setting != null) {
            memory.put(setting.getClass(), setting);
        }
    }

    @Override
    public void remove(@NonNull Class<? extends @NonNull Setting> clazz) {
        if (clazz != null) {
            memory.remove(clazz);
        }
    }

    @Override
    public @NonNull Iterator<@NonNull Setting> iterator() {
        return memory.values().iterator();
    }
}

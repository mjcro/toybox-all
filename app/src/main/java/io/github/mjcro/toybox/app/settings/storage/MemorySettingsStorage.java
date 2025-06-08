package io.github.mjcro.toybox.app.settings.storage;

import io.github.mjcro.toybox.api.Setting;
import io.github.mjcro.toybox.api.SettingsStorage;

import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class MemorySettingsStorage implements SettingsStorage {
    private final ConcurrentHashMap<Class<? extends Setting>, Setting> memory = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Setting> Optional<T> get(Class<T> clazz) {
        return Optional.ofNullable((T) memory.get(clazz));
    }

    @Override
    public void put(Setting setting) {
        if (setting != null) {
            memory.put(setting.getClass(), setting);
        }
    }

    @Override
    public void remove(Class<? extends Setting> clazz) {
        if (clazz != null) {
            memory.remove(clazz);
        }
    }

    @Override
    public Iterator<Setting> iterator() {
        return memory.values().iterator();
    }
}

package io.github.mjcro.toybox.app.settings.storage;

import io.github.mjcro.toybox.api.Setting;
import io.github.mjcro.toybox.api.SettingsStorage;
import io.github.mjcro.toybox.app.settings.ToyBoxSettingFileCreatedSetting;

import java.io.File;
import java.util.Iterator;
import java.util.Optional;

public class SettingsStorageDispatcher implements SettingsStorage {
    private final MemorySettingsStorage memory = new MemorySettingsStorage();
    private volatile SettingsStorage fileStorage = null;
    private volatile File file = null;

    public boolean isFileStorageEnabled() {
        return fileStorage != null;
    }

    public Optional<String> getFileName() {
        return Optional.ofNullable(file).map(File::getName);
    }

    public synchronized void close() {
        fileStorage = null;
        file = null;
    }

    public synchronized void bindExistingSettingsFile(File file, String password) {
        SettingsStorage storage = CipheredJsonFileStorage.Aes256Gcm(file, password);
        // Reading data to verify that everything is ok
        storage.get(ToyBoxSettingFileCreatedSetting.class);
        fileStorage = storage;
    }

    public synchronized void createNewSettingFile(File file, String password) {
        SettingsStorage storage = CipheredJsonFileStorage.Aes256Gcm(file, password);
        storage.put(new ToyBoxSettingFileCreatedSetting());
        for (Setting setting : memory) {
            if (!(setting instanceof ToyBoxSettingFileCreatedSetting)) {
                storage.put(setting);
            }
        }
        fileStorage = storage;
    }

    public synchronized SettingsStorage getActiveStorage() {
        if (isFileStorageEnabled()) {
            return fileStorage;
        }
        return memory;
    }

    @Override
    public <T extends Setting> Optional<T> get(Class<T> clazz) {
        return getActiveStorage().get(clazz);
    }

    @Override
    public void put(Setting setting) {
        getActiveStorage().put(setting);
    }

    @Override
    public void remove(Class<? extends Setting> clazz) {
        getActiveStorage().remove(clazz);
    }

    @Override
    public Iterator<Setting> iterator() {
        return getActiveStorage().iterator();
    }
}

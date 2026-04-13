package io.github.mjcro.toybox.app.settings.storage;

import io.github.mjcro.toybox.api.Setting;
import io.github.mjcro.toybox.api.SettingsStorage;
import io.github.mjcro.toybox.app.settings.ToyBoxSettingFileCreatedSetting;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.util.Iterator;
import java.util.Optional;

/**
 * Dispatching settings storage that delegates to either an in-memory store
 * or an encrypted file-based store depending on configuration state.
 */
public class SettingsStorageDispatcher implements SettingsStorage {
    private final @NonNull MemorySettingsStorage memory = new MemorySettingsStorage();
    private volatile @Nullable SettingsStorage fileStorage = null;
    private volatile @Nullable File file = null;

    /**
     * Returns whether a file-based settings storage is currently bound.
     *
     * @return true if file storage is enabled
     */
    public boolean isFileStorageEnabled() {
        return fileStorage != null;
    }

    /**
     * Returns the name of the currently bound settings file, if any.
     *
     * @return optional containing the file name, or empty if no file is bound
     */
    public @NonNull Optional<@NonNull String> getFileName() {
        return Optional.ofNullable(file).map(File::getName);
    }

    /**
     * Closes the file storage binding, reverting to in-memory storage.
     */
    public synchronized void close() {
        fileStorage = null;
        file = null;
    }

    /**
     * Binds an existing encrypted settings file using the given password.
     * Verifies file integrity by reading a marker setting.
     *
     * @param file     the settings file to bind
     * @param password the decryption password
     */
    public synchronized void bindExistingSettingsFile(@NonNull File file, @NonNull String password) {
        SettingsStorage storage = CipheredJsonFileStorage.Aes256Gcm(file, password);
        // Reading data to verify that everything is ok
        storage.get(ToyBoxSettingFileCreatedSetting.class);
        fileStorage = storage;
    }

    /**
     * Creates a new encrypted settings file, migrating current in-memory settings.
     *
     * @param file     the file to create
     * @param password the encryption password
     */
    public synchronized void createNewSettingFile(@NonNull File file, @NonNull String password) {
        SettingsStorage storage = CipheredJsonFileStorage.Aes256Gcm(file, password);
        storage.put(new ToyBoxSettingFileCreatedSetting());
        for (Setting setting : memory) {
            if (!(setting instanceof ToyBoxSettingFileCreatedSetting)) {
                storage.put(setting);
            }
        }
        fileStorage = storage;
    }

    /**
     * Returns the currently active storage (file-based if enabled, otherwise in-memory).
     *
     * @return the active settings storage
     */
    public synchronized @NonNull SettingsStorage getActiveStorage() {
        SettingsStorage fs = fileStorage;
        return fs != null ? fs : memory;
    }

    @Override
    public <T extends Setting> @NonNull Optional<@NonNull T> get(@NonNull Class<@NonNull T> clazz) {
        return getActiveStorage().get(clazz);
    }

    @Override
    public void put(@NonNull Setting setting) {
        getActiveStorage().put(setting);
    }

    @Override
    public void remove(@NonNull Class<? extends @NonNull Setting> clazz) {
        getActiveStorage().remove(clazz);
    }

    @Override
    public @NonNull Iterator<@NonNull Setting> iterator() {
        return getActiveStorage().iterator();
    }
}

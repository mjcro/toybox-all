package io.github.mjcro.toybox.app.settings.storage;

import io.github.mjcro.toybox.api.Setting;
import io.github.mjcro.toybox.api.SettingsStorage;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;

/**
 * Base class for file-backed settings storage implementations.
 * Subclasses provide the file reading and writing logic.
 */
public abstract class AbstractFileSettingsStorage implements SettingsStorage {
    /**
     * The file used for persistent storage.
     */
    protected final @NonNull File file;

    /**
     * Constructs a file-based settings storage for the given file.
     *
     * @param file the settings file
     */
    public AbstractFileSettingsStorage(@NonNull File file) {
        this.file = Objects.requireNonNull(file);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Setting> @NonNull Optional<@NonNull T> get(@NonNull Class<@NonNull T> clazz) {
        return Arrays.stream(readFile())
                .filter(s -> Objects.equals(s.getClass(), clazz))
                .map(s -> (T) s)
                .findAny();
    }

    @Override
    public void put(@Nullable Setting setting) {
        if (setting == null) {
            return;
        }

        ArrayList<Setting> settingList = new ArrayList<>();
        for (Setting s : readFile()) {
            if (!Objects.equals(s.getClass(), setting.getClass())) {
                settingList.add(s);
            }
        }
        settingList.add(setting);

        writeFile(settingList.toArray(new Setting[0]));
    }

    @Override
    public void remove(@NonNull Class<? extends @NonNull Setting> clazz) {
        Setting[] settings = Arrays.stream(readFile())
                .filter(s -> !Objects.equals(s.getClass(), clazz))
                .toArray(Setting[]::new);

        writeFile(settings);
    }

    @Override
    public @NonNull Iterator<@NonNull Setting> iterator() {
        return Arrays.asList(readFile()).iterator();
    }

    /**
     * Reads all settings from the backing file.
     *
     * @return an array of settings, possibly empty
     */
    protected abstract @NonNull Setting[] readFile();

    /**
     * Writes all settings to the backing file.
     *
     * @param settings the settings to persist
     */
    protected abstract void writeFile(@NonNull Setting[] settings);
}

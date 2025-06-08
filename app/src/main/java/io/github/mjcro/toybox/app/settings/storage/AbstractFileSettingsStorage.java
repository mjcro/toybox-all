package io.github.mjcro.toybox.app.settings.storage;

import io.github.mjcro.toybox.api.Setting;
import io.github.mjcro.toybox.api.SettingsStorage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractFileSettingsStorage implements SettingsStorage {
    protected final File file;

    public AbstractFileSettingsStorage(File file) {
        this.file = Objects.requireNonNull(file);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Setting> Optional<T> get(Class<T> clazz) {
        return Arrays.stream(readFile())
                .filter(s -> Objects.equals(s.getClass(), clazz))
                .map(s -> (T) s)
                .findAny();
    }

    @Override
    public void put(Setting setting) {
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
    public void remove(Class<? extends Setting> clazz) {
        Setting[] settings = Arrays.stream(readFile())
                .filter(s -> !Objects.equals(s.getClass(), clazz))
                .toArray(Setting[]::new);

        writeFile(settings);
    }

    @Override
    public Iterator<Setting> iterator() {
        return Arrays.asList(readFile()).iterator();
    }

    protected abstract Setting[] readFile();

    protected abstract void writeFile(Setting[] settings);
}

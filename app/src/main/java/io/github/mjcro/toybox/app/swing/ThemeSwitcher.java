package io.github.mjcro.toybox.app.swing;

import io.github.mjcro.toybox.api.SettingsStorage;
import io.github.mjcro.toybox.app.settings.ToyBoxThemeSetting;
import io.github.mjcro.toybox.swing.prefab.ToyBoxLaF;
import io.github.mjcro.toybox.swing.util.Slf4jUtil;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.util.Objects;

/**
 * Switches the application theme and keeps it in {@link ToyBoxThemeSetting}.
 * All methods MUST be called on the Event Dispatch Thread.
 */
public class ThemeSwitcher {
    private static final @NonNull Logger log = LoggerFactory.getLogger(ThemeSwitcher.class);

    private final @NonNull SettingsStorage settingsStorage;

    /**
     * Constructs the theme switcher.
     *
     * @param settingsStorage storage the selected theme is saved to and restored from
     */
    public ThemeSwitcher(@NonNull SettingsStorage settingsStorage) {
        this.settingsStorage = Objects.requireNonNull(settingsStorage, "settingsStorage");
    }

    /**
     * Applies the theme and saves it to settings; does nothing if it is already active.
     *
     * @param className Look and Feel class name.
     */
    public void select(@NonNull String className) {
        if (isActive(className)) {
            return;
        }
        if (apply(className)) {
            settingsStorage.put(new ToyBoxThemeSetting(className));
        }
    }

    /**
     * Applies the theme saved in settings; does nothing if none is saved,
     * it is already active or not available on the classpath.
     */
    public void restore() {
        settingsStorage.get(ToyBoxThemeSetting.class)
                .map(ToyBoxThemeSetting::getValue)
                .filter(className -> !isActive(className))
                .filter(ThemeSwitcher::isAvailable)
                .ifPresent(ThemeSwitcher::apply);
    }

    private static boolean isActive(@NonNull String className) {
        return className.equals(UIManager.getLookAndFeel().getClass().getName());
    }

    private static boolean isAvailable(@NonNull String className) {
        boolean available = ToyBoxLaF.available().stream().anyMatch(info -> className.equals(info.getClassName()));
        if (!available) {
            log.warn(Slf4jUtil.TOYBOX_MARKER, "Saved theme {} not found, keeping current one", className);
        }
        return available;
    }

    /**
     * Switches the Look and Feel, logging any failure.
     *
     * @param className Look and Feel class name.
     * @return true on success.
     */
    private static boolean apply(@NonNull String className) {
        try {
            ToyBoxLaF.change(className);
            log.info(Slf4jUtil.TOYBOX_MARKER, "Theme changed to {}", className);
            return true;
        } catch (ReflectiveOperationException | UnsupportedLookAndFeelException | RuntimeException e) {
            log.error(Slf4jUtil.TOYBOX_MARKER, "Unable to change theme to {}", className, e);
            return false;
        }
    }
}

package io.github.mjcro.toybox.swing.prefab;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.Window;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

/**
 * Static utility for Java Look and Feel initialization.
 */
public class ToyBoxLaF {
    private static final @NonNull Logger log = LoggerFactory.getLogger(ToyBoxLaF.class);

    /**
     * Initializes standard ToyBox Look and Feel (LaF).
     *
     * @param darkMode True if dark mode should be used, false otherwise.
     */
    public static void initialize(boolean darkMode) {
        // Register bundled fonts before FlatLaf reads its theme properties,
        // otherwise @fontFamily references in the .properties files will not resolve.
        EmbeddedFonts.register();

        // Installing look and feel
        FlatLightLaf.registerCustomDefaultsSource("toybox.laf.themes");
        if (darkMode) {
            FlatLaf.setup(new FlatDarkLaf());
        } else {
            FlatLaf.setup(new FlatLightLaf());
        }
        UIManager.put("TabbedPane.tabLayoutPolicy", "scroll");
    }

    /**
     * Discovers Look and Feels available on the classpath: bundled FlatLaf themes,
     * {@link UIManager#getInstalledLookAndFeels()} and supported
     * {@link LookAndFeel} providers registered via {@link ServiceLoader}.
     *
     * @return Unmodifiable list sorted by name, without duplicate class names.
     */
    public static @NonNull List<UIManager.@NonNull LookAndFeelInfo> available() {
        Map<String, UIManager.LookAndFeelInfo> found = new LinkedHashMap<>();
        for (LookAndFeel laf : List.of(
                new FlatLightLaf(),
                new FlatDarkLaf(),
                new FlatIntelliJLaf(),
                new FlatDarculaLaf(),
                new FlatMacLightLaf(),
                new FlatMacDarkLaf()
        )) {
            found.putIfAbsent(laf.getClass().getName(), new UIManager.LookAndFeelInfo(laf.getName(), laf.getClass().getName()));
        }
        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            found.putIfAbsent(info.getClassName(), info);
        }
        Iterator<LookAndFeel> providers = ServiceLoader.load(LookAndFeel.class).iterator();
        while (true) {
            try {
                if (!providers.hasNext()) {
                    break;
                }
                LookAndFeel laf = providers.next();
                if (laf.isSupportedLookAndFeel()) {
                    found.putIfAbsent(laf.getClass().getName(), new UIManager.LookAndFeelInfo(laf.getName(), laf.getClass().getName()));
                }
            } catch (ServiceConfigurationError e) {
                log.warn("Unable to load Look and Feel provider", e);
            }
        }

        List<UIManager.LookAndFeelInfo> result = new ArrayList<>(found.values());
        result.sort(Comparator.comparing(UIManager.LookAndFeelInfo::getName, String.CASE_INSENSITIVE_ORDER));
        return List.copyOf(result);
    }

    /**
     * Switches the current Look and Feel and refreshes all open windows.
     * MUST be called on the Event Dispatch Thread.
     *
     * @param className Look and Feel class name.
     * @throws ReflectiveOperationException    if the class cannot be loaded or instantiated.
     * @throws UnsupportedLookAndFeelException if the Look and Feel is not supported on this platform.
     */
    public static void change(@NonNull String className) throws ReflectiveOperationException, UnsupportedLookAndFeelException {
        Objects.requireNonNull(className, "className");
        UIManager.setLookAndFeel(className);
        for (Window window : Window.getWindows()) {
            SwingUtilities.updateComponentTreeUI(window);
            window.repaint();
        }
    }

    private ToyBoxLaF() {
    }
}

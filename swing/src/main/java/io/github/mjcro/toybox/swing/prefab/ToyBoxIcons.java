package io.github.mjcro.toybox.swing.prefab;

import io.github.mjcro.toybox.swing.util.Slf4jUtil;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.UIManager;
import java.awt.Image;
import java.awt.Taskbar;
import java.net.URL;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utility class for loading and caching Swing icons.
 *
 * <p>Icons are resolved from UIManager defaults, classpath resources
 * (famfamfam-silk webjar), and custom asset folders. Loaded icons
 * are cached by name and requested dimensions.
 */
public class ToyBoxIcons {
    private static final @NonNull Logger log = LoggerFactory.getLogger(ToyBoxIcons.class);
    private static final @NonNull ConcurrentHashMap<@NonNull Key, @NonNull Icon> icons = new ConcurrentHashMap<>();

    /** Whether dark mode is active, affecting icon resolution. */
    public static boolean DARK_MODE = false;

    /**
     * Sets main application image with Taskbar (if supported).
     *
     * @param frame    main application window frame
     * @param iconName icon name
     */
    public static void setMainApplicationIcon(@NonNull JFrame frame, @NonNull String iconName) {
        Optional<ImageIcon> optional = getImage(iconName);
        if (optional.isEmpty()) {
            return;
        }

        Image imageIcon = optional.get().getImage();
        frame.setIconImage(imageIcon);
        try {
            Taskbar.getTaskbar().setIconImage(imageIcon);
        } catch (UnsupportedOperationException e) {
            if (log.isWarnEnabled(Slf4jUtil.TOYBOX_MARKER)) {
                log.warn(Slf4jUtil.TOYBOX_MARKER, "The OS does not support: 'taskbar.setIconImage'");
            }
        } catch (SecurityException e) {
            if (log.isErrorEnabled(Slf4jUtil.TOYBOX_MARKER)) {
                log.error(Slf4jUtil.TOYBOX_MARKER, "Security exception setting taskbar icon", e);
            }
        }
    }

    /**
     * Loads and returns icon associated with given name.
     *
     * @param name icon name
     * @return the icon
     * @throws NoSuchElementException if icon cannot be found
     */
    public static @NonNull Icon mustGet(@NonNull String name) {
        return get(name).orElseThrow();
    }

    /**
     * Loads and returns image icon.
     *
     * @param name icon name
     * @return image icon, if found
     */
    public static @NonNull Optional<@NonNull ImageIcon> getImage(@NonNull String name) {
        return get(name).filter($ -> $ instanceof ImageIcon).map($ -> (ImageIcon) $);
    }

    /**
     * Loads and returns icon.
     *
     * @param name icon name
     * @return icon, if found
     */
    public static @NonNull Optional<@NonNull Icon> get(@NonNull String name) {
        return get(new Key(name, 0, 0));
    }

    /**
     * Loads and returns 16x16 icon.
     *
     * @param name icon name
     * @return icon, if found
     */
    public static @NonNull Optional<@NonNull Icon> getSmall(@NonNull String name) {
        return get(new Key(name, 16, 16));
    }

    /**
     * Loads and returns icon for given criteria.
     *
     * @param key icon criteria
     * @return icon, if found
     */
    private static @NonNull Optional<@NonNull Icon> get(@NonNull Key key) {
        try {
            return Optional.ofNullable(icons.computeIfAbsent(key, $ -> {
                boolean hasSchemaPrefix = $.name.indexOf("://") > 0;
                boolean hasExtension = $.name.endsWith(".png");

                // Resolving using UIManager
                if (!hasSchemaPrefix) {
                    Icon uiIcon = UIManager.getIcon($.name);
                    if (uiIcon != null) {
                        return uiIcon;
                    }
                }

                String name = $.name;

                ArrayList<String> resourcesFolders = new ArrayList<>();
                if (hasSchemaPrefix && $.name.startsWith("fam://")) {
                    name = name.substring(6);
                    resourcesFolders.add("/META-INF/resources/webjars/famfamfam-silk/1.3/icons/");
                } else {
                    resourcesFolders.add("/META-INF/resources/webjars/famfamfam-silk/1.3/icons/");
                    resourcesFolders.add("/assets/icons/");
                }

                for (String folder : resourcesFolders) {
                    String resourceName = folder + name;
                    if (!hasExtension) {
                        resourceName += ".png";
                    }
                    URL r = ToyBoxIcons.class.getResource(resourceName);
                    if (r == null) {
                        continue;
                    }
                    ImageIcon icon = new ImageIcon(r);
                    if ($.width > 0 && icon.getIconWidth() > $.width) {
                        Image downscale = icon.getImage().getScaledInstance($.width, $.height, Image.SCALE_SMOOTH);
                        icon = new ImageIcon(downscale);
                    }
                    return icon;
                }

                return null;
            }));
        } catch (RuntimeException e) {
            // ignore
            if (log.isErrorEnabled(Slf4jUtil.TOYBOX_MARKER)) {
                log.error(Slf4jUtil.TOYBOX_MARKER, "Error reading icon {}", key.name, e);
            }
            return Optional.empty();
        }
    }

    /**
     * Cache key for icon lookup, combining name and requested dimensions.
     */
    private static final class Key {
        private final @NonNull String name;
        private final int width;
        private final int height;

        /**
         * Creates a new icon cache key.
         *
         * @param name   the icon name
         * @param width  the requested width, or 0 for original size
         * @param height the requested height, or 0 for original size
         */
        Key(@NonNull String name, int width, int height) {
            this.name = name;
            this.width = width;
            this.height = height;
        }

        /**
         * Returns the icon name.
         *
         * @return the icon name
         */
        public @NonNull String getName() {
            return name;
        }

        /**
         * Returns the requested width.
         *
         * @return the width
         */
        public int getWidth() {
            return width;
        }

        /**
         * Returns the requested height.
         *
         * @return the height
         */
        public int getHeight() {
            return height;
        }

        @Override
        public boolean equals(@Nullable Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Key key = (Key) o;
            return width == key.width
                    && height == key.height
                    && Objects.equals(name, key.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, width, height);
        }

        @Override
        public @NonNull String toString() {
            return "Key{name='" + name + "', width=" + width + ", height=" + height + "}";
        }
    }
}

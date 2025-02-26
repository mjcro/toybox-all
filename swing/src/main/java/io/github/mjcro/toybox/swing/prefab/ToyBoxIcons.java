package io.github.mjcro.toybox.swing.prefab;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ToyBoxIcons {
    private static final ConcurrentHashMap<Key, Icon> icons = new ConcurrentHashMap<>();
    public static boolean DARK_MODE = false;

    /**
     * Sets main application image with Taskbar (if supported)
     *
     * @param frame    Main application window frame.
     * @param iconName Icon name.
     */
    public static void setMainApplicationIcon(JFrame frame, String iconName) {
        Optional<ImageIcon> optional = getImage(iconName);
        if (optional.isEmpty()) {
            return;
        }

        Image imageIcon = optional.get().getImage();
        frame.setIconImage(imageIcon);
        try {
            Taskbar.getTaskbar().setIconImage(imageIcon);
        } catch (UnsupportedOperationException e) {
            log.warn("The OS does not support: 'taskbar.setIconImage'");
        } catch (SecurityException e) {
            log.error("Security exception setting taskbar icon", e);
        }
    }

    /**
     * Loads and returns icon associated with given name.
     *
     * @param name Icon name.
     * @return Icon.
     * @throws NoSuchElementException If icon cannot be found.
     */
    public static Icon mustGet(String name) {
        return get(name).orElseThrow();
    }

    /**
     * Loads and returns image icon.
     *
     * @param name Icon name.
     * @return Image icon, if found.
     */
    public static Optional<ImageIcon> getImage(String name) {
        return get(name).filter($ -> $ instanceof ImageIcon).map($ -> (ImageIcon) $);
    }

    /**
     * Loads and returns icon.
     *
     * @param name Icon name.
     * @return Icon, if found.
     */
    public static Optional<Icon> get(String name) {
        return get(new Key(name, 0, 0));
    }

    /**
     * Loads and returns 16x16 icon.
     *
     * @param name Icon name.
     * @return Icon, if found.
     */
    public static Optional<Icon> getSmall(String name) {
        return get(new Key(name, 16, 16));
    }

    /**
     * Loads and returns icon for given criteria.
     *
     * @param key Icon criteria.
     * @return Icon, if found.
     */
    private static Optional<Icon> get(Key key) {
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
            log.error("Error reading icon " + key.name, e);
            return Optional.empty();
        }
    }

    @Data
    private static final class Key {
        private final String name;
        private final int width;
        private final int height;
    }
}

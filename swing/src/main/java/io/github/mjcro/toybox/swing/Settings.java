package io.github.mjcro.toybox.swing;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.UIManager;
import javax.swing.border.Border;
import java.awt.Color;

/**
 * Singleton holding cached Swing UI manager settings such as
 * table selection colors and cell highlight borders.
 */
public class Settings {
    private static @NonNull Settings settings = new Settings();

    /**
     * Returns the current singleton instance.
     *
     * @return settings instance
     */
    public static @NonNull Settings getInstance() {
        return settings;
    }

    /**
     * Resets the singleton to a fresh instance, re-reading UIManager values.
     */
    public static void reset() {
        settings = new Settings();
    }

    private Settings() {
    }

    /** Table selection background color from the current look-and-feel. */
    public final @Nullable Color Table_selectionBackground = UIManager.getColor("Table.selectionBackground");
    /** Table selection foreground color from the current look-and-feel. */
    public final @Nullable Color Table_selectionForeground = UIManager.getColor("Table.selectionForeground");
    /** Table inactive selection foreground color from the current look-and-feel. */
    public final @Nullable Color Table_selectionInactiveForeground = UIManager.getColor("Table.selectionInactiveForeground");
    /** Table focused selected cell highlight border from the current look-and-feel. */
    public final @Nullable Border Table_focusSelectedCellHighlightBorder = UIManager.getBorder("Table.focusSelectedCellHighlightBorder");
    /** Table focused cell highlight border from the current look-and-feel. */
    public final @Nullable Border Table_focusCellHighlightBorder = UIManager.getBorder("Table.focusCellHighlightBorder");
}

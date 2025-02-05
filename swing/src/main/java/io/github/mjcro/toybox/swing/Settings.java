package io.github.mjcro.toybox.swing;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class Settings {
    private static Settings settings = new Settings();

    public static Settings getInstance() {
        return settings;
    }

    public static void reset() {
        settings = new Settings();
    }

    private Settings() {
    }

    public final Color Table_selectionBackground = UIManager.getColor("Table.selectionBackground");
    public final Color Table_selectionForeground = UIManager.getColor("Table.selectionForeground");
    public final Color Table_selectionInactiveForeground = UIManager.getColor("Table.selectionInactiveForeground");
    public final Border Table_focusSelectedCellHighlightBorder = UIManager.getBorder("Table.focusSelectedCellHighlightBorder");
    public final Border Table_focusCellHighlightBorder = UIManager.getBorder("Table.focusCellHighlightBorder");
}

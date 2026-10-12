package io.github.mjcro.toybox.eve.plaf;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;

import io.github.mjcro.toybox.eve.EvePalette;

import org.jspecify.annotations.Nullable;

/**
 * Shared helpers of the data component group (tables, trees, lists).
 */
final class DataTableSupport {
    private DataTableSupport() {
    }

    /**
     * Resolves the unselected row background, applying {@code "Table.alternateRowColor"}
     * to odd rows unless the application set its own table background.
     *
     * @param table table, not null.
     * @param row   view row index.
     * @return non-null background color.
     */
    static Color rowBackground(JTable table, int row) {
        final Color background = table.getBackground();
        if (row % 2 != 0 && background instanceof UIResource) {
            final @Nullable Color alternate = UIManager.getColor("Table.alternateRowColor");
            if (alternate != null) {
                return alternate;
            }
        }
        return background;
    }

    /**
     * Resolves the selection marker color for a component.
     *
     * @param c component painting the marker, not null.
     * @return {@link EvePalette#ACCENT} when focused, {@link EvePalette#ACCENT_DARK} when unfocused,
     *         {@link EvePalette#BORDER} when disabled; never null.
     */
    static Color markerColor(Component c) {
        if (!c.isEnabled()) {
            return EvePalette.BORDER;
        }
        return c.isFocusOwner() ? EvePalette.ACCENT : EvePalette.ACCENT_DARK;
    }
}

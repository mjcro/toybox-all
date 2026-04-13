package io.github.mjcro.toybox.swing.renderers;

import io.github.mjcro.toybox.swing.prefab.ToyBoxLabels;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;
import java.awt.Color;
import java.awt.Component;

/**
 * Base class for custom table cell renderers providing standard color handling
 * for normal and selected states.
 */
public abstract class AbstractTableCellRenderer implements TableCellRenderer {
    protected @Nullable Color normalFg;
    protected @Nullable Color normalBg;
    protected @Nullable Color selectedFg;
    protected @Nullable Color selectedBg;

    /**
     * Initializes the renderer with default table colors from the UIManager.
     */
    public AbstractTableCellRenderer() {
        normalFg = UIManager.getColor("Table.foreground");
        normalBg = UIManager.getColor("Table.background");

        selectedFg = UIManager.getColor("Table.selectionForeground");
        selectedBg = UIManager.getColor("Table.selectionBackground");
    }

    /**
     * Creates a new opaque label with standard cell padding.
     *
     * @return A new label suitable for use as a cell renderer component.
     */
    protected @NonNull JLabel createLabel() {
        final JLabel label = ToyBoxLabels.create();
        label.setBorder(new EmptyBorder(3, 5, 3, 5));
        label.setOpaque(true);
        return label;
    }

    /**
     * Applies selection or normal foreground/background colors to the given component.
     *
     * @param component  The component to style.
     * @param isSelected Whether the cell is selected.
     * @param hasFocus   Whether the cell has focus.
     * @return The styled component.
     */
    protected @NonNull Component withSelection(@NonNull Component component, boolean isSelected, boolean hasFocus) {
        if (isSelected) {
            component.setForeground(selectedFg);
            component.setBackground(selectedBg);
        } else {
            component.setForeground(normalFg);
            component.setBackground(normalBg);
        }
        return component;
    }
}

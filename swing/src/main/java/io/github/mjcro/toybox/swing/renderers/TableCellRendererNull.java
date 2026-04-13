package io.github.mjcro.toybox.swing.renderers;

import io.github.mjcro.toybox.swing.hint.Hints;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import java.awt.Component;

/**
 * Table cell renderer that displays a centered "null" label for null cell values.
 * Hides the text when the row is selected.
 */
public class TableCellRendererNull extends AbstractTableCellRendererLabel {
    /**
     * Creates a new null cell renderer with centered, smaller-font, inactive-colored styling.
     */
    public TableCellRendererNull() {
        super();

        normalFg = UIManager.getColor("TextField.inactiveForeground");
        this.label.setHorizontalAlignment(SwingConstants.CENTER);
        Hints.FONT_SMALLER_2.apply(label);
    }

    @Override
    public @NonNull Component getTableCellRendererComponent(
            @NonNull JTable table,
            @Nullable Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column
    ) {
        label.setText(isSelected ? null : "null");
        return withSelection(label, isSelected, hasFocus);
    }
}

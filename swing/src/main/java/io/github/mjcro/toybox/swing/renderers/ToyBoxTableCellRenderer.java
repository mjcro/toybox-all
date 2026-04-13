package io.github.mjcro.toybox.swing.renderers;

import io.github.mjcro.toybox.swing.hint.WithHint;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;
import java.time.Instant;

/**
 * Composite table cell renderer that delegates to specialized renderers
 * based on the runtime type of the cell value.
 */
public class ToyBoxTableCellRenderer implements TableCellRenderer {
    private final @NonNull TableCellRendererNull nilRenderer = new TableCellRendererNull();
    private final @NonNull TableCellRendererString stringRenderer = new TableCellRendererString();
    private final @NonNull TableCellRendererLink linkRenderer = new TableCellRendererLink();
    private final @NonNull TableCellRendererNumber numberRenderer = new TableCellRendererNumber();
    private final @NonNull TableCellRendererInstant instantRenderer = new TableCellRendererInstant();
    private final @NonNull TableCellRendererWithHint hintLabelRenderer = new TableCellRendererWithHint();

    @Override
    public @NonNull Component getTableCellRendererComponent(
            @NonNull JTable table,
            @Nullable Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column
    ) {
        if (value instanceof CharSequence) {
            if (value instanceof String) {
                final String s = (String) value;
                if (s.startsWith("http://") || s.startsWith("https://")) {
                    return linkRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                }
            }
            return stringRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        } else if (value instanceof Number) {
            return numberRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        } else if (value instanceof Instant) {
            return instantRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        } else if (value instanceof WithHint<?>) {
            return hintLabelRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }

        return nilRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
}

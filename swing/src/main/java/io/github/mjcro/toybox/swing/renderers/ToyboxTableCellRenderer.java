package io.github.mjcro.toybox.swing.renderers;

import io.github.mjcro.toybox.swing.WithHint;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.time.Instant;

public class ToyboxTableCellRenderer implements TableCellRenderer {
    private final TableCellRendererNull nilRenderer = new TableCellRendererNull();
    private final TableCellRendererString stringRenderer = new TableCellRendererString();
    private final TableCellRendererLink linkRenderer = new TableCellRendererLink();
    private final TableCellRendererNumber numberRenderer = new TableCellRendererNumber();
    private final TableCellRendererInstant instantRenderer = new TableCellRendererInstant();
    private final TableCellRendererWithHint hintLabelRenderer = new TableCellRendererWithHint();

    @Override
    public Component getTableCellRendererComponent(
            JTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column
    ) {
        if (value instanceof CharSequence) {
            if (value instanceof String) {
                String s = (String) value;
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

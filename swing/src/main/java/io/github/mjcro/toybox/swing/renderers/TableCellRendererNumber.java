package io.github.mjcro.toybox.swing.renderers;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

public class TableCellRendererNumber extends AbstractTableCellRendererLabel {
    private final String format;

    public TableCellRendererNumber(String format) {
        super();
        this.format = format;
        this.label.setHorizontalAlignment(SwingConstants.RIGHT);
    }

    public TableCellRendererNumber() {
        this(null);
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column
    ) {
        label.setText(null);

        if (value instanceof Number) {
            String s;
            if (format == null) {
                s = value.toString();
            } else {
                s = String.format(Locale.ROOT, format, value);
            }
            label.setText(s);
        }

        return withSelection(label, isSelected, hasFocus);
    }
}
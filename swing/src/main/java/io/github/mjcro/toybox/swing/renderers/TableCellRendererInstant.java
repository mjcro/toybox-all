package io.github.mjcro.toybox.swing.renderers;

import io.github.mjcro.toybox.swing.Styles;

import javax.swing.*;
import java.awt.*;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public class TableCellRendererInstant extends AbstractTableCellRendererLabel {
    public static final DateTimeFormatter INSTANT_CELL_VALUE_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
            .withZone(ZoneOffset.UTC);

    public TableCellRendererInstant() {
        super();
        normalFg = UIManager.getColor("TextField.inactiveForeground");

        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        Styles.FONT_SMALLER_1.apply(label);
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

        if (value instanceof Instant) {
            label.setText(INSTANT_CELL_VALUE_FMT.format((TemporalAccessor) value));
        }

        return withSelection(label, isSelected, hasFocus);
    }
}

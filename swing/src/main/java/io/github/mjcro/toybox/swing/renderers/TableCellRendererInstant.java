package io.github.mjcro.toybox.swing.renderers;

import io.github.mjcro.toybox.swing.hint.Hints;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import java.awt.Component;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

/**
 * Table cell renderer that formats {@link Instant} values using a fixed UTC date-time pattern.
 */
public class TableCellRendererInstant extends AbstractTableCellRendererLabel {
    /**
     * Date-time formatter used for rendering instant values in table cells.
     */
    public static final @NonNull DateTimeFormatter INSTANT_CELL_VALUE_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
            .withZone(ZoneOffset.UTC);

    /**
     * Creates a new instant cell renderer with centered, smaller-font styling.
     */
    public TableCellRendererInstant() {
        super();
        normalFg = UIManager.getColor("TextField.inactiveForeground");

        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        Hints.FONT_SMALLER_1.apply(label);
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
        label.setText(null);

        if (value instanceof Instant) {
            label.setText(INSTANT_CELL_VALUE_FMT.format((TemporalAccessor) value));
        }

        return withSelection(label, isSelected, hasFocus);
    }
}

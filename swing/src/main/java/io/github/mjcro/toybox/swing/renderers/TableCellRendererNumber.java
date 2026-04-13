package io.github.mjcro.toybox.swing.renderers;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import java.awt.Component;
import java.util.Locale;

/**
 * Table cell renderer that displays {@link Number} values right-aligned with optional formatting.
 */
public class TableCellRendererNumber extends AbstractTableCellRendererLabel {
    private final @Nullable String format;

    /**
     * Creates a new number cell renderer with the given format string.
     *
     * @param format a {@link String#format}-compatible pattern, or {@code null} to use {@code toString()}
     */
    public TableCellRendererNumber(@Nullable String format) {
        super();
        this.format = format;
        this.label.setHorizontalAlignment(SwingConstants.RIGHT);
    }

    /**
     * Creates a new number cell renderer with no format (uses {@code toString()}).
     */
    public TableCellRendererNumber() {
        this(null);
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

        if (value instanceof Number) {
            final String s;
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

package io.github.mjcro.toybox.swing.renderers;

import io.github.mjcro.interfaces.strings.WithName;
import io.github.mjcro.toybox.api.Labeled;
import io.github.mjcro.toybox.swing.hint.Hint;
import io.github.mjcro.toybox.swing.hint.Hints;
import io.github.mjcro.toybox.swing.prefab.ToyBoxIcons;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.JLabel;
import javax.swing.JTable;
import java.awt.Component;

/**
 * Table cell renderer that displays string values with optional hint-based styling.
 * Supports {@link CharSequence}, {@link Labeled}, and {@link WithName} value types.
 */
public class TableCellRendererString extends AbstractTableCellRendererLabel {
    /**
     * Creates a bold-styled string renderer.
     *
     * @return a new bold string renderer
     */
    public static @NonNull TableCellRendererString bold() {
        return new TableCellRendererString(Hints.TEXT_SEMIBOLD);
    }

    /**
     * Creates a monospaced, slightly smaller string renderer.
     *
     * @return a new monospaced string renderer
     */
    public static @NonNull TableCellRendererString monospaced() {
        return new TableCellRendererString(Hints.TEXT_MONOSPACED, Hints.FONT_SMALLER_1);
    }

    /**
     * Creates a new string cell renderer with the given display hints.
     *
     * @param hints zero or more hints to apply to the label
     */
    @SafeVarargs
    public TableCellRendererString(@NonNull Hint<? super JLabel> @NonNull ... hints) {
        super();
        applyHints(hints);
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
        label.setIcon(null);
        label.setText(null);

        if (value instanceof CharSequence) {
            label.setText(value.toString());
        } else if (value instanceof Labeled) {
            final Labeled labeled = (Labeled) value;
            label.setText(labeled.getName());
            labeled.getLabel().getIconURI().flatMap(ToyBoxIcons::get).ifPresent(label::setIcon);
        } else if (value instanceof WithName) {
            label.setText(value.toString());
        }

        return withSelection(label, isSelected, hasFocus);
    }
}

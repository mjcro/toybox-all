package io.github.mjcro.toybox.swing.renderers;

import io.github.mjcro.interfaces.strings.WithName;
import io.github.mjcro.toybox.api.Labeled;
import io.github.mjcro.toybox.swing.hint.Hint;
import io.github.mjcro.toybox.swing.prefab.ToyBoxIcons;
import io.github.mjcro.toybox.swing.hint.Hints;

import javax.swing.*;
import java.awt.*;

public class TableCellRendererString extends AbstractTableCellRendererLabel {
    public static TableCellRendererString bold() {
        return new TableCellRendererString(Hints.TEXT_SEMIBOLD);
    }

    public static TableCellRendererString monospaced() {
        return new TableCellRendererString(Hints.TEXT_MONOSPACED, Hints.FONT_SMALLER_1);
    }

    @SafeVarargs
    public TableCellRendererString(Hint<? super JLabel>... hints) {
        super();
        applyHints(hints);
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
        label.setIcon(null);
        label.setText(null);

        if (value instanceof CharSequence) {
            label.setText(value.toString());
        } else if (value instanceof Labeled) {
            Labeled labeled = (Labeled) value;
            label.setText(labeled.getName());
            labeled.getLabel().getIconURI().flatMap(ToyBoxIcons::get).ifPresent(label::setIcon);
        } else if (value instanceof WithName) {
            label.setText(value.toString());
        }

        return withSelection(label, isSelected, hasFocus);
    }
}

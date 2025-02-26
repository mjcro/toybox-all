package io.github.mjcro.toybox.swing.renderers;

import io.github.mjcro.interfaces.strings.WithName;
import io.github.mjcro.interfaces.strings.WithText;
import io.github.mjcro.toybox.swing.hint.WithHint;

import javax.swing.*;
import java.awt.*;

public class TableCellRendererWithHint extends AbstractTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(
            JTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column
    ) {
        JLabel label = createLabel();

        if (!isSelected) {
            withSelection(label, isSelected, hasFocus);
        }

        if (value instanceof WithHint<?>) {
            WithHint<JLabel> withHint = (WithHint<JLabel>) value;
            String text = null;
            if (value instanceof WithText) {
                text = ((WithText) value).getText();
            } else if (value instanceof WithName) {
                text = ((WithName) value).getName();
            } else {
                text = value.toString();
            }

            label.setText(text);
            withHint.getHint().apply(label);
        }

        if (isSelected) {
            withSelection(label, isSelected, hasFocus);
        }

        return label;
    }
}

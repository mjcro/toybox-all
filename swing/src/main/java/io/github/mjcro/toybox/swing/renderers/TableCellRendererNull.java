package io.github.mjcro.toybox.swing.renderers;

import io.github.mjcro.toybox.swing.hint.Hints;

import javax.swing.*;
import java.awt.*;

public class TableCellRendererNull extends AbstractTableCellRendererLabel {
    public TableCellRendererNull() {
        super();

        normalFg = UIManager.getColor("TextField.inactiveForeground");
        this.label.setHorizontalAlignment(SwingConstants.CENTER);
        Hints.FONT_SMALLER_2.apply(label);
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
        label.setText(isSelected ? null : "null");
        return withSelection(label, isSelected, hasFocus);
    }
}

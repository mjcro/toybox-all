package io.github.mjcro.toybox.swing.renderers;

import io.github.mjcro.interfaces.strings.WithUrl;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.Map;

public class TableCellRendererLink extends AbstractTableCellRendererLabel {
    public TableCellRendererLink() {
        super();
        normalFg = UIManager.getColor("Component.linkColor");
        Font font = label.getFont();
        Map attributes = font.getAttributes();
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_DOTTED);
        label.setFont(font.deriveFont(attributes));
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

        if (value instanceof CharSequence) {
            label.setText(value.toString());
        } else if (value instanceof WithUrl) {
            WithUrl labeled = (WithUrl) value;
            label.setText(labeled.getURL());
        } else {
            label.setText(value.toString());
        }

        return withSelection(label, isSelected, hasFocus);
    }
}

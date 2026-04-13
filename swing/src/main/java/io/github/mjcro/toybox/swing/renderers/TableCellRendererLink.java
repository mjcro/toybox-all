package io.github.mjcro.toybox.swing.renderers;

import io.github.mjcro.interfaces.strings.WithUrl;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.JTable;
import javax.swing.UIManager;
import java.awt.Component;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.Map;

/**
 * Table cell renderer that displays values as underlined hyperlink-style text.
 * Supports {@link CharSequence} and {@link WithUrl} value types.
 */
public class TableCellRendererLink extends AbstractTableCellRendererLabel {
    /**
     * Creates a new link cell renderer with dotted-underline and link-colored styling.
     */
    @SuppressWarnings("unchecked")
    public TableCellRendererLink() {
        super();
        normalFg = UIManager.getColor("Component.linkColor");
        final Font font = label.getFont();
        @SuppressWarnings("rawtypes") final Map attributes = font.getAttributes();
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_DOTTED);
        label.setFont(font.deriveFont(attributes));
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

        if (value instanceof CharSequence) {
            label.setText(value.toString());
        } else if (value instanceof WithUrl) {
            final WithUrl labeled = (WithUrl) value;
            label.setText(labeled.getURL());
        } else if (value != null) {
            label.setText(value.toString());
        }

        return withSelection(label, isSelected, hasFocus);
    }
}

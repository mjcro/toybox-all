package io.github.mjcro.toybox.swing.renderers;

import io.github.mjcro.interfaces.strings.WithName;
import io.github.mjcro.interfaces.strings.WithText;
import io.github.mjcro.toybox.swing.hint.Hint;
import io.github.mjcro.toybox.swing.hint.WithHint;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.JLabel;
import javax.swing.JTable;
import java.awt.Component;

/**
 * Table cell renderer that applies a {@link WithHint} decoration to labels,
 * extracting text from {@link WithText} or {@link WithName} interfaces.
 */
public class TableCellRendererWithHint extends AbstractTableCellRenderer {

    @SuppressWarnings("unchecked")
    @Override
    public @NonNull Component getTableCellRendererComponent(
            @NonNull JTable table,
            @Nullable Object value,
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
            final Hint<? super JLabel> hint = withHint.getHint();
            if (hint != null) {
                hint.apply(label);
            }
        }

        if (isSelected) {
            withSelection(label, isSelected, hasFocus);
        }

        return label;
    }
}

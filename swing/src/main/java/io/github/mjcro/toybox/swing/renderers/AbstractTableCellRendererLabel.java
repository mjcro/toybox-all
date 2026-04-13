package io.github.mjcro.toybox.swing.renderers;

import io.github.mjcro.toybox.swing.hint.Hint;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.JLabel;
import javax.swing.JTable;
import java.awt.Component;

/**
 * Table cell renderer base that uses a single shared {@link JLabel} for rendering.
 */
public abstract class AbstractTableCellRendererLabel extends AbstractTableCellRenderer {
    protected @NonNull JLabel label;

    /**
     * Creates the renderer and initializes the shared label.
     */
    public AbstractTableCellRendererLabel() {
        super();
        label = createLabel();
    }

    /**
     * Applies the given hints to the shared label.
     *
     * @param hints Hints to apply, may be null or empty.
     */
    protected void applyHints(@Nullable Hint<? super JLabel>[] hints) {
        if (hints != null && hints.length > 0) {
            for (final Hint<? super JLabel> hint : hints) {
                hint.apply(label);
            }
        }
    }

    /**
     * Adjusts the table row height if the component's preferred height exceeds the current row height.
     *
     * @param component The rendered component.
     * @param table     The table being rendered.
     * @param row       The row index.
     */
    public void resizeHeight(@NonNull Component component, @NonNull JTable table, int row) {
        final int currentHeight = table.getRowHeight(row);
        final int componentHeight = component.getPreferredSize().height;
        if (componentHeight > currentHeight && currentHeight > 0) {
            table.setRowHeight(row, componentHeight);
        }
    }
}

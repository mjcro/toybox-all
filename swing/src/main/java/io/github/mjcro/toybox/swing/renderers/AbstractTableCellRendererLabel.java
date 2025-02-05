package io.github.mjcro.toybox.swing.renderers;

import io.github.mjcro.toybox.swing.Hint;

import javax.swing.*;
import java.awt.*;

public abstract class AbstractTableCellRendererLabel extends AbstractTableCellRenderer {
    protected JLabel label;

    public AbstractTableCellRendererLabel() {
        super();
        label = createLabel();
    }

    protected void applyHints(Hint<? super JLabel>[] hints) {
        if (hints != null && hints.length > 0) {
            for (Hint<? super JLabel> hint : hints) {
                hint.apply(label);
            }
        }
    }

    public void resizeHeight(Component component, JTable table, int row) {
        int currentHeight = table.getRowHeight(row);
        int componentHeight = component.getPreferredSize().height;
        if (componentHeight > currentHeight && currentHeight > 0) {
            table.setRowHeight(row, componentHeight);
        }
    }
}

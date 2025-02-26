package io.github.mjcro.toybox.swing.renderers;

import io.github.mjcro.toybox.swing.prefab.ToyBoxLabels;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public abstract class AbstractTableCellRenderer implements TableCellRenderer {
    protected Color normalFg, normalBg, selectedFg, selectedBg;

    public AbstractTableCellRenderer() {
        normalFg = UIManager.getColor("Table.foreground");
        normalBg = UIManager.getColor("Table.background");

        selectedFg = UIManager.getColor("Table.selectionForeground");
        selectedBg = UIManager.getColor("Table.selectionBackground");
    }

    protected JLabel createLabel() {
        JLabel label = ToyBoxLabels.create();
        label.setBorder(new EmptyBorder(3, 5, 3, 5));
        label.setOpaque(true);
        return label;
    }

    protected Component withSelection(Component component, boolean isSelected, boolean hasFocus) {
        if (component != null) {
            if (isSelected) {
                component.setForeground(selectedFg);
                component.setBackground(selectedBg);
            } else {
                component.setForeground(normalFg);
                component.setBackground(normalBg);
            }
        }
        return component;
    }
}

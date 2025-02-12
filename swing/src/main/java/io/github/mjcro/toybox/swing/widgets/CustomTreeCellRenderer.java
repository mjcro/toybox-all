package io.github.mjcro.toybox.swing.widgets;

import javax.swing.*;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

public abstract class CustomTreeCellRenderer implements TreeCellRenderer {
    protected final Color colorNormalFg, colorSelectedFg;

    public CustomTreeCellRenderer() {
        colorNormalFg = UIManager.getColor("Tree.foreground");
        colorSelectedFg = UIManager.getColor("Tree.selectionForeground");
    }
}

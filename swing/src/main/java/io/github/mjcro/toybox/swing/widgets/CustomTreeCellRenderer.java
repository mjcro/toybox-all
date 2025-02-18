package io.github.mjcro.toybox.swing.widgets;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class CustomTreeCellRenderer extends DefaultTreeCellRenderer {
    protected final Color colorNormalFg, colorSelectedFg;

    public CustomTreeCellRenderer() {
        colorNormalFg = UIManager.getColor("Tree.foreground");
        colorSelectedFg = UIManager.getColor("Tree.selectionForeground");
    }
}

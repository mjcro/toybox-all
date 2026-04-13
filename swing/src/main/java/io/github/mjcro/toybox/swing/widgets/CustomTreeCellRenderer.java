package io.github.mjcro.toybox.swing.widgets;

import org.jspecify.annotations.NonNull;

import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.Color;

/**
 * Base tree cell renderer that captures normal and selected foreground colors
 * from the current look-and-feel for use by subclasses.
 */
public class CustomTreeCellRenderer extends DefaultTreeCellRenderer {
    /** Foreground color for non-selected tree nodes. */
    protected final @NonNull Color colorNormalFg;
    /** Foreground color for selected tree nodes. */
    protected final @NonNull Color colorSelectedFg;

    /**
     * Creates a new renderer, resolving foreground colors from the current UI manager.
     */
    public CustomTreeCellRenderer() {
        colorNormalFg = UIManager.getColor("Tree.foreground");
        colorSelectedFg = UIManager.getColor("Tree.selectionForeground");
    }
}

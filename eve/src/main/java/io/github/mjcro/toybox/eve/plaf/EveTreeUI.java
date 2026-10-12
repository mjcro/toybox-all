package io.github.mjcro.toybox.eve.plaf;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.TreePath;

import io.github.mjcro.toybox.eve.EvePaint;
import io.github.mjcro.toybox.eve.EvePalette;

import org.jspecify.annotations.Nullable;

/**
 * Eve UI delegate for {@link javax.swing.JTree}.
 *
 * <p>Selected rows are highlighted across the full tree width with a 2px marker on
 * the leading edge (accent when focused), instead of only behind the node label.
 */
public class EveTreeUI extends BasicTreeUI {
    private final TreeSelectionListener rowRepainter = new TreeSelectionListener() {
        @Override
        public void valueChanged(TreeSelectionEvent e) {
            final TreePath[] paths = e.getPaths();
            for (TreePath path : paths) {
                repaintRow(path);
            }
        }
    };
    private final FocusListener focusRepainter = new FocusListener() {
        @Override
        public void focusGained(FocusEvent e) {
            repaintSelection();
        }

        @Override
        public void focusLost(FocusEvent e) {
            repaintSelection();
        }
    };

    /**
     * Creates the delegate; prefer {@link #createUI(javax.swing.JComponent)}.
     */
    public EveTreeUI() {
    }

    /**
     * Creates the UI delegate; called reflectively by {@link javax.swing.UIManager}.
     *
     * @param c component to create the delegate for, not null.
     * @return new non-null delegate.
     */
    public static ComponentUI createUI(JComponent c) {
        return new EveTreeUI();
    }

    @Override
    protected void installListeners() {
        super.installListeners();
        tree.addTreeSelectionListener(rowRepainter);
        tree.addFocusListener(focusRepainter);
    }

    @Override
    protected void uninstallListeners() {
        tree.removeFocusListener(focusRepainter);
        tree.removeTreeSelectionListener(rowRepainter);
        super.uninstallListeners();
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        paintSelectedRows(g);
        super.paint(g, c);
    }

    private void paintSelectedRows(Graphics g) {
        if (tree.getSelectionCount() == 0 || tree.getRowCount() == 0) {
            return;
        }
        final @Nullable Rectangle clip = g.getClipBounds();
        final Rectangle area = clip != null ? clip : new Rectangle(0, 0, tree.getWidth(), tree.getHeight());
        final int first = Math.max(0, tree.getClosestRowForLocation(0, area.y));
        final int last = tree.getClosestRowForLocation(0, area.y + area.height - 1);
        final Color fill = selectionFill();
        final Color marker = DataTableSupport.markerColor(tree);
        final int markerX = tree.getComponentOrientation().isLeftToRight() ? 0 : tree.getWidth() - 2;
        for (int row = first; row <= last; row++) {
            if (!tree.isRowSelected(row)) {
                continue;
            }
            final @Nullable Rectangle bounds = getRowBounds(row);
            if (bounds == null) {
                continue;
            }
            g.setColor(fill);
            g.fillRect(0, bounds.y, tree.getWidth(), bounds.height);
            EvePaint.drawMarker(g, marker, markerX, bounds.y, bounds.height);
        }
    }

    private Color selectionFill() {
        // Same color as DefaultTreeCellRenderer uses behind the label, so both blend seamlessly.
        final @Nullable Color color = UIManager.getColor("Tree.selectionBackground");
        return color != null ? color : EvePalette.SELECTION;
    }

    private @Nullable Rectangle getRowBounds(int row) {
        final @Nullable TreePath path = getPathForRow(tree, row);
        return path == null ? null : getPathBounds(tree, path);
    }

    private void repaintRow(@Nullable TreePath path) {
        if (path == null) {
            return;
        }
        final @Nullable Rectangle bounds = getPathBounds(tree, path);
        if (bounds != null) {
            tree.repaint(0, bounds.y, tree.getWidth(), bounds.height);
        }
    }

    private void repaintSelection() {
        final @Nullable TreePath[] paths = tree.getSelectionPaths();
        if (paths != null) {
            for (TreePath path : paths) {
                repaintRow(path);
            }
        }
    }
}

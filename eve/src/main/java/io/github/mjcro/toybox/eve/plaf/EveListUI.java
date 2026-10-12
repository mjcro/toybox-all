package io.github.mjcro.toybox.eve.plaf;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JComponent;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicListUI;

import io.github.mjcro.toybox.eve.EvePaint;

/**
 * Eve UI delegate for {@link javax.swing.JList}.
 *
 * <p>Adds a 2px marker on the leading edge of selected cells, accent when the list is focused.
 */
public class EveListUI extends BasicListUI {
    private final FocusListener markerRepainter = new FocusListener() {
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
    public EveListUI() {
    }

    /**
     * Creates the UI delegate; called reflectively by {@link javax.swing.UIManager}.
     *
     * @param c component to create the delegate for, not null.
     * @return new non-null delegate.
     */
    public static ComponentUI createUI(JComponent c) {
        return new EveListUI();
    }

    @Override
    protected void installListeners() {
        super.installListeners();
        list.addFocusListener(markerRepainter);
    }

    @Override
    protected void uninstallListeners() {
        list.removeFocusListener(markerRepainter);
        super.uninstallListeners();
    }

    @Override
    protected void paintCell(
            Graphics g,
            int row,
            Rectangle rowBounds,
            ListCellRenderer<Object> cellRenderer,
            ListModel<Object> dataModel,
            ListSelectionModel selModel,
            int leadIndex
    ) {
        super.paintCell(g, row, rowBounds, cellRenderer, dataModel, selModel, leadIndex);
        if (selModel.isSelectedIndex(row)) {
            final int x = list.getComponentOrientation().isLeftToRight()
                    ? rowBounds.x
                    : rowBounds.x + rowBounds.width - 2;
            EvePaint.drawMarker(g, DataTableSupport.markerColor(list), x, rowBounds.y, rowBounds.height);
        }
    }

    private void repaintSelection() {
        if (!list.isSelectionEmpty()) {
            list.repaint();
        }
    }
}

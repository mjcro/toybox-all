package io.github.mjcro.toybox.eve.plaf;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.table.TableCellRenderer;

import io.github.mjcro.toybox.eve.EvePalette;

import org.jspecify.annotations.Nullable;

/**
 * Eve UI delegate for {@link javax.swing.table.JTableHeader}.
 *
 * <p>Installs {@link DataHeaderRenderer} unless the application set its own renderer,
 * tracks the column under the mouse pointer for highlight, and continues the
 * header strip (background and bottom line) past the last column.
 */
public class EveTableHeaderUI extends BasicTableHeaderUI {
    private @Nullable TableCellRenderer originalRenderer;

    /**
     * Creates the delegate; prefer {@link #createUI(javax.swing.JComponent)}.
     */
    public EveTableHeaderUI() {
    }

    /**
     * Creates the UI delegate; called reflectively by {@link javax.swing.UIManager}.
     *
     * @param c component to create the delegate for, not null.
     * @return new non-null delegate.
     */
    public static ComponentUI createUI(JComponent c) {
        return new EveTableHeaderUI();
    }

    /**
     * Returns the index of the column under the mouse pointer.
     *
     * @return view column index, or {@code -1} when the pointer is outside the header.
     */
    public int rolloverColumn() {
        return getRolloverColumn();
    }

    @Override
    protected void installDefaults() {
        super.installDefaults();
        final @Nullable TableCellRenderer current = header.getDefaultRenderer();
        if (current instanceof UIResource && !(current instanceof DataHeaderRenderer)) {
            originalRenderer = current;
            header.setDefaultRenderer(new DataHeaderRenderer());
        }
    }

    @Override
    protected void uninstallDefaults() {
        final @Nullable TableCellRenderer original = originalRenderer;
        originalRenderer = null;
        if (original != null && header.getDefaultRenderer() instanceof DataHeaderRenderer) {
            header.setDefaultRenderer(original);
            if (original instanceof Component component) {
                SwingUtilities.updateComponentTreeUI(component);
            }
        }
        super.uninstallDefaults();
    }

    @Override
    protected void rolloverColumnUpdated(int oldColumn, int newColumn) {
        repaintColumn(oldColumn);
        repaintColumn(newColumn);
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        final @Nullable Rectangle clip = g.getClipBounds();
        final Rectangle area = clip != null ? clip : new Rectangle(0, 0, c.getWidth(), c.getHeight());
        g.setColor(c.getBackground());
        g.fillRect(area.x, area.y, area.width, area.height);
        g.setColor(EvePalette.BORDER_BRIGHT);
        g.fillRect(area.x, c.getHeight() - 1, area.width, 1);
        super.paint(g, c);
    }

    private void repaintColumn(int column) {
        if (column >= 0 && column < header.getColumnModel().getColumnCount()) {
            header.repaint(header.getHeaderRect(column));
        }
    }
}

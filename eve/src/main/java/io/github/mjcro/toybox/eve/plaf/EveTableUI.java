package io.github.mjcro.toybox.eve.plaf;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTableUI;
import javax.swing.table.TableCellRenderer;

import io.github.mjcro.toybox.eve.EvePaint;
import io.github.mjcro.toybox.eve.EvePalette;

import org.jspecify.annotations.Nullable;

/**
 * Eve UI delegate for {@link javax.swing.JTable}.
 *
 * <p>Adds to the basic behavior:
 * <ul>
 *     <li>Row height from {@code "Table.rowHeight"} unless set by the application.</li>
 *     <li>Grid defaults from {@code "Table.showHorizontalLines"} / {@code "Table.showVerticalLines"},
 *     applied only while the table keeps the JDK grid defaults, so application settings win.</li>
 *     <li>A 2px marker on the leading edge of selected rows, accent when focused.</li>
 *     <li>A Boolean renderer whose background follows selection ({@link DataBooleanRenderer}).</li>
 *     <li>A dimming veil over a disabled table.</li>
 * </ul>
 */
public class EveTableUI extends BasicTableUI {
    private static final String GRID_INSTALLED = "Eve.table.gridInstalled";
    private static final String ORIGINAL_BOOLEAN_RENDERER = "Eve.table.originalBooleanRenderer";
    private static final String JDK_BOOLEAN_RENDERER = "javax.swing.JTable$BooleanRenderer";
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
    public EveTableUI() {
    }

    /**
     * Creates the UI delegate; called reflectively by {@link javax.swing.UIManager}.
     *
     * @param c component to create the delegate for, not null.
     * @return new non-null delegate.
     */
    public static ComponentUI createUI(JComponent c) {
        return new EveTableUI();
    }

    @Override
    protected void installDefaults() {
        super.installDefaults();
        final @Nullable Object rowHeight = UIManager.get("Table.rowHeight");
        if (rowHeight instanceof Integer) {
            // Respected only while the application has not set the row height explicitly.
            LookAndFeel.installProperty(table, "rowHeight", rowHeight);
        }
        installGrid();
        installBooleanRenderer();
    }

    @Override
    protected void uninstallDefaults() {
        uninstallBooleanRenderer();
        uninstallGrid();
        super.uninstallDefaults();
    }

    @Override
    protected void installListeners() {
        super.installListeners();
        table.addFocusListener(markerRepainter);
    }

    @Override
    protected void uninstallListeners() {
        table.removeFocusListener(markerRepainter);
        super.uninstallListeners();
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        super.paint(g, c);
        final Rectangle clip = clip(g);
        paintSelectionMarkers(g, clip);
        if (!table.isEnabled()) {
            g.setColor(EvePalette.alpha(EvePalette.PANEL, 120));
            g.fillRect(clip.x, clip.y, clip.width, clip.height);
        }
    }

    private Rectangle clip(Graphics g) {
        final @Nullable Rectangle clip = g.getClipBounds();
        return clip != null ? clip : new Rectangle(0, 0, table.getWidth(), table.getHeight());
    }

    private void paintSelectionMarkers(Graphics g, Rectangle clip) {
        if (!table.getRowSelectionAllowed() || table.getSelectedRowCount() == 0 || table.getColumnCount() == 0) {
            return;
        }
        int first = table.rowAtPoint(new Point(0, clip.y));
        int last = table.rowAtPoint(new Point(0, clip.y + clip.height - 1));
        if (first < 0) {
            first = 0;
        }
        if (last < 0) {
            last = table.getRowCount() - 1;
        }
        final Color color = DataTableSupport.markerColor(table);
        final int x = table.getComponentOrientation().isLeftToRight() ? 0 : table.getWidth() - 2;
        for (int row = first; row <= last; row++) {
            if (table.isRowSelected(row)) {
                final Rectangle cell = table.getCellRect(row, 0, false);
                EvePaint.drawMarker(g, color, x, cell.y, cell.height);
            }
        }
    }

    private void repaintSelection() {
        if (table.getSelectedRowCount() > 0) {
            table.repaint();
        }
    }

    /**
     * Applies Eve grid defaults while the table still has JDK defaults (both line sets, 1x1 spacing).
     */
    private void installGrid() {
        if (!table.getShowHorizontalLines() || !table.getShowVerticalLines()
                || !new Dimension(1, 1).equals(table.getIntercellSpacing())) {
            return;
        }
        final boolean horizontal = flag("Table.showHorizontalLines", true);
        final boolean vertical = flag("Table.showVerticalLines", false);
        table.setShowHorizontalLines(horizontal);
        table.setShowVerticalLines(vertical);
        table.setIntercellSpacing(new Dimension(vertical ? 1 : 0, horizontal ? 1 : 0));
        table.putClientProperty(GRID_INSTALLED, Boolean.TRUE);
    }

    /**
     * Restores JDK grid defaults when the table still has the values set by {@link #installGrid()}.
     */
    private void uninstallGrid() {
        if (!Boolean.TRUE.equals(table.getClientProperty(GRID_INSTALLED))) {
            return;
        }
        table.putClientProperty(GRID_INSTALLED, null);
        final boolean horizontal = flag("Table.showHorizontalLines", true);
        final boolean vertical = flag("Table.showVerticalLines", false);
        if (table.getShowHorizontalLines() == horizontal && table.getShowVerticalLines() == vertical
                && new Dimension(vertical ? 1 : 0, horizontal ? 1 : 0).equals(table.getIntercellSpacing())) {
            table.setShowGrid(true);
            table.setIntercellSpacing(new Dimension(1, 1));
        }
    }

    private void installBooleanRenderer() {
        final @Nullable TableCellRenderer current = table.getDefaultRenderer(Boolean.class);
        if (current != null && JDK_BOOLEAN_RENDERER.equals(current.getClass().getName())) {
            table.putClientProperty(ORIGINAL_BOOLEAN_RENDERER, current);
            table.setDefaultRenderer(Boolean.class, new DataBooleanRenderer());
        }
    }

    private void uninstallBooleanRenderer() {
        final @Nullable Object original = table.getClientProperty(ORIGINAL_BOOLEAN_RENDERER);
        table.putClientProperty(ORIGINAL_BOOLEAN_RENDERER, null);
        if (original instanceof TableCellRenderer renderer
                && table.getDefaultRenderer(Boolean.class) instanceof DataBooleanRenderer) {
            table.setDefaultRenderer(Boolean.class, renderer);
        }
    }

    private static boolean flag(String key, boolean fallback) {
        final @Nullable Object value = UIManager.get(key);
        return value instanceof Boolean b ? b : fallback;
    }
}

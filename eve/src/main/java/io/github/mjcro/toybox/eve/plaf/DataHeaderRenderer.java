package io.github.mjcro.toybox.eve.plaf;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.util.List;
import java.util.Locale;

import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import io.github.mjcro.toybox.eve.EveBorders;
import io.github.mjcro.toybox.eve.EvePalette;

import org.jspecify.annotations.Nullable;

/**
 * Default table header renderer of the Eve Look and Feel.
 *
 * <p>Renders uppercase letter-spaced titles in dim text on a raised strip, with
 * 1px column separators, a bright bottom line, an accent chevron for the primary
 * sort column and a highlight for the column under the mouse pointer.
 */
public class DataHeaderRenderer extends DefaultTableCellRenderer implements UIResource {
    private static final Border PADDING = EveBorders.empty(3, 6, 3, 6);

    /**
     * Creates the renderer.
     */
    public DataHeaderRenderer() {
        setOpaque(false);
        setHorizontalAlignment(SwingConstants.LEADING);
        setHorizontalTextPosition(SwingConstants.LEADING);
        setIconTextGap(4);
        setName("TableHeader.renderer");
    }

    @Override
    public Component getTableCellRendererComponent(
            @Nullable JTable table,
            @Nullable Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column
    ) {
        final @Nullable JTableHeader header = table == null ? null : table.getTableHeader();
        final @Nullable SortOrder order = table == null ? null : sortOrder(table, column);
        final boolean sorted = order == SortOrder.ASCENDING || order == SortOrder.DESCENDING;

        setText(value == null ? "" : value.toString().toUpperCase(Locale.ROOT));
        setIcon(sortIcon(order));
        setBorder(PADDING);
        if (header != null) {
            setFont(header.getFont());
            setEnabled(header.isEnabled());
            setForeground(sorted ? EvePalette.TEXT_BRIGHT : header.getForeground());
            setBackground(isHighlighted(header, column) ? EvePalette.RAISED_HOVER : header.getBackground());
        } else {
            setFont(UIManager.getFont("TableHeader.font"));
            setForeground(UIManager.getColor("TableHeader.foreground"));
            setBackground(UIManager.getColor("TableHeader.background"));
        }
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        final int w = getWidth();
        final int h = getHeight();
        final @Nullable Color background = getBackground();
        if (background != null) {
            g.setColor(background);
            g.fillRect(0, 0, w, h);
        }
        super.paintComponent(g);
        g.setColor(EvePalette.BORDER);
        if (getComponentOrientation().isLeftToRight()) {
            g.fillRect(w - 1, 0, 1, h - 1);
        } else {
            g.fillRect(0, 0, 1, h - 1);
        }
        g.setColor(EvePalette.BORDER_BRIGHT);
        g.fillRect(0, h - 1, w, 1);
    }

    /**
     * Returns {@code true} when the column is under the mouse pointer or being dragged.
     */
    private static boolean isHighlighted(JTableHeader header, int column) {
        if (header.getUI() instanceof EveTableHeaderUI ui && ui.rolloverColumn() == column && header.isEnabled()) {
            return true;
        }
        final @Nullable TableColumn dragged = header.getDraggedColumn();
        return dragged != null && column >= 0 && column < header.getColumnModel().getColumnCount()
                && header.getColumnModel().getColumn(column) == dragged;
    }

    private static @Nullable SortOrder sortOrder(JTable table, int column) {
        final @Nullable RowSorter<?> sorter = table.getRowSorter();
        if (sorter == null || column < 0) {
            return null;
        }
        final List<? extends RowSorter.SortKey> keys = sorter.getSortKeys();
        if (keys.isEmpty()) {
            return null;
        }
        final RowSorter.SortKey key = keys.get(0);
        return key.getColumn() == table.convertColumnIndexToModel(column) ? key.getSortOrder() : null;
    }

    private static @Nullable Icon sortIcon(@Nullable SortOrder order) {
        if (order == SortOrder.ASCENDING) {
            return UIManager.getIcon("Table.ascendingSortIcon");
        }
        if (order == SortOrder.DESCENDING) {
            return UIManager.getIcon("Table.descendingSortIcon");
        }
        return null;
    }
}

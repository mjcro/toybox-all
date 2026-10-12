package io.github.mjcro.toybox.eve.plaf;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;
import javax.swing.table.TableCellRenderer;

import io.github.mjcro.toybox.eve.EveBorders;

import org.jspecify.annotations.Nullable;

/**
 * Check box renderer for {@link Boolean} table columns.
 *
 * <p>Replaces the JDK default Boolean renderer: paints its own background, so the
 * cell always follows row selection and alternate row tint
 * ({@code "Table.alternateRowColor"}) regardless of check box opacity.
 */
public class DataBooleanRenderer extends JCheckBox implements TableCellRenderer, UIResource {
    private static final Border NO_FOCUS = EveBorders.empty(1, 1, 1, 1);

    /**
     * Creates a centered check box renderer.
     */
    public DataBooleanRenderer() {
        setHorizontalAlignment(SwingConstants.CENTER);
        setBorderPainted(true);
        setOpaque(false);
        setName("Table.cellRenderer");
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table,
            @Nullable Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column
    ) {
        if (isSelected) {
            setForeground(table.getSelectionForeground());
            setBackground(table.getSelectionBackground());
        } else {
            setForeground(table.getForeground());
            setBackground(DataTableSupport.rowBackground(table, row));
        }
        setSelected(Boolean.TRUE.equals(value));
        setEnabled(table.isEnabled());
        final @Nullable Border focus = UIManager.getBorder("Table.focusCellHighlightBorder");
        setBorder(hasFocus && focus != null ? focus : NO_FOCUS);
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        final Color background = getBackground();
        if (background != null) {
            g.setColor(background);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        super.paintComponent(g);
    }
}

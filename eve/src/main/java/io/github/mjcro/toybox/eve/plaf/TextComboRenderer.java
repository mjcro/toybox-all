package io.github.mjcro.toybox.eve.plaf;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.JList;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import org.jspecify.annotations.Nullable;

import io.github.mjcro.toybox.eve.EveBorders;
import io.github.mjcro.toybox.eve.EvePaint;
import io.github.mjcro.toybox.eve.EvePalette;

/**
 * Compact combo box renderer: padded popup rows with an accent marker on the selected row.
 * The current value cell (index {@code -1}) has no padding, since the combo delegate
 * applies {@code ComboBox.padding} around it.
 */
final class TextComboRenderer extends BasicComboBoxRenderer.UIResource {
    private static final Border ITEM = EveBorders.empty(2, 6, 2, 6);
    private static final Border VALUE = EveBorders.empty(0, 0, 0, 0);

    private boolean marker = false;

    @Override
    public Component getListCellRendererComponent(
            JList<?> list,
            @Nullable Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus
    ) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        setBorder(index < 0 ? VALUE : ITEM);
        marker = isSelected && index >= 0;
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (marker) {
            EvePaint.drawMarker(g, EvePalette.ACCENT, 0, 0, getHeight());
        }
    }
}

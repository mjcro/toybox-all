package io.github.mjcro.toybox.eve.plaf;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.UIResource;

import io.github.mjcro.toybox.eve.EvePalette;

/**
 * Field-like 1px frame of compound inputs (combo boxes, spinners).
 *
 * <p>The frame is {@link EvePalette#ACCENT} while the component or its inner editor owns
 * focus or the combo popup is open, {@link EvePalette#BORDER_BRIGHT} on hover (client
 * property {@link #ROLLOVER} set to {@link Boolean#TRUE}), {@link EvePalette#GRID} when disabled.
 */
public final class TextFrameBorder extends AbstractBorder implements UIResource {
    /** Client property key set by the owning delegate while the mouse is over the component. */
    static final String ROLLOVER = "Eve.rollover";

    /** Padding inside the frame. */
    private final Insets insets;

    /**
     * Creates the border.
     *
     * @param top    top inset in pixels, including the 1px line.
     * @param left   left inset in pixels, including the 1px line.
     * @param bottom bottom inset in pixels, including the 1px line.
     * @param right  right inset in pixels, including the 1px line.
     */
    public TextFrameBorder(int top, int left, int bottom, int right) {
        this.insets = new Insets(top, left, bottom, right);
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        g.setColor(lineColor(c));
        g.drawRect(x, y, width - 1, height - 1);
    }

    @Override
    public Insets getBorderInsets(Component c, Insets target) {
        target.set(insets.top, insets.left, insets.bottom, insets.right);
        return target;
    }

    private static Color lineColor(Component c) {
        if (!c.isEnabled()) {
            return EvePalette.GRID;
        }
        if (isActive(c)) {
            return EvePalette.ACCENT;
        }
        if (c instanceof JComponent jc && Boolean.TRUE.equals(jc.getClientProperty(ROLLOVER))) {
            return EvePalette.BORDER_BRIGHT;
        }
        return EvePalette.BORDER;
    }

    private static boolean isActive(Component c) {
        if (c.isFocusOwner()) {
            return true;
        }
        if (c instanceof JComboBox<?> combo) {
            return combo.isPopupVisible()
                    || (combo.isEditable() && combo.getEditor().getEditorComponent().isFocusOwner());
        }
        if (c instanceof JSpinner spinner && spinner.getEditor() instanceof JSpinner.DefaultEditor editor) {
            return editor.getTextField().isFocusOwner();
        }
        return false;
    }
}

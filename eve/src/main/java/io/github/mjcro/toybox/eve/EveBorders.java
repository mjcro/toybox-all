package io.github.mjcro.toybox.eve;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.AbstractButton;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.UIResource;

/**
 * Border factory of the Eve Look and Feel.
 *
 * <p>Every returned border implements {@link UIResource}, so it is replaced on
 * Look and Feel switch and never overrides borders set by application code.
 * Insets are deliberately small to keep the UI dense.
 */
public final class EveBorders {
    private EveBorders() {
    }

    /**
     * Returns the border for text fields and other single-line inputs:
     * 1px frame that turns {@link EvePalette#ACCENT} when the component owns focus.
     *
     * @return non-null border with 3px vertical and 6px horizontal padding.
     */
    public static Border field() {
        return new FieldBorder(new Insets(3, 6, 3, 6));
    }

    /**
     * Returns the border for multi-line text components placed inside scroll panes:
     * padding only, since the scroll pane draws the frame.
     *
     * @return non-null empty border of 3px vertical and 5px horizontal padding.
     */
    public static Border textArea() {
        return empty(3, 5, 3, 5);
    }

    /**
     * Returns the border for push and toggle buttons: chamfered 1px frame whose
     * color follows {@link EvePaint#buttonLine}.
     *
     * @return non-null border with 3px vertical and 10px horizontal padding.
     */
    public static Border button() {
        return new ButtonBorder(new Insets(3, 10, 3, 10));
    }

    /**
     * Returns a plain 1px line border.
     *
     * @param color line color, not null.
     * @return non-null border with 1px insets.
     */
    public static Border line(Color color) {
        return new BorderUIResource.LineBorderUIResource(color, 1);
    }

    /**
     * Returns the border for scroll panes and other framed containers.
     *
     * @return non-null 1px {@link EvePalette#BORDER} line border.
     */
    public static Border frame() {
        return line(EvePalette.BORDER);
    }

    /**
     * Returns the border for popup menus, combo box popups and tooltips.
     *
     * @return non-null 1px {@link EvePalette#BORDER_BRIGHT} line border.
     */
    public static Border popup() {
        return line(EvePalette.BORDER_BRIGHT);
    }

    /**
     * Returns an empty padding border.
     *
     * @param top    top padding in pixels.
     * @param left   left padding in pixels.
     * @param bottom bottom padding in pixels.
     * @param right  right padding in pixels.
     * @return non-null border.
     */
    public static Border empty(int top, int left, int bottom, int right) {
        return new BorderUIResource.EmptyBorderUIResource(top, left, bottom, right);
    }

    /**
     * Frame for input fields; accent-colored when focused, dimmed when disabled.
     */
    private static final class FieldBorder extends AbstractBorder implements UIResource {
        private final Insets insets;

        FieldBorder(Insets insets) {
            this.insets = insets;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            final Color line;
            if (!c.isEnabled()) {
                line = EvePalette.GRID;
            } else if (c.isFocusOwner()) {
                line = EvePalette.ACCENT;
            } else {
                line = EvePalette.BORDER;
            }
            g.setColor(line);
            g.drawRect(x, y, width - 1, height - 1);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets target) {
            target.set(insets.top, insets.left, insets.bottom, insets.right);
            return target;
        }
    }

    /**
     * Chamfered frame for buttons; color follows button model state.
     */
    private static final class ButtonBorder extends AbstractBorder implements UIResource {
        private final Insets insets;

        ButtonBorder(Insets insets) {
            this.insets = insets;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            final Color line = c instanceof AbstractButton b ? EvePaint.buttonLine(b) : EvePalette.BORDER;
            EvePaint.drawCut(g, line, x, y, width, height, EvePaint.DIAGONAL);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets target) {
            target.set(insets.top, insets.left, insets.bottom, insets.right);
            return target;
        }
    }
}

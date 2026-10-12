package io.github.mjcro.toybox.eve;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;

/**
 * Shared painting primitives of the Eve Look and Feel.
 *
 * <p>Geometry conventions: all {@code x, y, w, h} arguments describe the outer
 * bounds in pixels; 1px frames are drawn inside these bounds. Cut corners are
 * the signature EVE shape: a rectangle with one or more corners chamfered at 45°.
 */
public final class EvePaint {
    /** Default chamfer size of cut corners, in pixels. */
    public static final int CUT = 4;
    /** Corner mask: top-left corner. */
    public static final int TOP_LEFT = 1;
    /** Corner mask: top-right corner. */
    public static final int TOP_RIGHT = 2;
    /** Corner mask: bottom-right corner. */
    public static final int BOTTOM_RIGHT = 4;
    /** Corner mask: bottom-left corner. */
    public static final int BOTTOM_LEFT = 8;
    /** Corner mask: the EVE default, top-left and bottom-right corners cut. */
    public static final int DIAGONAL = TOP_LEFT | BOTTOM_RIGHT;

    private EvePaint() {
    }

    /**
     * Creates a graphics copy with antialiasing and pure stroke control enabled.
     * The caller MUST {@link Graphics#dispose() dispose} the returned instance.
     *
     * @param g source graphics, not null.
     * @return new non-null graphics.
     */
    public static Graphics2D smooth(Graphics g) {
        final Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        return g2;
    }

    /**
     * Builds a rectangle outline with chamfered corners.
     *
     * @param x       left coordinate.
     * @param y       top coordinate.
     * @param w       width.
     * @param h       height.
     * @param cut     chamfer size in pixels; {@code 0} produces a plain rectangle.
     * @param corners corner mask built from {@link #TOP_LEFT}, {@link #TOP_RIGHT},
     *                {@link #BOTTOM_RIGHT}, {@link #BOTTOM_LEFT}.
     * @return new non-null closed path.
     */
    public static Path2D cutShape(double x, double y, double w, double h, double cut, int corners) {
        final double tl = (corners & TOP_LEFT) != 0 ? cut : 0;
        final double tr = (corners & TOP_RIGHT) != 0 ? cut : 0;
        final double br = (corners & BOTTOM_RIGHT) != 0 ? cut : 0;
        final double bl = (corners & BOTTOM_LEFT) != 0 ? cut : 0;
        final Path2D.Double p = new Path2D.Double();
        p.moveTo(x + tl, y);
        p.lineTo(x + w - tr, y);
        p.lineTo(x + w, y + tr);
        p.lineTo(x + w, y + h - br);
        p.lineTo(x + w - br, y + h);
        p.lineTo(x + bl, y + h);
        p.lineTo(x, y + h - bl);
        p.lineTo(x, y + tl);
        p.closePath();
        return p;
    }

    /**
     * Fills a chamfered rectangle.
     *
     * @param g       target graphics, not null.
     * @param fill    fill color, not null.
     * @param x       left coordinate.
     * @param y       top coordinate.
     * @param w       width.
     * @param h       height.
     * @param corners corner mask, see {@link #cutShape}.
     */
    public static void fillCut(Graphics g, Color fill, int x, int y, int w, int h, int corners) {
        final Graphics2D g2 = smooth(g);
        try {
            g2.setColor(fill);
            g2.fill(cutShape(x, y, w, h, CUT, corners));
        } finally {
            g2.dispose();
        }
    }

    /**
     * Draws a 1px chamfered frame inside the given bounds.
     *
     * @param g       target graphics, not null.
     * @param line    frame color, not null.
     * @param x       left coordinate.
     * @param y       top coordinate.
     * @param w       width.
     * @param h       height.
     * @param corners corner mask, see {@link #cutShape}.
     */
    public static void drawCut(Graphics g, Color line, int x, int y, int w, int h, int corners) {
        final Graphics2D g2 = smooth(g);
        try {
            g2.setColor(line);
            g2.setStroke(new BasicStroke(1f));
            g2.draw(cutShape(x + 0.5, y + 0.5, w - 1, h - 1, CUT, corners));
        } finally {
            g2.dispose();
        }
    }

    /**
     * Fills a rectangle with a top-to-bottom gradient.
     *
     * @param g      target graphics, not null.
     * @param top    color at the top edge, not null.
     * @param bottom color at the bottom edge, not null.
     * @param x      left coordinate.
     * @param y      top coordinate.
     * @param w      width.
     * @param h      height.
     */
    public static void fillGradient(Graphics g, Color top, Color bottom, int x, int y, int w, int h) {
        final Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setPaint(new GradientPaint(0, y, top, 0, y + h, bottom));
            g2.fillRect(x, y, w, h);
        } finally {
            g2.dispose();
        }
    }

    /**
     * Draws L-shaped corner brackets around the given bounds, the EVE "targeting" frame.
     *
     * @param g      target graphics, not null.
     * @param line   bracket color, not null.
     * @param x      left coordinate.
     * @param y      top coordinate.
     * @param w      width.
     * @param h      height.
     * @param length bracket arm length in pixels.
     */
    public static void drawBrackets(Graphics g, Color line, int x, int y, int w, int h, int length) {
        final int r = x + w - 1;
        final int b = y + h - 1;
        g.setColor(line);
        g.drawLine(x, y, x + length, y);
        g.drawLine(x, y, x, y + length);
        g.drawLine(r - length, y, r, y);
        g.drawLine(r, y, r, y + length);
        g.drawLine(x, b, x + length, b);
        g.drawLine(x, b - length, x, b);
        g.drawLine(r - length, b, r, b);
        g.drawLine(r, b - length, r, b);
    }

    /**
     * Draws a vertical accent marker bar, used for selected list rows, tabs and focused fields.
     *
     * @param g     target graphics, not null.
     * @param color bar color, not null.
     * @param x     left coordinate.
     * @param y     top coordinate.
     * @param h     bar height.
     */
    public static void drawMarker(Graphics g, Color color, int x, int y, int h) {
        g.setColor(color);
        g.fillRect(x, y, 2, h);
    }

    /**
     * Resolves the fill color of a raised clickable element from its model state.
     *
     * @param model button model, not null.
     * @return non-null fill color.
     */
    public static Color buttonFill(ButtonModel model) {
        if (!model.isEnabled()) {
            return EvePalette.PANEL;
        }
        if (model.isPressed() && model.isArmed()) {
            return EvePalette.ACCENT_DARK;
        }
        if (model.isSelected()) {
            return EvePalette.SELECTION;
        }
        if (model.isRollover()) {
            return EvePalette.RAISED_HOVER;
        }
        return EvePalette.RAISED;
    }

    /**
     * Resolves the frame color of a raised clickable element from its state.
     *
     * @param button button, not null.
     * @return non-null frame color.
     */
    public static Color buttonLine(AbstractButton button) {
        final ButtonModel model = button.getModel();
        if (!model.isEnabled()) {
            return EvePalette.GRID;
        }
        if (button.isFocusOwner() || model.isSelected() || (model.isPressed() && model.isArmed())) {
            return EvePalette.ACCENT;
        }
        if (model.isRollover()) {
            return EvePalette.BORDER_BRIGHT;
        }
        return EvePalette.BORDER;
    }

    /**
     * Returns the text color for a component, honoring its enabled state.
     *
     * @param c component, not null.
     * @return component foreground when enabled, {@link EvePalette#TEXT_DIM} otherwise; never null.
     */
    public static Color text(Component c) {
        return c.isEnabled() ? c.getForeground() : EvePalette.TEXT_DIM;
    }
}

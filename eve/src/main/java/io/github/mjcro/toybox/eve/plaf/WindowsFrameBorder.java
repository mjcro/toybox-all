package io.github.mjcro.toybox.eve.plaf;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.Window;

import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.UIResource;

import io.github.mjcro.toybox.eve.EvePaint;
import io.github.mjcro.toybox.eve.EvePalette;

/**
 * Chamfered window frame for decorated root panes, internal frames and desktop icons.
 *
 * <p>The frame is a 1px line with the top-left and bottom-right corners cut; the
 * area outside the cut is filled with the parent background so the window looks
 * chamfered. The rest of the border thickness is filled with {@link EvePalette#PANEL}
 * and doubles as the resize grip of decorated windows. Active (focused window or
 * selected internal frame) frames are brighter and tinted with the accent color.
 */
public final class WindowsFrameBorder extends AbstractBorder implements UIResource {
    private static final int CUT = 6;
    /** Accent color of the active frame tick. */
    private final Color accent;
    /** Frame thickness in pixels. */
    private final int thickness;

    /**
     * Creates the border.
     *
     * @param accent    accent color of the frame (e.g. {@link EvePalette#WARNING} for warning dialogs), not null.
     * @param thickness border thickness in pixels, at least 1.
     */
    public WindowsFrameBorder(Color accent, int thickness) {
        this.accent = accent;
        this.thickness = Math.max(1, thickness);
    }

    /**
     * Returns the accent color of this frame.
     *
     * @return non-null accent color.
     */
    public Color getAccent() {
        return accent;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        final boolean active = isActive(c);
        final Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setColor(EvePalette.PANEL);
            for (int i = 1; i < thickness; i++) {
                g2.drawRect(x + i, y + i, width - 2 * i - 1, height - 2 * i - 1);
            }
            final Container parent = c.getParent();
            g2.setColor(parent != null && !(parent instanceof Window) ? parent.getBackground() : EvePalette.BACKGROUND);
            g2.fillPolygon(new Polygon(new int[]{x, x + CUT, x}, new int[]{y, y, y + CUT}, 3));
            final int r = x + width;
            final int b = y + height;
            g2.fillPolygon(new Polygon(new int[]{r, r - CUT, r}, new int[]{b, b, b - CUT}, 3));
        } finally {
            g2.dispose();
        }
        final Color line;
        if (!active) {
            line = EvePalette.BORDER;
        } else if (accent.equals(EvePalette.ACCENT)) {
            line = EvePalette.BORDER_BRIGHT;
        } else {
            line = EvePalette.mix(EvePalette.BORDER, accent, 0.65f);
        }
        drawChamfer(g, line, x, y, width, height);
        if (active && thickness > 1) {
            // Short accent tick right after the cut corner, the EVE "active window" marker.
            g.setColor(accent);
            g.fillRect(x + CUT, y, Math.min(28, Math.max(0, width - 2 * CUT)), 2);
        }
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.set(thickness, thickness, thickness, thickness);
        return insets;
    }

    private static void drawChamfer(Graphics g, Color line, int x, int y, int w, int h) {
        final Graphics2D g2 = EvePaint.smooth(g);
        try {
            g2.setColor(line);
            g2.draw(EvePaint.cutShape(x + 0.5, y + 0.5, w - 1, h - 1, CUT, EvePaint.DIAGONAL));
        } finally {
            g2.dispose();
        }
    }

    private static boolean isActive(Component c) {
        if (c instanceof JInternalFrame frame) {
            return frame.isSelected();
        }
        if (c instanceof JInternalFrame.JDesktopIcon icon) {
            return icon.getInternalFrame().isSelected();
        }
        final Window window = c instanceof Window w ? w : SwingUtilities.getWindowAncestor(c);
        return window == null || window.isActive();
    }
}

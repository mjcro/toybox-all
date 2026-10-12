package io.github.mjcro.toybox.eve.plaf;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import javax.swing.JButton;
import javax.swing.JSplitPane;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import io.github.mjcro.toybox.eve.EvePaint;
import io.github.mjcro.toybox.eve.EvePalette;

/**
 * Split pane divider of the Eve Look and Feel.
 *
 * <p>Flat panel-colored strip with a centered dotted grip. The grip turns accent on
 * hover; while dragged the whole divider is highlighted. One-touch-expand buttons
 * are tiny triangles that light up on hover.
 */
public class RangesSplitPaneDivider extends BasicSplitPaneDivider {
    private static final int DOT = 2;
    private static final int DOT_GAP = 3;
    private static final int DOTS = 5;

    /**
     * Creates the divider.
     *
     * @param ui owning split pane delegate, not null.
     */
    public RangesSplitPaneDivider(BasicSplitPaneUI ui) {
        super(ui);
    }

    @Override
    public void paint(Graphics g) {
        final boolean dragging = dragger != null;
        g.setColor(dragging ? EvePalette.SELECTION : splitPane.getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        final Color grip;
        if (!splitPane.isEnabled()) {
            grip = EvePalette.GRID;
        } else if (dragging || isMouseOver()) {
            grip = EvePalette.ACCENT;
        } else {
            grip = EvePalette.BORDER_BRIGHT;
        }
        g.setColor(grip);
        final int length = DOTS * DOT + (DOTS - 1) * DOT_GAP;
        if (orientation == JSplitPane.HORIZONTAL_SPLIT) {
            final int x = (getWidth() - DOT) / 2;
            final int y0 = (getHeight() - length) / 2;
            for (int i = 0; i < DOTS; i++) {
                g.fillRect(x, y0 + i * (DOT + DOT_GAP), DOT, DOT);
            }
        } else {
            final int y = (getHeight() - DOT) / 2;
            final int x0 = (getWidth() - length) / 2;
            for (int i = 0; i < DOTS; i++) {
                g.fillRect(x0 + i * (DOT + DOT_GAP), y, DOT, DOT);
            }
        }
        // Paints one-touch buttons and the (empty) border.
        super.paint(g);
    }

    @Override
    protected void setMouseOver(boolean mouseOver) {
        if (isMouseOver() != mouseOver) {
            super.setMouseOver(mouseOver);
            repaint();
        }
    }

    @Override
    protected void prepareForDragging() {
        super.prepareForDragging();
        repaint();
    }

    @Override
    protected void finishDraggingTo(int location) {
        super.finishDraggingTo(location);
        repaint();
    }

    @Override
    protected JButton createLeftOneTouchButton() {
        return new RangesOneTouchButton(true);
    }

    @Override
    protected JButton createRightOneTouchButton() {
        return new RangesOneTouchButton(false);
    }

    /**
     * Tiny triangle button collapsing the split pane towards one side.
     */
    private final class RangesOneTouchButton extends JButton {
        private final boolean leading;

        RangesOneTouchButton(boolean leading) {
            this.leading = leading;
            setFocusable(false);
            setRequestFocusEnabled(false);
            setRolloverEnabled(true);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
        }

        @Override
        public void paint(Graphics g) {
            final int w = getWidth();
            final int h = getHeight();
            final Color color = getModel().isRollover() ? EvePalette.ACCENT : EvePalette.TEXT_DIM;
            final Path2D.Double p = new Path2D.Double();
            if (orientation == JSplitPane.HORIZONTAL_SPLIT) {
                // Vertical divider: buttons point left / right.
                final double s = Math.min(w, h / 2.0);
                final double cy = h / 2.0;
                final double x0 = (w - s) / 2.0;
                if (leading) {
                    p.moveTo(x0 + s, cy - s);
                    p.lineTo(x0, cy);
                    p.lineTo(x0 + s, cy + s);
                } else {
                    p.moveTo(x0, cy - s);
                    p.lineTo(x0 + s, cy);
                    p.lineTo(x0, cy + s);
                }
            } else {
                final double s = Math.min(h, w / 2.0);
                final double cx = w / 2.0;
                final double y0 = (h - s) / 2.0;
                if (leading) {
                    p.moveTo(cx - s, y0 + s);
                    p.lineTo(cx, y0);
                    p.lineTo(cx + s, y0 + s);
                } else {
                    p.moveTo(cx - s, y0);
                    p.lineTo(cx, y0 + s);
                    p.lineTo(cx + s, y0);
                }
            }
            p.closePath();
            final Graphics2D g2 = EvePaint.smooth(g);
            try {
                g2.setColor(color);
                g2.fill(p);
            } finally {
                g2.dispose();
            }
        }
    }
}

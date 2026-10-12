package io.github.mjcro.toybox.eve.plaf;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

import javax.swing.AbstractButton;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;

import io.github.mjcro.toybox.eve.EvePaint;
import io.github.mjcro.toybox.eve.EvePalette;

/**
 * Borders of the buttons component group: push/toggle buttons, tooltips and titled borders.
 * All returned borders implement {@link UIResource}.
 */
public final class ButtonsBorders {
    private ButtonsBorders() {
    }

    /**
     * Returns the push and toggle button border: chamfered 1px frame, accent when focused,
     * pressed or selected, dark accent for default buttons.
     *
     * @return non-null border with 3px vertical and 10px horizontal padding.
     */
    public static Border button() {
        return new ButtonBorder();
    }

    /**
     * Returns the tooltip border: 1px {@link EvePalette#BORDER_BRIGHT} frame with a 2px accent marker on the left.
     *
     * @return non-null border.
     */
    public static Border toolTip() {
        return new ToolTipBorder();
    }

    /**
     * Returns the frame used by {@link javax.swing.border.TitledBorder} by default: section frame
     * with a brighter top line, chamfered top-left corner and an accent tick below it.
     *
     * @return non-null border with 1px insets.
     */
    public static Border titledFrame() {
        return new TitledFrameBorder();
    }

    /**
     * Chamfered button frame; color follows {@link ButtonsPainter#line}.
     */
    private static final class ButtonBorder extends AbstractBorder implements UIResource {
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            if (c instanceof AbstractButton b) {
                if (!b.isBorderPainted() || ButtonsPainter.isIdleToolBarButton(b, b.getModel())) {
                    return;
                }
                EvePaint.drawCut(g, ButtonsPainter.line(b), x, y, width, height, EvePaint.DIAGONAL);
            } else {
                EvePaint.drawCut(g, EvePalette.BORDER, x, y, width, height, EvePaint.DIAGONAL);
            }
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.set(3, 10, 3, 10);
            return insets;
        }
    }

    /**
     * Tooltip frame with the accent marker.
     */
    private static final class ToolTipBorder extends AbstractBorder implements UIResource {
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.setColor(EvePalette.BORDER_BRIGHT);
            g.drawRect(x, y, width - 1, height - 1);
            EvePaint.drawMarker(g, c.isEnabled() ? EvePalette.ACCENT : EvePalette.TEXT_DIM, x + 1, y + 1, height - 2);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.set(3, 8, 3, 7);
            return insets;
        }
    }

    /**
     * Section frame: dim sides, brighter top line, chamfered top-left corner and an accent tick.
     */
    private static final class TitledFrameBorder extends AbstractBorder implements UIResource {
        private static final int TICK = 8;

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            final Color frame = c.isEnabled() ? EvePalette.BORDER : EvePalette.GRID;
            final Graphics2D g2 = EvePaint.smooth(g);
            try {
                g2.setStroke(new BasicStroke(1f));
                g2.setColor(EvePalette.GRID);
                g2.draw(EvePaint.cutShape(x + 0.5, y + 0.5, width - 1, height - 1, EvePaint.CUT, EvePaint.TOP_LEFT));
                g2.setColor(frame);
                g2.drawLine(x + EvePaint.CUT, y, x + width - 1, y);
                g2.drawLine(x, y + EvePaint.CUT, x + EvePaint.CUT, y);
            } finally {
                g2.dispose();
            }
            // Vertical tick on the left edge: the top edge near the corner is covered by the title.
            g.setColor(c.isEnabled() ? EvePalette.ACCENT : EvePalette.TEXT_DIM);
            g.fillRect(x, y + EvePaint.CUT, 2, TICK);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.set(1, 1, 1, 1);
            return insets;
        }
    }
}

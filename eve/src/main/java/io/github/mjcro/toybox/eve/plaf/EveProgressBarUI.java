package io.github.mjcro.toybox.eve.plaf;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LinearGradientPaint;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicProgressBarUI;

import io.github.mjcro.toybox.eve.EvePalette;

/**
 * Eve UI delegate for {@link javax.swing.JProgressBar}.
 *
 * <p>Flat dark track framed by the component border, filled with an accent bar styled
 * after EVE capacitor gauges: a bright leading edge and, when no text is painted, thin
 * segment gaps. Indeterminate mode slides a glowing accent block. Painted text switches
 * color over the filled part to stay readable.
 */
public class EveProgressBarUI extends BasicProgressBarUI {
    private static final int SEGMENT = 6;

    /**
     * Creates the delegate; prefer {@link #createUI(javax.swing.JComponent)}.
     */
    public EveProgressBarUI() {
    }

    /**
     * Creates the UI delegate; called reflectively by {@link javax.swing.UIManager}.
     *
     * @param c component to create the delegate for, not null.
     * @return new non-null delegate.
     */
    public static ComponentUI createUI(JComponent c) {
        return new EveProgressBarUI();
    }

    @Override
    protected void paintDeterminate(Graphics g, JComponent c) {
        final Rectangle inner = inner(c);
        if (inner.isEmpty()) {
            return;
        }
        paintTrack(g, inner);
        final Insets b = progressBar.getInsets();
        final int amount = getAmountFull(b, inner.width, inner.height);
        final Rectangle fill = fillBounds(inner, amount);
        if (!fill.isEmpty()) {
            paintFill(g, fill, !progressBar.isStringPainted());
        }
        if (progressBar.isStringPainted()) {
            paintString(g, inner.x, inner.y, inner.width, inner.height, amount, b);
        }
    }

    @Override
    protected void paintIndeterminate(Graphics g, JComponent c) {
        final Rectangle inner = inner(c);
        if (inner.isEmpty()) {
            return;
        }
        paintTrack(g, inner);
        final Rectangle box = getBox(boxRect);
        boxRect = box;
        int amount = 0;
        if (box != null && !box.isEmpty()) {
            paintGlow(g, box.intersection(inner));
            amount = horizontal() ? box.width : box.height;
        }
        if (progressBar.isStringPainted()) {
            paintString(g, inner.x, inner.y, inner.width, inner.height, amount, progressBar.getInsets());
        }
    }

    @Override
    protected int getBoxLength(int availableLength, int otherDimension) {
        return Math.max(otherDimension, availableLength / 4);
    }

    private boolean horizontal() {
        return progressBar.getOrientation() == JProgressBar.HORIZONTAL;
    }

    private Rectangle inner(JComponent c) {
        final Insets b = c.getInsets();
        return new Rectangle(b.left, b.top, c.getWidth() - b.left - b.right, c.getHeight() - b.top - b.bottom);
    }

    /**
     * Computes the filled area: grows from leading edge horizontally, from the bottom vertically.
     */
    private Rectangle fillBounds(Rectangle inner, int amount) {
        if (horizontal()) {
            final int x = progressBar.getComponentOrientation().isLeftToRight()
                    ? inner.x
                    : inner.x + inner.width - amount;
            return new Rectangle(x, inner.y, amount, inner.height);
        }
        return new Rectangle(inner.x, inner.y + inner.height - amount, inner.width, amount);
    }

    private void paintTrack(Graphics g, Rectangle inner) {
        g.setColor(progressBar.getBackground());
        g.fillRect(inner.x, inner.y, inner.width, inner.height);
    }

    private void paintFill(Graphics g, Rectangle fill, boolean segmented) {
        final boolean enabled = progressBar.isEnabled();
        final Color base = enabled ? progressBar.getForeground() : EvePalette.BORDER_BRIGHT;
        final Color dark = EvePalette.mix(base, EvePalette.BACKGROUND, 0.35f);
        final Graphics2D g2 = (Graphics2D) g.create();
        try {
            // Gradient across the bar thickness gives the gauge a lit, cylindrical look.
            if (horizontal()) {
                g2.setPaint(new LinearGradientPaint(0, fill.y, 0, fill.y + fill.height,
                        new float[]{0f, 1f}, new Color[]{base, dark}));
            } else {
                g2.setPaint(new LinearGradientPaint(fill.x, 0, fill.x + fill.width, 0,
                        new float[]{0f, 1f}, new Color[]{base, dark}));
            }
            g2.fill(fill);
            if (segmented) {
                g2.setColor(EvePalette.alpha(progressBar.getBackground(), 170));
                if (horizontal()) {
                    for (int x = fill.x + SEGMENT; x < fill.x + fill.width - 1; x += SEGMENT) {
                        g2.drawLine(x, fill.y, x, fill.y + fill.height - 1);
                    }
                } else {
                    for (int y = fill.y + fill.height - SEGMENT; y > fill.y; y -= SEGMENT) {
                        g2.drawLine(fill.x, y, fill.x + fill.width - 1, y);
                    }
                }
            }
            // Bright leading edge marks the current value.
            g2.setColor(enabled ? EvePalette.TEXT_BRIGHT : EvePalette.TEXT_DIM);
            if (!horizontal()) {
                g2.drawLine(fill.x, fill.y, fill.x + fill.width - 1, fill.y);
            } else if (progressBar.getComponentOrientation().isLeftToRight()) {
                g2.drawLine(fill.x + fill.width - 1, fill.y, fill.x + fill.width - 1, fill.y + fill.height - 1);
            } else {
                g2.drawLine(fill.x, fill.y, fill.x, fill.y + fill.height - 1);
            }
        } finally {
            g2.dispose();
        }
    }

    private void paintGlow(Graphics g, Rectangle box) {
        if (box.isEmpty()) {
            return;
        }
        final Color base = progressBar.isEnabled() ? progressBar.getForeground() : EvePalette.BORDER_BRIGHT;
        final Color clear = EvePalette.alpha(base, 0);
        final float[] fractions = {0f, 0.5f, 1f};
        final Color[] colors = {clear, base, clear};
        final Graphics2D g2 = (Graphics2D) g.create();
        try {
            if (horizontal()) {
                g2.setPaint(new LinearGradientPaint(box.x, 0, box.x + box.width, 0, fractions, colors));
            } else {
                g2.setPaint(new LinearGradientPaint(0, box.y, 0, box.y + box.height, fractions, colors));
            }
            g2.fill(box);
        } finally {
            g2.dispose();
        }
    }
}

package io.github.mjcro.toybox.eve.plaf;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Path2D;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.plaf.basic.BasicGraphicsUtils;

import io.github.mjcro.toybox.eve.EvePaint;
import io.github.mjcro.toybox.eve.EvePalette;

/**
 * Painting routines shared by Eve button-like delegates: push buttons, toggles,
 * check boxes and radio buttons.
 */
final class ButtonsPainter {
    private ButtonsPainter() {
    }

    /**
     * Fills the chamfered content area of a push or toggle button with a subtle
     * top-down gradient. Selected toggles also get an accent bar along the bottom edge (dim when disabled).
     * Does nothing when the content area is not filled, or for idle tool bar buttons (flat until hovered).
     *
     * @param g target graphics, not null.
     * @param b button, not null.
     */
    static void paintBackground(Graphics g, AbstractButton b) {
        final ButtonModel model = b.getModel();
        if (!b.isContentAreaFilled() || isIdleToolBarButton(b, model)) {
            return;
        }
        final Color fill = fill(b);
        final int w = b.getWidth();
        final int h = b.getHeight();
        final Graphics2D g2 = EvePaint.smooth(g);
        try {
            final Path2D shape = EvePaint.cutShape(0, 0, w, h, EvePaint.CUT, EvePaint.DIAGONAL);
            if (model.isEnabled()) {
                g2.setPaint(new GradientPaint(0, 0, EvePalette.mix(fill, EvePalette.TEXT_BRIGHT, 0.05f), 0, h, fill));
            } else {
                g2.setColor(fill);
            }
            g2.fill(shape);
            if (model.isSelected() && !(b instanceof JButton)) {
                g2.clip(shape);
                g2.setColor(model.isEnabled() ? EvePalette.ACCENT : EvePalette.BORDER);
                g2.fillRect(EvePaint.CUT, h - 3, w - EvePaint.CUT * 2, 2);
            }
        } finally {
            g2.dispose();
        }
    }

    /**
     * Resolves the content fill color of a push or toggle button.
     *
     * @param b button, not null.
     * @return non-null fill color.
     */
    static Color fill(AbstractButton b) {
        final ButtonModel model = b.getModel();
        if (model.isEnabled() && !model.isSelected() && !(model.isPressed() && model.isArmed()) && isDefault(b)) {
            return model.isRollover()
                    ? EvePalette.mix(EvePalette.RAISED_HOVER, EvePalette.SELECTION, 0.6f)
                    : EvePalette.mix(EvePalette.RAISED, EvePalette.SELECTION, 0.5f);
        }
        return EvePaint.buttonFill(model);
    }

    /**
     * Resolves the frame color of a push or toggle button. Unlike {@link EvePaint#buttonLine},
     * default buttons get a dark accent frame.
     *
     * @param b button, not null.
     * @return non-null frame color.
     */
    static Color line(AbstractButton b) {
        final Color base = EvePaint.buttonLine(b);
        if (base == EvePalette.BORDER && isDefault(b)) {
            return EvePalette.ACCENT_DARK;
        }
        return base;
    }

    /**
     * Resolves the text color of a button-like component.
     *
     * @param b button, not null.
     * @return {@link EvePalette#TEXT_DIM} when disabled, the foreground otherwise; never null.
     */
    static Color text(AbstractButton b) {
        if (!b.getModel().isEnabled()) {
            return EvePalette.TEXT_DIM;
        }
        return b.getForeground();
    }

    /**
     * Draws button text with mnemonic underline.
     *
     * @param g        target graphics, not null.
     * @param b        button, not null.
     * @param textRect text bounds computed by the delegate, not null.
     * @param text     clipped text to draw, not null.
     * @param shift    text shift offset in pixels, applied to both axes.
     */
    static void paintText(Graphics g, AbstractButton b, Rectangle textRect, String text, int shift) {
        final FontMetrics fm = b.getFontMetrics(g.getFont());
        g.setColor(text(b));
        BasicGraphicsUtils.drawStringUnderlineCharAt(
                b,
                (Graphics2D) g,
                text,
                b.getDisplayedMnemonicIndex(),
                textRect.x + shift,
                textRect.y + fm.getAscent() + shift
        );
    }

    /**
     * Checks whether the button sits on a tool bar and is neither hovered, pressed nor selected.
     *
     * @param b     button, not null.
     * @param model button model, not null.
     * @return true if the button should be painted flat.
     */
    static boolean isIdleToolBarButton(AbstractButton b, ButtonModel model) {
        return b.getParent() instanceof JToolBar
                && !model.isRollover()
                && !model.isSelected()
                && !(model.isPressed() && model.isArmed());
    }

    private static boolean isDefault(AbstractButton b) {
        return b instanceof JButton jb && jb.isDefaultButton();
    }
}

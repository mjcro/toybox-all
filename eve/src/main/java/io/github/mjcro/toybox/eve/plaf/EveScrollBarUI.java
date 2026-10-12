package io.github.mjcro.toybox.eve.plaf;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollBarUI;

import org.jspecify.annotations.Nullable;

import io.github.mjcro.toybox.eve.EvePalette;

/**
 * Eve UI delegate for {@link javax.swing.JScrollBar}.
 *
 * <p>Slim bar without arrow buttons: flat track and an inset thumb that brightens
 * on hover and turns accent while dragged. Width comes from {@code ScrollBar.width}.
 */
public class EveScrollBarUI extends BasicScrollBarUI {
    private static final int THUMB_INSET = 2;

    /**
     * Creates the delegate; prefer {@link #createUI(javax.swing.JComponent)}.
     */
    public EveScrollBarUI() {
    }

    /**
     * Creates the UI delegate; called reflectively by {@link javax.swing.UIManager}.
     *
     * @param c component to create the delegate for, not null.
     * @return new non-null delegate.
     */
    public static ComponentUI createUI(JComponent c) {
        return new EveScrollBarUI();
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return new RangesZeroButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return new RangesZeroButton();
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        g.setColor(c.getBackground());
        g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
            return;
        }
        final Color fill;
        if (isDragging) {
            fill = color("ScrollBar.thumbPressed", EvePalette.ACCENT);
        } else if (isThumbRollover()) {
            fill = color("ScrollBar.thumbHover", EvePalette.BORDER_BRIGHT);
        } else {
            fill = color("ScrollBar.thumb", EvePalette.BORDER);
        }
        g.setColor(fill);
        if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
            g.fillRect(thumbBounds.x + THUMB_INSET, thumbBounds.y + 1,
                    thumbBounds.width - THUMB_INSET * 2, thumbBounds.height - 2);
        } else {
            g.fillRect(thumbBounds.x + 1, thumbBounds.y + THUMB_INSET,
                    thumbBounds.width - 2, thumbBounds.height - THUMB_INSET * 2);
        }
    }

    @Override
    protected Dimension getMinimumThumbSize() {
        final @Nullable Dimension size = (Dimension) UIManager.get("ScrollBar.minimumThumbSize");
        return size == null ? new Dimension(24, 24) : size;
    }

    private static Color color(String key, Color fallback) {
        final @Nullable Color color = UIManager.getColor(key);
        return color == null ? fallback : color;
    }

    /**
     * Invisible zero-size button replacing scroll arrows.
     */
    private static final class RangesZeroButton extends JButton {
        RangesZeroButton() {
            setFocusable(false);
            setRequestFocusEnabled(false);
            setBorder(null);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(0, 0);
        }

        @Override
        public Dimension getMinimumSize() {
            return getPreferredSize();
        }

        @Override
        public Dimension getMaximumSize() {
            return getPreferredSize();
        }

        @Override
        public void paint(Graphics g) {
        }
    }
}

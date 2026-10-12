package io.github.mjcro.toybox.eve.plaf;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSliderUI;

import io.github.mjcro.toybox.eve.EvePaint;
import io.github.mjcro.toybox.eve.EvePalette;

/**
 * Eve UI delegate for {@link javax.swing.JSlider}.
 *
 * <p>Thin 2px track with an accent-filled part up to the current value and a compact
 * pointer-shaped thumb aimed at the tick side. The thumb brightens on hover and while
 * dragged; no focus rectangle is painted. Supports both orientations and inversion.
 */
public class EveSliderUI extends BasicSliderUI {
    private static final int THUMB_SHORT = 9;
    private static final int THUMB_LONG = 14;
    private static final int TRACK = 2;
    private final HoverListener hoverListener = new HoverListener();
    private boolean thumbHover = false;

    /**
     * Creates the delegate for the given component.
     *
     * @param slider target component, not null.
     */
    public EveSliderUI(JSlider slider) {
        super(slider);
    }

    /**
     * Creates the UI delegate; called reflectively by {@link javax.swing.UIManager}.
     *
     * @param c component to create the delegate for, not null.
     * @return new non-null delegate.
     */
    public static ComponentUI createUI(JComponent c) {
        return new EveSliderUI((JSlider) c);
    }

    @Override
    protected void installListeners(JSlider slider) {
        super.installListeners(slider);
        slider.addMouseListener(hoverListener);
        slider.addMouseMotionListener(hoverListener);
    }

    @Override
    protected void uninstallListeners(JSlider slider) {
        slider.removeMouseListener(hoverListener);
        slider.removeMouseMotionListener(hoverListener);
        super.uninstallListeners(slider);
    }

    @Override
    protected Dimension getThumbSize() {
        if (slider.getOrientation() == JSlider.HORIZONTAL) {
            return new Dimension(THUMB_SHORT, THUMB_LONG);
        }
        return new Dimension(THUMB_LONG, THUMB_SHORT);
    }

    @Override
    protected int getTickLength() {
        return 6;
    }

    @Override
    public void paintFocus(Graphics g) {
    }

    @Override
    public void paintTrack(Graphics g) {
        final Rectangle t = trackRect;
        final boolean enabled = slider.isEnabled();
        g.setColor(enabled ? EvePalette.BORDER : EvePalette.GRID);
        if (slider.getOrientation() == JSlider.HORIZONTAL) {
            final int y = t.y + (t.height - TRACK) / 2;
            g.fillRect(t.x, y, t.width, TRACK);
            final int value = thumbRect.x + thumbRect.width / 2;
            final int from = drawInverted() ? value : t.x;
            final int to = drawInverted() ? t.x + t.width : value;
            g.setColor(enabled ? EvePalette.ACCENT_DARK : EvePalette.BORDER);
            g.fillRect(from, y, to - from, TRACK);
        } else {
            final int x = t.x + (t.width - TRACK) / 2;
            g.fillRect(x, t.y, TRACK, t.height);
            final int value = thumbRect.y + thumbRect.height / 2;
            // Vertical sliders grow upwards unless inverted.
            final int from = drawInverted() ? t.y : value;
            final int to = drawInverted() ? value : t.y + t.height;
            g.setColor(enabled ? EvePalette.ACCENT_DARK : EvePalette.BORDER);
            g.fillRect(x, from, TRACK, to - from);
        }
    }

    @Override
    public void paintThumb(Graphics g) {
        final Rectangle r = thumbRect;
        final boolean horizontal = slider.getOrientation() == JSlider.HORIZONTAL;
        final int corners;
        if (horizontal) {
            corners = EvePaint.BOTTOM_LEFT | EvePaint.BOTTOM_RIGHT;
        } else if (slider.getComponentOrientation().isLeftToRight()) {
            corners = EvePaint.TOP_RIGHT | EvePaint.BOTTOM_RIGHT;
        } else {
            corners = EvePaint.TOP_LEFT | EvePaint.BOTTOM_LEFT;
        }
        final Color fill;
        final Color line;
        if (!slider.isEnabled()) {
            fill = EvePalette.RAISED;
            line = EvePalette.BORDER;
        } else if (isDragging() || thumbHover) {
            fill = EvePalette.ACCENT;
            line = EvePalette.TEXT_BRIGHT;
        } else {
            fill = EvePalette.ACCENT_DARK;
            line = EvePalette.ACCENT;
        }
        final Graphics2D g2 = EvePaint.smooth(g);
        try {
            final double cut = 3.5;
            g2.setColor(fill);
            g2.fill(EvePaint.cutShape(r.x, r.y, r.width, r.height, cut, corners));
            g2.setColor(line);
            g2.draw(EvePaint.cutShape(r.x + 0.5, r.y + 0.5, r.width - 1, r.height - 1, cut, corners));
        } finally {
            g2.dispose();
        }
    }

    @Override
    protected void paintMinorTickForHorizSlider(Graphics g, Rectangle tickBounds, int x) {
        g.setColor(tickColor(false));
        g.drawLine(x, 0, x, tickBounds.height / 2 - 1);
    }

    @Override
    protected void paintMajorTickForHorizSlider(Graphics g, Rectangle tickBounds, int x) {
        g.setColor(tickColor(true));
        g.drawLine(x, 0, x, tickBounds.height - 2);
    }

    @Override
    protected void paintMinorTickForVertSlider(Graphics g, Rectangle tickBounds, int y) {
        g.setColor(tickColor(false));
        g.drawLine(0, y, tickBounds.width / 2 - 1, y);
    }

    @Override
    protected void paintMajorTickForVertSlider(Graphics g, Rectangle tickBounds, int y) {
        g.setColor(tickColor(true));
        g.drawLine(0, y, tickBounds.width - 2, y);
    }

    private Color tickColor(boolean major) {
        if (!slider.isEnabled()) {
            return EvePalette.GRID;
        }
        return major ? EvePalette.TEXT_DIM : EvePalette.BORDER_BRIGHT;
    }

    private void setThumbHover(boolean hover) {
        if (thumbHover != hover) {
            thumbHover = hover;
            slider.repaint(thumbRect);
        }
    }

    /**
     * Tracks whether the mouse pointer is over the thumb.
     */
    private final class HoverListener extends MouseAdapter {
        @Override
        public void mouseMoved(MouseEvent e) {
            setThumbHover(slider.isEnabled() && thumbRect.contains(e.getPoint()));
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            slider.repaint(thumbRect);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            setThumbHover(slider.isEnabled() && thumbRect.contains(e.getPoint()));
            slider.repaint();
        }

        @Override
        public void mouseExited(MouseEvent e) {
            setThumbHover(false);
        }
    }
}

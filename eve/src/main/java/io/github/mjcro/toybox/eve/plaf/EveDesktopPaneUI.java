package io.github.mjcro.toybox.eve.plaf;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicDesktopPaneUI;

import io.github.mjcro.toybox.eve.EvePalette;

/**
 * Eve UI delegate for {@link javax.swing.JDesktopPane}.
 *
 * <p>Paints the desktop background with a faint tactical grid and small
 * cross markers at every fourth intersection.
 */
public class EveDesktopPaneUI extends BasicDesktopPaneUI {
    private static final int STEP = 24;
    private static final Color GRID_LINE = EvePalette.mix(EvePalette.BACKGROUND, EvePalette.GRID, 0.45f);
    private static final Color GRID_MARK = EvePalette.mix(EvePalette.BACKGROUND, EvePalette.BORDER, 0.7f);

    /**
     * Creates the delegate; prefer {@link #createUI(javax.swing.JComponent)}.
     */
    public EveDesktopPaneUI() {
    }

    /**
     * Creates the UI delegate; called reflectively by {@link javax.swing.UIManager}.
     *
     * @param c component to create the delegate for, not null.
     * @return new non-null delegate.
     */
    public static ComponentUI createUI(JComponent c) {
        return new EveDesktopPaneUI();
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        if (!c.isOpaque()) {
            return;
        }
        final Rectangle clip = g.getClipBounds() != null ? g.getClipBounds() : new Rectangle(0, 0, c.getWidth(), c.getHeight());
        g.setColor(GRID_LINE);
        final int x0 = clip.x / STEP * STEP;
        final int y0 = clip.y / STEP * STEP;
        for (int x = x0; x <= clip.x + clip.width; x += STEP) {
            g.drawLine(x, clip.y, x, clip.y + clip.height);
        }
        for (int y = y0; y <= clip.y + clip.height; y += STEP) {
            g.drawLine(clip.x, y, clip.x + clip.width, y);
        }
        g.setColor(GRID_MARK);
        final int major = STEP * 4;
        for (int x = clip.x / major * major; x <= clip.x + clip.width; x += major) {
            for (int y = clip.y / major * major; y <= clip.y + clip.height; y += major) {
                g.drawLine(x - 3, y, x + 3, y);
                g.drawLine(x, y - 3, x, y + 3);
            }
        }
    }
}

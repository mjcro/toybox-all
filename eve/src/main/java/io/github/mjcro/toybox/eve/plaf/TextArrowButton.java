package io.github.mjcro.toybox.eve.plaf;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.plaf.UIResource;

import io.github.mjcro.toybox.eve.EveIcons;
import io.github.mjcro.toybox.eve.EvePalette;

/**
 * Flat chevron button used by combo boxes and spinners.
 *
 * <p>Paints itself completely (no button delegate painting): transparent body,
 * hover and pressed fills, a 1px separator on its leading edge and optionally on its
 * bottom edge, and a chevron that turns accent-colored on hover.
 */
final class TextArrowButton extends JButton implements UIResource {
    private final Icon icon;
    private final Icon hoverIcon;
    private final int width;
    private final boolean bottomLine;

    /**
     * Creates the button.
     *
     * @param direction  chevron direction, one of {@link javax.swing.SwingConstants#NORTH},
     *                   {@link javax.swing.SwingConstants#SOUTH}.
     * @param iconSize   chevron size in pixels.
     * @param width      preferred width in pixels; height is decided by the owner's layout.
     * @param bottomLine whether to paint a separator along the bottom edge (upper spinner button).
     */
    TextArrowButton(int direction, int iconSize, int width, boolean bottomLine) {
        this.icon = EveIcons.arrow(direction, iconSize, EvePalette.TEXT);
        this.hoverIcon = EveIcons.arrow(direction, iconSize, EvePalette.ACCENT);
        this.width = width;
        this.bottomLine = bottomLine;
        setBorder(BorderFactory.createEmptyBorder());
        setFocusable(false);
        setRequestFocusEnabled(false);
        setRolloverEnabled(true);
        setContentAreaFilled(false);
        setOpaque(false);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(width, 8);
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    protected void paintComponent(Graphics g) {
        final ButtonModel model = getModel();
        final int w = getWidth();
        final int h = getHeight();
        final boolean active = isEnabled() && (model.isRollover() || model.isPressed());
        if (isEnabled() && model.isPressed() && model.isArmed()) {
            fill(g, EvePalette.ACCENT_DARK, w, h);
        } else if (active) {
            fill(g, EvePalette.RAISED_HOVER, w, h);
        }
        g.setColor(isEnabled() ? EvePalette.BORDER : EvePalette.GRID);
        g.drawLine(0, 0, 0, h - 1);
        if (bottomLine) {
            g.drawLine(0, h - 1, w - 1, h - 1);
        }
        final Icon current = active ? hoverIcon : icon;
        current.paintIcon(this, g, (w - current.getIconWidth() + 1) / 2, (h - current.getIconHeight()) / 2);
    }

    @Override
    protected void paintBorder(Graphics g) {
        // Separators are painted by paintComponent.
    }

    private static void fill(Graphics g, Color color, int w, int h) {
        g.setColor(color);
        g.fillRect(0, 0, w, h);
    }
}

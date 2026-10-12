package io.github.mjcro.toybox.eve.plaf;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.plaf.UIResource;

import io.github.mjcro.toybox.eve.EveIcons;
import io.github.mjcro.toybox.eve.EvePaint;
import io.github.mjcro.toybox.eve.EvePalette;

/**
 * Icons of the menus group: selection marks of check box and radio button menu items
 * and blank spacers that keep menu item columns aligned.
 */
public final class MenusIcons {
    /** Width and height of menu item selection marks, in pixels. */
    public static final int MARK_SIZE = 12;

    private MenusIcons() {
    }

    /**
     * Returns the check box menu item mark: dim square frame, accent check mark when selected.
     *
     * @return non-null icon; expects an {@link AbstractButton} as paint target.
     */
    public static Icon checkMark() {
        return new MarkIcon(false);
    }

    /**
     * Returns the radio button menu item mark: dim diamond frame, accent diamond when selected.
     *
     * @return non-null icon; expects an {@link AbstractButton} as paint target.
     */
    public static Icon radioMark() {
        return new MarkIcon(true);
    }

    /**
     * Returns an invisible icon reserving space, used to align plain items with check items and submenus.
     *
     * @param width  icon width in pixels.
     * @param height icon height in pixels.
     * @return non-null icon that paints nothing.
     */
    public static Icon blank(int width, int height) {
        return new BlankIcon(width, height);
    }

    /**
     * Selection mark painted according to the button selected and enabled state.
     */
    private static final class MarkIcon implements Icon, UIResource {
        private final boolean radio;
        private final Icon check = EveIcons.check(MARK_SIZE, EvePalette.ACCENT);

        MarkIcon(boolean radio) {
            this.radio = radio;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            final boolean selected = c instanceof AbstractButton b && b.isSelected();
            final Graphics2D g2 = EvePaint.smooth(g);
            try {
                g2.translate(x, y);
                g2.setStroke(new BasicStroke(1f));
                if (radio) {
                    paintRadio(g2, c.isEnabled(), selected);
                } else {
                    g2.setColor(c.isEnabled() ? EvePalette.BORDER_BRIGHT : EvePalette.BORDER);
                    g2.drawRect(1, 1, MARK_SIZE - 3, MARK_SIZE - 3);
                }
            } finally {
                g2.dispose();
            }
            if (selected && !radio) {
                check.paintIcon(c, g, x, y);
            }
        }

        private static void paintRadio(Graphics2D g2, boolean enabled, boolean selected) {
            final double c = (MARK_SIZE - 1) / 2.0;
            g2.setColor(enabled ? EvePalette.BORDER_BRIGHT : EvePalette.BORDER);
            g2.draw(diamond(c, c - 1));
            if (selected) {
                g2.setColor(enabled ? EvePalette.ACCENT : EvePalette.TEXT_DIM);
                g2.fill(diamond(c, c - 3.5));
            }
        }

        private static Path2D diamond(double center, double radius) {
            final Path2D.Double p = new Path2D.Double();
            p.moveTo(center + 0.5, center + 0.5 - radius);
            p.lineTo(center + 0.5 + radius, center + 0.5);
            p.lineTo(center + 0.5, center + 0.5 + radius);
            p.lineTo(center + 0.5 - radius, center + 0.5);
            p.closePath();
            return p;
        }

        @Override
        public int getIconWidth() {
            return MARK_SIZE;
        }

        @Override
        public int getIconHeight() {
            return MARK_SIZE;
        }
    }

    /**
     * Space reserving icon.
     */
    private record BlankIcon(int width, int height) implements Icon, UIResource {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
        }

        @Override
        public int getIconWidth() {
            return width;
        }

        @Override
        public int getIconHeight() {
            return height;
        }
    }
}

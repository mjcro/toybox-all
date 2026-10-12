package io.github.mjcro.toybox.eve.plaf;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;

import javax.swing.Icon;
import javax.swing.plaf.UIResource;

import io.github.mjcro.toybox.eve.EvePaint;
import io.github.mjcro.toybox.eve.EvePalette;

/**
 * Vector icons of the data component group: tree folder and leaf glyphs.
 *
 * <p>Icons are minimal 1px outlines in the EVE style. When the target component is
 * disabled, they are painted with {@link EvePalette#GRID} instead of their own color.
 */
public final class DataIcons {
    /** Size of tree node icons in pixels. */
    public static final int SIZE = 14;

    private DataIcons() {
    }

    /**
     * Creates a folder icon used for tree branch nodes.
     *
     * @param open {@code true} for the expanded (open) variant, painted with accent color and tinted fill;
     *             {@code false} for the collapsed (closed) variant.
     * @return non-null icon of {@link #SIZE} pixels.
     */
    public static Icon folder(boolean open) {
        return new OutlineIcon(open ? EvePalette.ACCENT : EvePalette.TEXT_DIM) {
            @Override
            void paintShape(Graphics2D g2, Color color) {
                final Path2D.Double p = new Path2D.Double();
                p.moveTo(1.5, 3.5);
                p.lineTo(5.5, 3.5);
                p.lineTo(7, 5);
                p.lineTo(12.5, 5);
                p.lineTo(12.5, 10.5);
                p.lineTo(10.5, 12.5);
                p.lineTo(1.5, 12.5);
                p.closePath();
                if (open) {
                    g2.setColor(EvePalette.alpha(color, 50));
                    g2.fill(p);
                }
                g2.setColor(color);
                g2.draw(p);
                g2.draw(new Line2D.Double(1.5, 6.5, 12.5, 6.5));
            }
        };
    }

    /**
     * Creates a leaf icon used for tree leaf nodes: a small chamfered square chip.
     *
     * @return non-null icon of {@link #SIZE} pixels.
     */
    public static Icon leaf() {
        return new OutlineIcon(EvePalette.TEXT_DIM) {
            @Override
            void paintShape(Graphics2D g2, Color color) {
                g2.setColor(color);
                g2.draw(EvePaint.cutShape(3.5, 3.5, 7, 7, 2, EvePaint.TOP_LEFT | EvePaint.BOTTOM_RIGHT));
                g2.fillRect(6, 6, 2, 2);
            }
        };
    }

    /**
     * Base class for {@link #SIZE}-pixel single-color outline icons.
     */
    private abstract static class OutlineIcon implements Icon, UIResource {
        private final Color color;

        OutlineIcon(Color color) {
            this.color = color;
        }

        abstract void paintShape(Graphics2D g2, Color color);

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            final Graphics2D g2 = EvePaint.smooth(g);
            try {
                g2.translate(x, y);
                g2.setStroke(new BasicStroke(1f));
                paintShape(g2, c.isEnabled() ? color : EvePalette.GRID);
            } finally {
                g2.dispose();
            }
        }

        @Override
        public int getIconWidth() {
            return SIZE;
        }

        @Override
        public int getIconHeight() {
            return SIZE;
        }
    }
}

package io.github.mjcro.toybox.eve;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;

import javax.swing.Icon;
import javax.swing.SwingConstants;
import javax.swing.plaf.UIResource;

/**
 * Vector-painted generic icons shared across Eve UI delegates.
 *
 * <p>All icons are stateless, scale-free shapes painted with {@link EvePaint#smooth};
 * no image resources are used. When the target component is disabled, icons are
 * painted with {@link EvePalette#TEXT_DIM} instead of their own color.
 */
public final class EveIcons {
    private EveIcons() {
    }

    /**
     * Creates a chevron arrow icon.
     *
     * @param direction one of {@link SwingConstants#NORTH}, {@link SwingConstants#SOUTH},
     *                  {@link SwingConstants#EAST}, {@link SwingConstants#WEST}.
     * @param size      icon width and height in pixels.
     * @param color     arrow color, not null.
     * @return non-null icon.
     * @throws IllegalArgumentException if the direction is not supported.
     */
    public static Icon arrow(int direction, int size, Color color) {
        if (direction != SwingConstants.NORTH && direction != SwingConstants.SOUTH
                && direction != SwingConstants.EAST && direction != SwingConstants.WEST) {
            throw new IllegalArgumentException("Unsupported arrow direction " + direction);
        }
        return new ShapeIcon(size, color) {
            @Override
            void paintShape(Graphics2D g2, int s) {
                final double a = s * 0.3;
                final double c = s / 2.0;
                final Path2D.Double p = new Path2D.Double();
                switch (direction) {
                    case SwingConstants.NORTH -> {
                        p.moveTo(c - a, c + a / 2);
                        p.lineTo(c, c - a / 2);
                        p.lineTo(c + a, c + a / 2);
                    }
                    case SwingConstants.SOUTH -> {
                        p.moveTo(c - a, c - a / 2);
                        p.lineTo(c, c + a / 2);
                        p.lineTo(c + a, c - a / 2);
                    }
                    case SwingConstants.EAST -> {
                        p.moveTo(c - a / 2, c - a);
                        p.lineTo(c + a / 2, c);
                        p.lineTo(c - a / 2, c + a);
                    }
                    default -> {
                        p.moveTo(c + a / 2, c - a);
                        p.lineTo(c - a / 2, c);
                        p.lineTo(c + a / 2, c + a);
                    }
                }
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
                g2.draw(p);
            }
        };
    }

    /**
     * Creates a check mark icon.
     *
     * @param size  icon width and height in pixels.
     * @param color mark color, not null.
     * @return non-null icon.
     */
    public static Icon check(int size, Color color) {
        return new ShapeIcon(size, color) {
            @Override
            void paintShape(Graphics2D g2, int s) {
                final Path2D.Double p = new Path2D.Double();
                p.moveTo(s * 0.2, s * 0.52);
                p.lineTo(s * 0.42, s * 0.74);
                p.lineTo(s * 0.8, s * 0.28);
                g2.setStroke(new BasicStroke(Math.max(1.5f, s / 7f), BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
                g2.draw(p);
            }
        };
    }

    /**
     * Creates a filled square "dot" icon, used as the radio selection mark.
     * EVE favors squares and diamonds over circles.
     *
     * @param size  icon width and height in pixels.
     * @param color dot color, not null.
     * @return non-null icon.
     */
    public static Icon dot(int size, Color color) {
        return new ShapeIcon(size, color) {
            @Override
            void paintShape(Graphics2D g2, int s) {
                final double c = s / 2.0;
                final double r = s * 0.22;
                final Path2D.Double p = new Path2D.Double();
                p.moveTo(c, c - r * 1.4);
                p.lineTo(c + r * 1.4, c);
                p.lineTo(c, c + r * 1.4);
                p.lineTo(c - r * 1.4, c);
                p.closePath();
                g2.fill(p);
            }
        };
    }

    /**
     * Creates a diagonal cross icon, used for close buttons.
     *
     * @param size  icon width and height in pixels.
     * @param color cross color, not null.
     * @return non-null icon.
     */
    public static Icon cross(int size, Color color) {
        return new ShapeIcon(size, color) {
            @Override
            void paintShape(Graphics2D g2, int s) {
                final double a = s * 0.28;
                final double b = s - a;
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new Line2D.Double(a, a, b, b));
                g2.draw(new Line2D.Double(a, b, b, a));
            }
        };
    }

    /**
     * Creates an outlined square icon with a cut top-left corner; generic placeholder glyph.
     *
     * @param size  icon width and height in pixels.
     * @param color outline color, not null.
     * @return non-null icon.
     */
    public static Icon frame(int size, Color color) {
        return new ShapeIcon(size, color) {
            @Override
            void paintShape(Graphics2D g2, int s) {
                g2.setStroke(new BasicStroke(1f));
                g2.draw(EvePaint.cutShape(1.5, 1.5, s - 3, s - 3, Math.max(2, s / 4.0), EvePaint.TOP_LEFT));
            }
        };
    }

    /**
     * Base class for square vector icons painted with a single color.
     */
    private abstract static class ShapeIcon implements Icon, UIResource {
        private final int size;
        private final Color color;

        ShapeIcon(int size, Color color) {
            this.size = size;
            this.color = color;
        }

        abstract void paintShape(Graphics2D g2, int s);

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            final Graphics2D g2 = EvePaint.smooth(g);
            try {
                g2.translate(x, y);
                g2.setColor(c.isEnabled() ? color : EvePalette.TEXT_DIM);
                paintShape(g2, size);
            } finally {
                g2.dispose();
            }
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }
    }
}

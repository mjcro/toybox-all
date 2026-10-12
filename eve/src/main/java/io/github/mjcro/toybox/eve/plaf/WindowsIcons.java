package io.github.mjcro.toybox.eve.plaf;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

import javax.swing.Icon;
import javax.swing.plaf.UIResource;

import io.github.mjcro.toybox.eve.EveFonts;
import io.github.mjcro.toybox.eve.EvePaint;
import io.github.mjcro.toybox.eve.EvePalette;

/**
 * Vector icons of the windows component group: window controls, option pane
 * message icons and file chooser icons.
 *
 * <p>Every icon is drawn on a 16x16 unit grid scaled to the requested pixel size;
 * icons painted on a disabled component use {@link EvePalette#TEXT_DIM}.
 */
public final class WindowsIcons {
    private static final float UNIT = 16f;

    private WindowsIcons() {
    }

    /**
     * Creates the "minimize" (iconify) window control glyph.
     *
     * @param size  icon size in pixels.
     * @param color glyph color, not null.
     * @return non-null icon.
     */
    public static Icon minimize(int size, Color color) {
        return new VectorIcon(size, color, g -> g.draw(new Line2D.Double(3.5, 11.5, 12.5, 11.5)));
    }

    /**
     * Creates the "maximize" window control glyph.
     *
     * @param size  icon size in pixels.
     * @param color glyph color, not null.
     * @return non-null icon.
     */
    public static Icon maximize(int size, Color color) {
        return new VectorIcon(size, color, g -> {
            g.draw(EvePaint.cutShape(3.5, 3.5, 9, 9, 2, EvePaint.TOP_LEFT));
            g.draw(new Line2D.Double(5.5, 4.5, 12.5, 4.5));
        });
    }

    /**
     * Creates the "restore" window control glyph: two overlapping frames.
     *
     * @param size  icon size in pixels.
     * @param color glyph color, not null.
     * @return non-null icon.
     */
    public static Icon restore(int size, Color color) {
        return new VectorIcon(size, color, g -> {
            g.draw(new Rectangle2D.Double(3.5, 6.5, 7, 6));
            final Path2D.Double back = new Path2D.Double();
            back.moveTo(5.5, 6.5);
            back.lineTo(5.5, 3.5);
            back.lineTo(12.5, 3.5);
            back.lineTo(12.5, 9.5);
            back.lineTo(10.5, 9.5);
            g.draw(back);
        });
    }

    /**
     * Creates the "close" window control glyph.
     *
     * @param size  icon size in pixels.
     * @param color glyph color, not null.
     * @return non-null icon.
     */
    public static Icon close(int size, Color color) {
        return new VectorIcon(size, color, g -> {
            g.draw(new Line2D.Double(4, 4, 12, 12));
            g.draw(new Line2D.Double(4, 12, 12, 4));
        });
    }

    /**
     * Creates the option pane "information" icon: chamfered frame with an "i".
     *
     * @param size icon size in pixels.
     * @return non-null icon.
     */
    public static Icon information(int size) {
        return new VectorIcon(size, EvePalette.ACCENT, g -> {
            badge(g, EvePalette.ACCENT);
            g.fill(new Rectangle2D.Double(7.25, 4, 1.5, 1.5));
            g.fill(new Rectangle2D.Double(7.25, 6.75, 1.5, 5.25));
        });
    }

    /**
     * Creates the option pane "question" icon: chamfered frame with a "?".
     *
     * @param size icon size in pixels.
     * @return non-null icon.
     */
    public static Icon question(int size) {
        return new VectorIcon(size, EvePalette.ACCENT, g -> {
            badge(g, EvePalette.ACCENT);
            glyph(g, "?");
        });
    }

    /**
     * Creates the option pane "warning" icon: amber triangle with an "!".
     *
     * @param size icon size in pixels.
     * @return non-null icon.
     */
    public static Icon warning(int size) {
        return new VectorIcon(size, EvePalette.WARNING, g -> {
            final Path2D.Double triangle = new Path2D.Double();
            triangle.moveTo(8, 1.5);
            triangle.lineTo(15, 14.5);
            triangle.lineTo(1, 14.5);
            triangle.closePath();
            final Color line = g.getColor();
            g.setColor(EvePalette.alpha(line, 40));
            g.fill(triangle);
            g.setColor(line);
            g.draw(triangle);
            g.fill(new Rectangle2D.Double(7.3, 5.5, 1.4, 5));
            g.fill(new Rectangle2D.Double(7.3, 11.6, 1.4, 1.4));
        });
    }

    /**
     * Creates the option pane "error" icon: red octagon with a cross.
     *
     * @param size icon size in pixels.
     * @return non-null icon.
     */
    public static Icon error(int size) {
        return new VectorIcon(size, EvePalette.DANGER, g -> {
            final Path2D.Double octagon = new Path2D.Double();
            octagon.moveTo(5, 1);
            octagon.lineTo(11, 1);
            octagon.lineTo(15, 5);
            octagon.lineTo(15, 11);
            octagon.lineTo(11, 15);
            octagon.lineTo(5, 15);
            octagon.lineTo(1, 11);
            octagon.lineTo(1, 5);
            octagon.closePath();
            final Color line = g.getColor();
            g.setColor(EvePalette.alpha(line, 40));
            g.fill(octagon);
            g.setColor(line);
            g.draw(octagon);
            g.setStroke(new BasicStroke(1.4f));
            g.draw(new Line2D.Double(5.5, 5.5, 10.5, 10.5));
            g.draw(new Line2D.Double(5.5, 10.5, 10.5, 5.5));
        });
    }

    /**
     * Creates the folder icon.
     *
     * @param size  icon size in pixels.
     * @param color outline color, not null.
     * @return non-null icon.
     */
    public static Icon folder(int size, Color color) {
        return new VectorIcon(size, color, WindowsIcons::folderShape);
    }

    /**
     * Creates the generic file icon: sheet with a folded corner.
     *
     * @param size  icon size in pixels.
     * @param color outline color, not null.
     * @return non-null icon.
     */
    public static Icon file(int size, Color color) {
        return new VectorIcon(size, color, g -> {
            final Path2D.Double sheet = new Path2D.Double();
            sheet.moveTo(3.5, 1.5);
            sheet.lineTo(9.5, 1.5);
            sheet.lineTo(12.5, 4.5);
            sheet.lineTo(12.5, 14.5);
            sheet.lineTo(3.5, 14.5);
            sheet.closePath();
            g.draw(sheet);
            final Path2D.Double fold = new Path2D.Double();
            fold.moveTo(9.5, 1.5);
            fold.lineTo(9.5, 4.5);
            fold.lineTo(12.5, 4.5);
            g.draw(fold);
            g.draw(new Line2D.Double(5.5, 8.5, 10.5, 8.5));
            g.draw(new Line2D.Double(5.5, 11.5, 10.5, 11.5));
        });
    }

    /**
     * Creates the "up one level" icon: folder with an up arrow.
     *
     * @param size  icon size in pixels.
     * @param color outline color, not null.
     * @return non-null icon.
     */
    public static Icon upFolder(int size, Color color) {
        return new VectorIcon(size, color, g -> {
            folderShape(g);
            g.setColor(EvePalette.TEXT_BRIGHT);
            g.draw(new Line2D.Double(8, 12, 8, 7));
            final Path2D.Double head = new Path2D.Double();
            head.moveTo(5.5, 9.5);
            head.lineTo(8, 7);
            head.lineTo(10.5, 9.5);
            g.draw(head);
        });
    }

    /**
     * Creates the "new folder" icon: folder with a plus sign.
     *
     * @param size  icon size in pixels.
     * @param color outline color, not null.
     * @return non-null icon.
     */
    public static Icon newFolder(int size, Color color) {
        return new VectorIcon(size, color, g -> {
            folderShape(g);
            g.setColor(EvePalette.TEXT_BRIGHT);
            g.draw(new Line2D.Double(8, 7, 8, 12));
            g.draw(new Line2D.Double(5.5, 9.5, 10.5, 9.5));
        });
    }

    /**
     * Creates the "home folder" icon: house outline.
     *
     * @param size  icon size in pixels.
     * @param color outline color, not null.
     * @return non-null icon.
     */
    public static Icon home(int size, Color color) {
        return new VectorIcon(size, color, g -> {
            final Path2D.Double house = new Path2D.Double();
            house.moveTo(1.5, 8);
            house.lineTo(8, 2);
            house.lineTo(14.5, 8);
            g.draw(house);
            final Path2D.Double body = new Path2D.Double();
            body.moveTo(3.5, 6.5);
            body.lineTo(3.5, 14.5);
            body.lineTo(12.5, 14.5);
            body.lineTo(12.5, 6.5);
            g.draw(body);
            g.draw(new Rectangle2D.Double(6.5, 9.5, 3, 5));
        });
    }

    /**
     * Creates the "list view" icon: rows of bullets and lines.
     *
     * @param size  icon size in pixels.
     * @param color glyph color, not null.
     * @return non-null icon.
     */
    public static Icon listView(int size, Color color) {
        return new VectorIcon(size, color, g -> {
            for (int row = 0; row < 3; row++) {
                final double y = 3.5 + row * 4.5;
                g.fill(new Rectangle2D.Double(2, y - 1, 2, 2));
                g.draw(new Line2D.Double(6, y, 14, y));
            }
        });
    }

    /**
     * Creates the "details view" icon: table with header and columns.
     *
     * @param size  icon size in pixels.
     * @param color glyph color, not null.
     * @return non-null icon.
     */
    public static Icon detailsView(int size, Color color) {
        return new VectorIcon(size, color, g -> {
            g.draw(new Rectangle2D.Double(1.5, 2.5, 13, 11));
            g.fill(new Rectangle2D.Double(1.5, 2.5, 13, 2.5));
            g.draw(new Line2D.Double(1.5, 8.5, 14.5, 8.5));
            g.draw(new Line2D.Double(6.5, 5, 6.5, 13.5));
        });
    }

    /**
     * Creates the "computer" icon: monitor on a stand.
     *
     * @param size  icon size in pixels.
     * @param color outline color, not null.
     * @return non-null icon.
     */
    public static Icon computer(int size, Color color) {
        return new VectorIcon(size, color, g -> {
            g.draw(EvePaint.cutShape(1.5, 2.5, 13, 9, 2, EvePaint.TOP_LEFT));
            g.draw(new Line2D.Double(8, 11.5, 8, 13.5));
            g.draw(new Line2D.Double(4.5, 13.5, 11.5, 13.5));
        });
    }

    /**
     * Creates the "hard drive" icon.
     *
     * @param size  icon size in pixels.
     * @param color outline color, not null.
     * @return non-null icon.
     */
    public static Icon hardDrive(int size, Color color) {
        return new VectorIcon(size, color, g -> {
            g.draw(EvePaint.cutShape(1.5, 5.5, 13, 6, 2, EvePaint.DIAGONAL));
            g.fill(new Rectangle2D.Double(11, 8, 1.5, 1.5));
            g.draw(new Line2D.Double(3.5, 8.5, 8.5, 8.5));
        });
    }

    /**
     * Creates the "floppy drive" icon.
     *
     * @param size  icon size in pixels.
     * @param color outline color, not null.
     * @return non-null icon.
     */
    public static Icon floppy(int size, Color color) {
        return new VectorIcon(size, color, g -> {
            g.draw(EvePaint.cutShape(2.5, 2.5, 11, 11, 2.5, EvePaint.TOP_RIGHT));
            g.draw(new Rectangle2D.Double(5.5, 2.5, 4, 3));
            g.draw(new Rectangle2D.Double(4.5, 8.5, 7, 5));
        });
    }

    private static void folderShape(Graphics2D g) {
        final Path2D.Double folder = new Path2D.Double();
        folder.moveTo(1.5, 3.5);
        folder.lineTo(6, 3.5);
        folder.lineTo(7.5, 5);
        folder.lineTo(14.5, 5);
        folder.lineTo(14.5, 13.5);
        folder.lineTo(1.5, 13.5);
        folder.closePath();
        final Color line = g.getColor();
        g.setColor(EvePalette.alpha(line, 50));
        g.fill(folder);
        g.setColor(line);
        g.draw(folder);
    }

    private static void badge(Graphics2D g, Color color) {
        final Shape shape = EvePaint.cutShape(1, 1, 14, 14, 3, EvePaint.DIAGONAL);
        g.setColor(EvePalette.alpha(color, 40));
        g.fill(shape);
        g.setColor(color);
        g.draw(shape);
    }

    private static void glyph(Graphics2D g, String text) {
        final Font font = EveFonts.sans(Font.BOLD, 11);
        g.setFont(font);
        final FontMetrics fm = g.getFontMetrics();
        final float x = (UNIT - fm.stringWidth(text)) / 2f;
        final float y = (UNIT - fm.getHeight()) / 2f + fm.getAscent();
        g.drawString(text, x, y);
    }

    /**
     * Painting routine on the 16x16 unit grid.
     */
    @FunctionalInterface
    private interface Painter {
        void paint(Graphics2D g);
    }

    /**
     * Scalable single-color icon backed by a {@link Painter}.
     */
    private static final class VectorIcon implements Icon, UIResource {
        private final int size;
        private final Color color;
        private final Painter painter;

        VectorIcon(int size, Color color, Painter painter) {
            this.size = size;
            this.color = color;
            this.painter = painter;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            final Graphics2D g2 = EvePaint.smooth(g);
            try {
                final double scale = size / UNIT;
                g2.translate(x, y);
                g2.scale(scale, scale);
                g2.setStroke(new BasicStroke((float) Math.max(0.75, 1.0 / scale), BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
                g2.setColor(c.isEnabled() ? color : EvePalette.TEXT_DIM);
                painter.paint(g2);
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

package io.github.mjcro.toybox.eve.plaf;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.plaf.UIResource;

import io.github.mjcro.toybox.eve.EveIcons;
import io.github.mjcro.toybox.eve.EvePalette;

/**
 * State-aware selection icons of check boxes and radio buttons.
 *
 * <p>Icons read the {@link ButtonModel} of the painted {@link AbstractButton}: frame turns
 * brighter on hover and {@link EvePalette#ACCENT} when selected, pressed or focused;
 * disabled buttons are dimmed. Check boxes are squares, radio buttons are diamonds.
 */
public final class ButtonsIcons {
    /** Width and height of check box and radio button icons, in pixels. */
    public static final int SIZE = 13;

    private ButtonsIcons() {
    }

    /**
     * Returns the check box icon: square frame with an accent check mark when selected.
     *
     * @return non-null icon of {@link #SIZE} pixels.
     */
    public static Icon checkBox() {
        return new SelectionIcon(false);
    }

    /**
     * Returns the radio button icon: diamond frame with an accent diamond dot when selected.
     *
     * @return non-null icon of {@link #SIZE} pixels.
     */
    public static Icon radioButton() {
        return new SelectionIcon(true);
    }

    /**
     * Square or diamond selection indicator.
     */
    private static final class SelectionIcon implements Icon, UIResource {
        private final boolean diamond;
        private final Icon check = EveIcons.check(SIZE, EvePalette.ACCENT);
        private final Icon dot = EveIcons.dot(SIZE, EvePalette.ACCENT);

        SelectionIcon(boolean diamond) {
            this.diamond = diamond;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            final ButtonModel model = c instanceof AbstractButton b ? b.getModel() : null;
            final boolean enabled = c.isEnabled();
            final boolean selected = model != null && model.isSelected();
            final boolean pressed = model != null && model.isPressed() && model.isArmed();
            final boolean rollover = model != null && model.isRollover();

            final Color fill;
            final Color line;
            if (!enabled) {
                fill = EvePalette.PANEL;
                line = EvePalette.BORDER;
            } else {
                fill = pressed ? EvePalette.RAISED_HOVER : EvePalette.SURFACE;
                if (selected || pressed || c.isFocusOwner()) {
                    line = EvePalette.ACCENT;
                } else if (rollover) {
                    line = EvePalette.TEXT_DIM;
                } else {
                    line = EvePalette.BORDER_BRIGHT;
                }
            }

            final Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.translate(x, y);
                if (diamond) {
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                }
                final Shape outline = diamond ? diamond() : new Rectangle2D.Double(0, 0, SIZE - 1, SIZE - 1);
                g2.setColor(fill);
                g2.fill(outline);
                g2.setStroke(new BasicStroke(1f));
                g2.setColor(line);
                g2.draw(outline);
            } finally {
                g2.dispose();
            }
            if (selected) {
                (diamond ? dot : check).paintIcon(c, g, x, y);
            }
        }

        private static Shape diamond() {
            final double c = (SIZE - 1) / 2.0;
            final Path2D.Double p = new Path2D.Double();
            p.moveTo(c, 0);
            p.lineTo(SIZE - 1, c);
            p.lineTo(c, SIZE - 1);
            p.lineTo(0, c);
            p.closePath();
            return p;
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

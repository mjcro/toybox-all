package io.github.mjcro.toybox.eve.plaf;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;

import org.jspecify.annotations.Nullable;

import io.github.mjcro.toybox.eve.EvePalette;

/**
 * Shared behavior of Eve text component delegates: focus-driven repaint and placeholder text.
 *
 * <p>Placeholder usage:
 * <pre>{@code
 * field.putClientProperty(TextSupport.PLACEHOLDER, "Search...");
 * }</pre>
 */
public final class TextSupport {
    /**
     * Client property key of the placeholder text: a {@link String} painted with
     * {@link EvePalette#TEXT_DIM} while the component is empty and not focused.
     * Supported by text fields, formatted text fields, password fields and text areas.
     */
    public static final String PLACEHOLDER = "Eve.placeholder";

    private static final Listener LISTENER = new Listener();

    private TextSupport() {
    }

    /**
     * Installs listeners that repaint the component on focus and placeholder changes.
     *
     * @param c text component, not null.
     */
    static void install(JTextComponent c) {
        c.addFocusListener(LISTENER);
        c.addPropertyChangeListener(PLACEHOLDER, LISTENER);
    }

    /**
     * Removes listeners added by {@link #install(JTextComponent)}.
     *
     * @param c text component, not null.
     */
    static void uninstall(JTextComponent c) {
        c.removeFocusListener(LISTENER);
        c.removePropertyChangeListener(PLACEHOLDER, LISTENER);
    }

    /**
     * Paints the placeholder text when the component is empty, unfocused and has the {@link #PLACEHOLDER} property.
     *
     * @param g graphics of the component, not null.
     * @param c text component, not null.
     */
    static void paintPlaceholder(Graphics g, JTextComponent c) {
        if (!(c.getClientProperty(PLACEHOLDER) instanceof String text) || text.isEmpty()
                || c.getDocument().getLength() > 0 || c.isFocusOwner()) {
            return;
        }
        final @Nullable Rectangle2D origin = originOf(c);
        if (origin == null) {
            return;
        }
        final Graphics2D g2 = (Graphics2D) g.create();
        try {
            final @Nullable Object aa = UIManager.get(RenderingHints.KEY_TEXT_ANTIALIASING);
            if (aa != null) {
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, aa);
            }
            g2.setFont(c.getFont());
            g2.setColor(EvePalette.TEXT_DIM);
            final FontMetrics fm = g2.getFontMetrics();
            g2.drawString(text, (float) (origin.getX() - alignmentShift(c, fm.stringWidth(text))),
                    (float) origin.getY() + fm.getAscent());
        } finally {
            g2.dispose();
        }
    }

    /**
     * Empty right- or center-aligned fields report the caret origin at the aligned position,
     * so the placeholder must be shifted left by its full or half width.
     */
    private static int alignmentShift(JTextComponent c, int width) {
        if (!(c instanceof JTextField field)) {
            return 0;
        }
        final boolean ltr = field.getComponentOrientation().isLeftToRight();
        return switch (field.getHorizontalAlignment()) {
            case SwingConstants.RIGHT -> width;
            case SwingConstants.CENTER -> width / 2;
            case SwingConstants.TRAILING -> ltr ? width : 0;
            case SwingConstants.LEADING -> ltr ? 0 : width;
            default -> 0;
        };
    }

    private static @Nullable Rectangle2D originOf(JTextComponent c) {
        try {
            return c.getUI().modelToView2D(c, 0, Position.Bias.Forward);
        } catch (BadLocationException e) {
            return null;
        }
    }

    /**
     * Stateless listener shared by all text components.
     */
    private static final class Listener implements FocusListener, PropertyChangeListener {
        @Override
        public void focusGained(FocusEvent e) {
            e.getComponent().repaint();
        }

        @Override
        public void focusLost(FocusEvent e) {
            e.getComponent().repaint();
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getSource() instanceof JTextComponent c) {
                c.repaint();
            }
        }
    }
}

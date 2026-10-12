package io.github.mjcro.toybox.eve.plaf;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSeparatorUI;

/**
 * Eve UI delegate for {@link javax.swing.JSeparator}.
 *
 * <p>Paints a single 1px line in the separator foreground, centered across the component
 * thickness; preferred thickness is 3px (line plus 1px breathing room on each side).
 */
public class EveSeparatorUI extends BasicSeparatorUI {
    private static final int THICKNESS = 3;

    /**
     * Creates the delegate; prefer {@link #createUI(javax.swing.JComponent)}.
     */
    public EveSeparatorUI() {
    }

    /**
     * Creates the UI delegate; called reflectively by {@link javax.swing.UIManager}.
     *
     * @param c component to create the delegate for, not null.
     * @return new non-null delegate.
     */
    public static ComponentUI createUI(JComponent c) {
        return new EveSeparatorUI();
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        final JSeparator separator = (JSeparator) c;
        g.setColor(c.getForeground());
        if (separator.getOrientation() == SwingConstants.VERTICAL) {
            g.fillRect(c.getWidth() / 2, 0, 1, c.getHeight());
        } else {
            g.fillRect(0, c.getHeight() / 2, c.getWidth(), 1);
        }
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        if (((JSeparator) c).getOrientation() == SwingConstants.VERTICAL) {
            return new Dimension(THICKNESS, 0);
        }
        return new Dimension(0, THICKNESS);
    }
}

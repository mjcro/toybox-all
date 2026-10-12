package io.github.mjcro.toybox.eve.plaf;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicRadioButtonUI;

/**
 * Eve UI delegate for {@link javax.swing.JRadioButton}.
 *
 * <p>The selection icon ({@link ButtonsIcons}) is a diamond with an accent diamond dot;
 * focus is shown by the accent icon frame instead of a focus rectangle.
 */
public class EveRadioButtonUI extends BasicRadioButtonUI {
    /**
     * Creates the delegate; prefer {@link #createUI(javax.swing.JComponent)}.
     */
    public EveRadioButtonUI() {
    }

    /**
     * Creates the UI delegate; called reflectively by {@link javax.swing.UIManager}.
     *
     * @param c component to create the delegate for, not null.
     * @return new non-null delegate.
     */
    public static ComponentUI createUI(JComponent c) {
        return new EveRadioButtonUI();
    }

    @Override
    protected void installDefaults(AbstractButton b) {
        super.installDefaults(b);
        LookAndFeel.installProperty(b, "rolloverEnabled", Boolean.TRUE);
    }

    @Override
    protected void paintFocus(Graphics g, Rectangle textRect, Dimension size) {
        // Focus is shown by the accent frame of the icon.
    }

    @Override
    protected void paintText(Graphics g, JComponent c, Rectangle textRect, String text) {
        ButtonsPainter.paintText(g, (AbstractButton) c, textRect, text, getTextShiftOffset());
    }
}

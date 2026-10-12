package io.github.mjcro.toybox.eve.plaf;

import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * Eve UI delegate for {@link javax.swing.JButton}.
 *
 * <p>Paints a chamfered, non-opaque button: gradient fill by model state (hover, pressed,
 * default), accent frame on focus instead of a focus rectangle, dimmed text when disabled.
 */
public class EveButtonUI extends BasicButtonUI {
    /**
     * Creates the delegate; prefer {@link #createUI(javax.swing.JComponent)}.
     */
    public EveButtonUI() {
    }

    /**
     * Creates the UI delegate; called reflectively by {@link javax.swing.UIManager}.
     *
     * @param c component to create the delegate for, not null.
     * @return new non-null delegate.
     */
    public static ComponentUI createUI(JComponent c) {
        return new EveButtonUI();
    }

    @Override
    protected void installDefaults(AbstractButton b) {
        super.installDefaults(b);
        LookAndFeel.installProperty(b, "opaque", Boolean.FALSE);
        LookAndFeel.installProperty(b, "rolloverEnabled", Boolean.TRUE);
    }

    @Override
    public void update(Graphics g, JComponent c) {
        ButtonsPainter.paintBackground(g, (AbstractButton) c);
        paint(g, c);
    }

    @Override
    protected void paintButtonPressed(Graphics g, AbstractButton b) {
        // Pressed state is part of the background fill, see update().
    }

    @Override
    protected void paintFocus(Graphics g, AbstractButton b, Rectangle viewRect, Rectangle textRect, Rectangle iconRect) {
        // Focus is shown by the accent frame of the border.
    }

    @Override
    protected void paintText(Graphics g, JComponent c, Rectangle textRect, String text) {
        ButtonsPainter.paintText(g, (AbstractButton) c, textRect, text, getTextShiftOffset());
    }
}

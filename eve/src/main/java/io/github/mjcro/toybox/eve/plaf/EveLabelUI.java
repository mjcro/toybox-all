package io.github.mjcro.toybox.eve.plaf;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.basic.BasicLabelUI;

import io.github.mjcro.toybox.eve.EvePalette;

/**
 * Eve UI delegate for {@link javax.swing.JLabel}.
 *
 * <p>Disabled text is painted flat with {@code Label.disabledForeground} instead of the
 * etched Basic look.
 */
public class EveLabelUI extends BasicLabelUI {
    /**
     * Creates the delegate; prefer {@link #createUI(javax.swing.JComponent)}.
     */
    public EveLabelUI() {
    }

    /**
     * Creates the UI delegate; called reflectively by {@link javax.swing.UIManager}.
     *
     * @param c component to create the delegate for, not null.
     * @return new non-null delegate.
     */
    public static ComponentUI createUI(JComponent c) {
        return new EveLabelUI();
    }

    @Override
    protected void paintDisabledText(JLabel l, Graphics g, String s, int textX, int textY) {
        final Color color = UIManager.getColor("Label.disabledForeground");
        g.setColor(color != null ? color : EvePalette.TEXT_DIM);
        BasicGraphicsUtils.drawStringUnderlineCharAt(l, (Graphics2D) g, s, l.getDisplayedMnemonicIndex(), textX, textY);
    }
}

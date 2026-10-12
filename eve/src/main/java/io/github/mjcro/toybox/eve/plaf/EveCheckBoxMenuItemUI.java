package io.github.mjcro.toybox.eve.plaf;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicCheckBoxMenuItemUI;

/**
 * Eve UI delegate for {@link javax.swing.JCheckBoxMenuItem}: flat item with accent marker when armed.
 */
public class EveCheckBoxMenuItemUI extends BasicCheckBoxMenuItemUI {
    /**
     * Creates the delegate; prefer {@link #createUI(javax.swing.JComponent)}.
     */
    public EveCheckBoxMenuItemUI() {
    }

    /**
     * Creates the UI delegate; called reflectively by {@link javax.swing.UIManager}.
     *
     * @param c component to create the delegate for, not null.
     * @return new non-null delegate.
     */
    public static ComponentUI createUI(JComponent c) {
        return new EveCheckBoxMenuItemUI();
    }

    @Override
    protected void paintBackground(Graphics g, JMenuItem menuItem, Color bgColor) {
        MenusPainter.paintBackground(g, menuItem);
    }

    @Override
    protected void paintText(Graphics g, JMenuItem menuItem, Rectangle textRect, String text) {
        MenusPainter.paintText(g, menuItem, textRect, text, selectionForeground, disabledForeground);
    }
}

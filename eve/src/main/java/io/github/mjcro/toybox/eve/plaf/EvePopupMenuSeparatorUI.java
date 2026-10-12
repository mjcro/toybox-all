package io.github.mjcro.toybox.eve.plaf;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPopupMenuSeparatorUI;

import io.github.mjcro.toybox.eve.EvePalette;

/**
 * Eve UI delegate for {@link javax.swing.JPopupMenu.Separator}: single 1px line inset from the edges.
 */
public class EvePopupMenuSeparatorUI extends BasicPopupMenuSeparatorUI {
    private static final int PADDING = 3;
    private static final int INSET = 4;

    /**
     * Creates the delegate; prefer {@link #createUI(javax.swing.JComponent)}.
     */
    public EvePopupMenuSeparatorUI() {
    }

    /**
     * Creates the UI delegate; called reflectively by {@link javax.swing.UIManager}.
     *
     * @param c component to create the delegate for, not null.
     * @return new non-null delegate.
     */
    public static ComponentUI createUI(JComponent c) {
        return new EvePopupMenuSeparatorUI();
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        g.setColor(EvePalette.BORDER);
        g.fillRect(INSET, c.getHeight() / 2, c.getWidth() - INSET * 2, 1);
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        return new Dimension(0, PADDING * 2 + 1);
    }
}

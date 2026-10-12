package io.github.mjcro.toybox.eve.plaf;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuUI;

/**
 * Eve UI delegate for {@link javax.swing.JMenu}.
 *
 * <p>Top-level menus get a hover highlight and an accent underline while open;
 * submenus look like regular menu items with a chevron arrow.
 */
public class EveMenuUI extends BasicMenuUI {
    private final MouseAdapter rolloverHandler = new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            setRollover(true);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            setRollover(false);
        }
    };

    /**
     * Creates the delegate; prefer {@link #createUI(javax.swing.JComponent)}.
     */
    public EveMenuUI() {
    }

    /**
     * Creates the UI delegate; called reflectively by {@link javax.swing.UIManager}.
     *
     * @param c component to create the delegate for, not null.
     * @return new non-null delegate.
     */
    public static ComponentUI createUI(JComponent c) {
        return new EveMenuUI();
    }

    @Override
    protected void installListeners() {
        super.installListeners();
        menuItem.addMouseListener(rolloverHandler);
    }

    @Override
    protected void uninstallListeners() {
        menuItem.removeMouseListener(rolloverHandler);
        super.uninstallListeners();
    }

    @Override
    protected void paintBackground(Graphics g, JMenuItem menuItem, Color bgColor) {
        MenusPainter.paintBackground(g, menuItem);
    }

    @Override
    protected void paintText(Graphics g, JMenuItem menuItem, Rectangle textRect, String text) {
        MenusPainter.paintText(g, menuItem, textRect, text, selectionForeground, disabledForeground);
    }

    private void setRollover(boolean rollover) {
        if (menuItem instanceof JMenu menu && menu.isTopLevelMenu() && menu.isEnabled()) {
            menu.getModel().setRollover(rollover);
            menu.repaint();
        }
    }
}

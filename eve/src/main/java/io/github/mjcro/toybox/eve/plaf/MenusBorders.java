package io.github.mjcro.toybox.eve.plaf;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;

import io.github.mjcro.toybox.eve.EvePaint;
import io.github.mjcro.toybox.eve.EvePalette;

/**
 * Borders of the menus group: menu bar, popup menu, toolbar and toolbar buttons.
 */
public final class MenusBorders {
    private MenusBorders() {
    }

    /**
     * Returns the menu bar border: 1px {@link EvePalette#BORDER} bottom line.
     *
     * @return non-null border.
     */
    public static Border menuBar() {
        return new MenuBarBorder();
    }

    /**
     * Returns the popup menu border: 1px {@link EvePalette#BORDER_BRIGHT} frame with an
     * accent corner bracket and 1px inner padding.
     *
     * @return non-null border.
     */
    public static Border popup() {
        return new PopupBorder();
    }

    /**
     * Returns the toolbar border: 1px separating line (bottom for horizontal, right for vertical
     * toolbars) plus a dotted grip when the toolbar is floatable.
     *
     * @return non-null border.
     */
    public static Border toolBar() {
        return new ToolBarBorder();
    }

    /**
     * Returns the rollover border of toolbar buttons: invisible until hovered, pressed or selected.
     * Fills are painted by {@link EveToolBarUI}, which owns the area behind the button.
     *
     * @return non-null border.
     */
    public static Border toolBarButton() {
        return new ToolBarButtonBorder();
    }

    /**
     * Bottom line of the menu bar.
     */
    private static final class MenuBarBorder extends AbstractBorder implements UIResource {
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.setColor(EvePalette.BORDER);
            g.fillRect(x, y + height - 1, width, 1);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.set(0, 2, 1, 2);
            return insets;
        }
    }

    /**
     * Popup frame with an accent bracket in the top-left corner.
     */
    private static final class PopupBorder extends AbstractBorder implements UIResource {
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.setColor(EvePalette.BORDER_BRIGHT);
            g.drawRect(x, y, width - 1, height - 1);
            g.setColor(EvePalette.ACCENT);
            g.fillRect(x, y, Math.min(16, width), 1);
            g.fillRect(x, y, 1, Math.min(8, height));
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.set(2, 2, 2, 2);
            return insets;
        }
    }

    /**
     * Toolbar separating line and floatable grip.
     */
    private static final class ToolBarBorder extends AbstractBorder implements UIResource {
        private static final int GRIP = 8;

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            final boolean vertical = isVertical(c);
            g.setColor(EvePalette.BORDER);
            if (vertical) {
                g.fillRect(x + width - 1, y, 1, height);
            } else {
                g.fillRect(x, y + height - 1, width, 1);
            }
            if (c instanceof JToolBar bar && bar.isFloatable()) {
                g.setColor(EvePalette.BORDER_BRIGHT);
                if (vertical) {
                    for (int i = x + 4; i < x + width - 4; i += 3) {
                        g.fillRect(i, y + 2, 1, 1);
                        g.fillRect(i + 1, y + 5, 1, 1);
                    }
                } else {
                    for (int j = y + 4; j < y + height - 4; j += 3) {
                        g.fillRect(x + 2, j, 1, 1);
                        g.fillRect(x + 5, j + 1, 1, 1);
                    }
                }
            }
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            final int grip = c instanceof JToolBar bar && bar.isFloatable() ? GRIP : 0;
            if (isVertical(c)) {
                insets.set(2 + grip, 2, 2, 3);
            } else {
                insets.set(2, 2 + grip, 3, 2);
            }
            return insets;
        }

        private static boolean isVertical(Component c) {
            return c instanceof JToolBar bar && bar.getOrientation() == SwingConstants.VERTICAL;
        }
    }

    /**
     * Toolbar button frame, visible on hover, press and selection only.
     */
    private static final class ToolBarButtonBorder extends AbstractBorder implements UIResource {
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            if (!(c instanceof AbstractButton b) || !b.isEnabled()) {
                return;
            }
            final ButtonModel model = b.getModel();
            final Color line;
            if ((model.isPressed() && model.isArmed()) || model.isSelected()) {
                line = EvePalette.ACCENT;
            } else if (model.isRollover()) {
                line = EvePalette.BORDER_BRIGHT;
            } else {
                return;
            }
            EvePaint.drawCut(g, line, x, y, width, height, EvePaint.DIAGONAL);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.set(3, 6, 3, 6);
            return insets;
        }
    }
}

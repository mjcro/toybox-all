package io.github.mjcro.toybox.eve.plaf;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.ButtonModel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.plaf.basic.BasicGraphicsUtils;

import org.jspecify.annotations.Nullable;

import io.github.mjcro.toybox.eve.EvePaint;
import io.github.mjcro.toybox.eve.EvePalette;

/**
 * Shared painting of menu items and menus; used by all menu item UI delegates.
 */
final class MenusPainter {
    private MenusPainter() {
    }

    /**
     * Paints the item background: highlight with accent marker for armed items,
     * hover and open states (with accent underline) for top-level menus.
     */
    static void paintBackground(Graphics g, JMenuItem item) {
        final ButtonModel model = item.getModel();
        final int w = item.getWidth();
        final int h = item.getHeight();
        if (item.isOpaque()) {
            g.setColor(item.getBackground());
            g.fillRect(0, 0, w, h);
        }
        if (!item.isEnabled()) {
            return;
        }
        if (isTopLevel(item)) {
            if (model.isSelected()) {
                g.setColor(EvePalette.SELECTION);
                g.fillRect(0, 0, w, h);
                g.setColor(EvePalette.ACCENT);
                g.fillRect(0, h - 2, w, 2);
            } else if (model.isRollover()) {
                g.setColor(EvePalette.RAISED_HOVER);
                g.fillRect(0, 0, w, h);
            }
        } else if (isHighlighted(item)) {
            g.setColor(EvePalette.SELECTION);
            g.fillRect(0, 0, w, h);
            EvePaint.drawMarker(g, EvePalette.ACCENT, 0, 0, h);
        }
    }

    /**
     * Paints the item text with mnemonic underline, honoring enabled and highlight states.
     */
    static void paintText(
            Graphics g,
            JMenuItem item,
            Rectangle textRect,
            String text,
            Color selectionForeground,
            @Nullable Color disabledForeground
    ) {
        final Color color;
        if (!item.isEnabled()) {
            color = disabledForeground != null ? disabledForeground : EvePalette.TEXT_DIM;
        } else if (isHighlighted(item) || (isTopLevel(item) && item.getModel().isRollover())) {
            color = selectionForeground;
        } else {
            color = item.getForeground();
        }
        final FontMetrics fm = g.getFontMetrics();
        g.setColor(color);
        BasicGraphicsUtils.drawStringUnderlineCharAt(item, (Graphics2D) g, text,
                item.getDisplayedMnemonicIndex(), textRect.x, textRect.y + fm.getAscent());
    }

    private static boolean isTopLevel(JMenuItem item) {
        return item instanceof JMenu menu && menu.isTopLevelMenu();
    }

    private static boolean isHighlighted(JMenuItem item) {
        final ButtonModel model = item.getModel();
        return model.isArmed() || (item instanceof JMenu && model.isSelected());
    }
}

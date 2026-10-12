package io.github.mjcro.toybox.eve.plaf;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.text.View;

import org.jspecify.annotations.Nullable;

import io.github.mjcro.toybox.eve.EveIcons;
import io.github.mjcro.toybox.eve.EvePalette;

/**
 * Eve UI delegate for {@link javax.swing.JTabbedPane}.
 *
 * <p>Flat, compact tabs on a dark strip. The selected tab shares the content
 * background and carries a 2px accent line on its outer edge; unselected tabs show
 * dim text that brightens on hover. The content area is separated from the tab
 * strip by a single 1px line interrupted under the selected tab.
 */
public class EveTabbedPaneUI extends BasicTabbedPaneUI {
    private static final int MIN_TAB_HEIGHT = 22;
    private final Rectangle scratch = new Rectangle();

    /**
     * Creates the delegate; prefer {@link #createUI(javax.swing.JComponent)}.
     */
    public EveTabbedPaneUI() {
    }

    /**
     * Creates the UI delegate; called reflectively by {@link javax.swing.UIManager}.
     *
     * @param c component to create the delegate for, not null.
     * @return new non-null delegate.
     */
    public static ComponentUI createUI(JComponent c) {
        return new EveTabbedPaneUI();
    }

    @Override
    protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
        return Math.max(MIN_TAB_HEIGHT, super.calculateTabHeight(tabPlacement, tabIndex, fontHeight));
    }

    @Override
    protected int getTabLabelShiftX(int tabPlacement, int tabIndex, boolean isSelected) {
        return 0;
    }

    @Override
    protected int getTabLabelShiftY(int tabPlacement, int tabIndex, boolean isSelected) {
        return 0;
    }

    @Override
    protected Insets getContentBorderInsets(int tabPlacement) {
        return switch (tabPlacement) {
            case LEFT -> new Insets(0, 1, 0, 0);
            case BOTTOM -> new Insets(0, 0, 1, 0);
            case RIGHT -> new Insets(0, 0, 0, 1);
            default -> new Insets(1, 0, 0, 0);
        };
    }

    @Override
    protected void setRolloverTab(int index) {
        final int previous = getRolloverTab();
        super.setRolloverTab(index);
        if (previous != index) {
            repaintTab(previous);
            repaintTab(index);
        }
    }

    @Override
    protected void paintTabArea(Graphics g, int tabPlacement, int selectedIndex) {
        if (tabPane.getTabLayoutPolicy() == JTabbedPane.WRAP_TAB_LAYOUT) {
            g.setColor(strip());
            final Rectangle area = tabArea(tabPlacement);
            g.fillRect(area.x, area.y, area.width, area.height);
        }
        super.paintTabArea(g, tabPlacement, selectedIndex);
    }

    @Override
    protected void paintTabBackground(
            Graphics g,
            int tabPlacement,
            int tabIndex,
            int x,
            int y,
            int w,
            int h,
            boolean isSelected
    ) {
        if (isSelected) {
            g.setColor(tabPane.getBackground());
            g.fillRect(x, y, w, h);
        } else if (tabIndex == getRolloverTab() && tabPane.isEnabledAt(tabIndex)) {
            g.setColor(EvePalette.RAISED);
            g.fillRect(x, y, w, h);
        }
    }

    @Override
    protected void paintTabBorder(
            Graphics g,
            int tabPlacement,
            int tabIndex,
            int x,
            int y,
            int w,
            int h,
            boolean isSelected
    ) {
        final boolean horizontal = tabPlacement == TOP || tabPlacement == BOTTOM;
        if (!isSelected) {
            g.setColor(EvePalette.GRID);
            if (horizontal) {
                g.fillRect(x + w - 1, y + 4, 1, h - 8);
            } else {
                g.fillRect(x + 4, y + h - 1, w - 8, 1);
            }
            return;
        }
        g.setColor(EvePalette.BORDER);
        if (horizontal) {
            g.fillRect(x, y, 1, h);
            g.fillRect(x + w - 1, y, 1, h);
        } else {
            g.fillRect(x, y, w, 1);
            g.fillRect(x, y + h - 1, w, 1);
        }
        g.setColor(tabPane.isEnabledAt(tabIndex) ? EvePalette.ACCENT : EvePalette.BORDER_BRIGHT);
        switch (tabPlacement) {
            case LEFT -> g.fillRect(x, y, 2, h);
            case BOTTOM -> g.fillRect(x, y + h - 2, w, 2);
            case RIGHT -> g.fillRect(x + w - 2, y, 2, h);
            default -> g.fillRect(x, y, w, 2);
        }
    }

    @Override
    protected void paintFocusIndicator(
            Graphics g,
            int tabPlacement,
            Rectangle[] rects,
            int tabIndex,
            Rectangle iconRect,
            Rectangle textRect,
            boolean isSelected
    ) {
        // Focus is expressed by the accent line of the selected tab.
    }

    @Override
    protected void paintText(
            Graphics g,
            int tabPlacement,
            Font font,
            FontMetrics metrics,
            int tabIndex,
            String title,
            Rectangle textRect,
            boolean isSelected
    ) {
        g.setFont(font);
        final View v = getTextViewForTab(tabIndex);
        if (v != null) {
            v.paint(g, textRect);
            return;
        }
        final Color custom = tabPane.getForegroundAt(tabIndex);
        final Color color;
        if (!tabPane.isEnabled() || !tabPane.isEnabledAt(tabIndex)) {
            color = EvePalette.mix(EvePalette.TEXT_DIM, EvePalette.PANEL, 0.35f);
        } else if (custom != null && !(custom instanceof UIResource)) {
            color = custom;
        } else if (isSelected) {
            color = EvePalette.TEXT_BRIGHT;
        } else if (tabIndex == getRolloverTab()) {
            color = EvePalette.TEXT;
        } else {
            color = EvePalette.TEXT_DIM;
        }
        g.setColor(color);
        BasicGraphicsUtils.drawStringUnderlineCharAt(tabPane, (Graphics2D) g, title,
                tabPane.getDisplayedMnemonicIndexAt(tabIndex), textRect.x, textRect.y + metrics.getAscent());
    }

    @Override
    protected void paintContentBorderTopEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
        if (tabPlacement == TOP) {
            paintEdge(g, selectedIndex, x, y, w, true);
        }
    }

    @Override
    protected void paintContentBorderBottomEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
        if (tabPlacement == BOTTOM) {
            paintEdge(g, selectedIndex, x, y + h - 1, w, true);
        }
    }

    @Override
    protected void paintContentBorderLeftEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
        if (tabPlacement == LEFT) {
            paintEdge(g, selectedIndex, x, y, h, false);
        }
    }

    @Override
    protected void paintContentBorderRightEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
        if (tabPlacement == RIGHT) {
            paintEdge(g, selectedIndex, x + w - 1, y, h, false);
        }
    }

    @Override
    protected JButton createScrollButton(int direction) {
        return new ScrollButton(direction);
    }

    /**
     * Draws the 1px separator line between tab strip and content, leaving a gap under the selected tab.
     *
     * @param horizontal {@code true} for a horizontal line starting at (x, y) of the given length.
     */
    private void paintEdge(Graphics g, int selectedIndex, int x, int y, int length, boolean horizontal) {
        g.setColor(EvePalette.BORDER);
        final @Nullable Rectangle sel = selectedIndex < 0 ? null : getTabBounds(selectedIndex, scratch);
        if (horizontal) {
            if (sel == null || sel.x + sel.width <= x || sel.x >= x + length) {
                g.fillRect(x, y, length, 1);
                return;
            }
            g.fillRect(x, y, Math.max(0, sel.x - x + 1), 1);
            g.fillRect(sel.x + sel.width - 1, y, Math.max(0, x + length - sel.x - sel.width + 1), 1);
        } else {
            if (sel == null || sel.y + sel.height <= y || sel.y >= y + length) {
                g.fillRect(x, y, 1, length);
                return;
            }
            g.fillRect(x, y, 1, Math.max(0, sel.y - y + 1));
            g.fillRect(x, sel.y + sel.height - 1, 1, Math.max(0, y + length - sel.y - sel.height + 1));
        }
    }

    private Rectangle tabArea(int tabPlacement) {
        final Insets insets = tabPane.getInsets();
        final int width = tabPane.getWidth() - insets.left - insets.right;
        final int height = tabPane.getHeight() - insets.top - insets.bottom;
        return switch (tabPlacement) {
            case LEFT -> new Rectangle(insets.left, insets.top,
                    calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth), height);
            case RIGHT -> {
                final int aw = calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth);
                yield new Rectangle(insets.left + width - aw, insets.top, aw, height);
            }
            case BOTTOM -> {
                final int ah = calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
                yield new Rectangle(insets.left, insets.top + height - ah, width, ah);
            }
            default -> new Rectangle(insets.left, insets.top, width,
                    calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight));
        };
    }

    private void repaintTab(int index) {
        if (index >= 0 && index < tabPane.getTabCount()) {
            tabPane.repaint(getTabBounds(tabPane, index));
        }
    }

    private static Color strip() {
        final Color color = UIManager.getColor("TabbedPane.tabAreaBackground");
        return color != null ? color : EvePalette.BACKGROUND;
    }

    /**
     * Flat chevron button used to scroll tabs in {@link javax.swing.JTabbedPane#SCROLL_TAB_LAYOUT}.
     */
    private static final class ScrollButton extends BasicArrowButton implements UIResource {
        ScrollButton(int direction) {
            super(direction);
            setBorder(null);
            setFocusable(false);
            setRolloverEnabled(true);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(16, MIN_TAB_HEIGHT);
        }

        @Override
        public void paint(Graphics g) {
            final boolean active = isEnabled() && (getModel().isRollover() || getModel().isPressed());
            g.setColor(active ? EvePalette.RAISED_HOVER : strip());
            g.fillRect(0, 0, getWidth(), getHeight());
            final int direction = switch (getDirection()) {
                case SwingConstants.NORTH, SwingConstants.SOUTH, SwingConstants.EAST, SwingConstants.WEST -> getDirection();
                default -> SwingConstants.EAST;
            };
            final Icon icon = EveIcons.arrow(direction, 12, active ? EvePalette.ACCENT : EvePalette.TEXT);
            icon.paintIcon(this, g, (getWidth() - icon.getIconWidth()) / 2, (getHeight() - icon.getIconHeight()) / 2);
        }
    }
}

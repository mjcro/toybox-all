package io.github.mjcro.toybox.eve.plaf;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;

import io.github.mjcro.toybox.eve.EvePaint;
import io.github.mjcro.toybox.eve.EvePalette;

/**
 * Compact title bar of internal frames and desktop icons, styled like {@link WindowsTitlePane}:
 * accent marker, uppercase letter-spaced title and flat iconify / maximize / close buttons.
 * The system menu is kept for keyboard access but takes no space.
 */
public class WindowsInternalFrameTitlePane extends BasicInternalFrameTitlePane {
    private static final int HEIGHT = 22;

    /**
     * Creates the title pane.
     *
     * @param frame internal frame this title pane belongs to, not null.
     */
    public WindowsInternalFrameTitlePane(JInternalFrame frame) {
        super(frame);
        setOpaque(true);
    }

    @Override
    protected void createButtons() {
        iconButton = control(false, "InternalFrameTitlePane.iconifyButtonAccessibleName");
        iconButton.addActionListener(iconifyAction);
        maxButton = control(false, "InternalFrameTitlePane.maximizeButtonAccessibleName");
        maxButton.addActionListener(maximizeAction);
        closeButton = control(true, "InternalFrameTitlePane.closeButtonAccessibleName");
        closeButton.addActionListener(closeAction);
        setButtonIcons();
    }

    @Override
    protected LayoutManager createLayout() {
        return new TitleLayout();
    }

    @Override
    public void paintComponent(Graphics g) {
        final boolean selected = frame.isSelected();
        final int w = getWidth();
        final int h = getHeight();
        if (selected) {
            EvePaint.fillGradient(g, EvePalette.RAISED, EvePalette.PANEL, 0, 0, w, h);
        } else {
            g.setColor(EvePalette.PANEL);
            g.fillRect(0, 0, w, h);
        }
        g.setColor(EvePalette.BORDER);
        g.fillRect(0, h - 1, w, 1);
        EvePaint.drawMarker(g, selected ? EvePalette.ACCENT : EvePalette.BORDER_BRIGHT, 5, (h - 10) / 2, 10);

        final String title = frame.getTitle();
        if (title == null || title.isEmpty()) {
            return;
        }
        final Graphics2D g2 = EvePaint.smooth(g);
        try {
            g2.setFont(getFont());
            final FontMetrics fm = g2.getFontMetrics();
            final int x = 12;
            final int available = leftmostButtonX() - x - 6;
            g2.setColor(selected ? EvePalette.TEXT_BRIGHT : EvePalette.TEXT_DIM);
            g2.drawString(getTitle(title.toUpperCase(Locale.ROOT), fm, available), x, (h - fm.getHeight()) / 2 + fm.getAscent());
        } finally {
            g2.dispose();
        }
    }

    private int leftmostButtonX() {
        int x = getWidth();
        for (Component c : getComponents()) {
            if (c instanceof WindowsControlButton && c.isVisible() && c.getWidth() > 0) {
                x = Math.min(x, c.getX());
            }
        }
        return x;
    }

    private static JButton control(boolean danger, String accessibleNameKey) {
        final WindowsControlButton button = new WindowsControlButton(danger);
        final String name = UIManager.getString(accessibleNameKey);
        button.getAccessibleContext().setAccessibleName(name);
        button.setToolTipText(name);
        return button;
    }

    /**
     * Places buttons right-to-left (close, maximize, iconify) and hides the system menu bar.
     */
    private final class TitleLayout implements LayoutManager {
        @Override
        public void addLayoutComponent(String name, Component c) {
        }

        @Override
        public void removeLayoutComponent(Component c) {
        }

        @Override
        public Dimension preferredLayoutSize(Container c) {
            final Insets i = c.getInsets();
            int width = 60;
            for (JButton button : new JButton[]{closeButton, maxButton, iconButton}) {
                width += button.getPreferredSize().width;
            }
            return new Dimension(width + i.left + i.right, HEIGHT + i.top + i.bottom);
        }

        @Override
        public Dimension minimumLayoutSize(Container c) {
            return preferredLayoutSize(c);
        }

        @Override
        public void layoutContainer(Container c) {
            final Insets i = c.getInsets();
            if (menuBar != null) {
                menuBar.setBounds(0, 0, 0, 0);
            }
            int x = c.getWidth() - i.right - 2;
            x = place(closeButton, frame.isClosable(), x, c.getHeight());
            x = place(maxButton, frame.isMaximizable(), x, c.getHeight());
            place(iconButton, frame.isIconifiable(), x, c.getHeight());
        }

        private int place(JButton button, boolean shown, int right, int height) {
            if (!shown) {
                button.setBounds(0, 0, 0, 0);
                return right;
            }
            final Dimension size = button.getPreferredSize();
            final int bh = Math.min(size.height, height - 2);
            button.setBounds(right - size.width, (height - bh) / 2, size.width, bh);
            return right - size.width - 1;
        }
    }
}

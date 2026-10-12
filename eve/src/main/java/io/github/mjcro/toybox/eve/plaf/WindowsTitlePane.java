package io.github.mjcro.toybox.eve.plaf;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Locale;

import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.MouseInputAdapter;

import org.jspecify.annotations.Nullable;

import io.github.mjcro.toybox.eve.EveFonts;
import io.github.mjcro.toybox.eve.EvePaint;
import io.github.mjcro.toybox.eve.EvePalette;

/**
 * Title bar of Look and Feel decorated windows, installed by {@link EveRootPaneUI}.
 *
 * <p>Shows an accent marker, the window icon (frames only), the uppercase
 * letter-spaced title and the minimize / maximize-restore / close buttons
 * (dialogs get close only). Dragging the bar moves the window; double-click
 * toggles maximization of resizable frames.
 *
 * <p>When the root pane is not hosted in a window (e.g. rendered off-screen), the
 * title is read from the {@link #TITLE_PROPERTY} client property of the root pane.
 */
public final class WindowsTitlePane extends JComponent {
    /** Root pane client property holding the title used when there is no window. */
    public static final String TITLE_PROPERTY = "Eve.windowTitle";

    private static final int HEIGHT = 24;
    private static final int GLYPH = 12;
    /** Owning root pane. */
    private final JRootPane root;
    /** Minimize (iconify) button. */
    private final WindowsControlButton minimizeButton = new WindowsControlButton(false);
    /** Maximize / restore button. */
    private final WindowsControlButton maximizeButton = new WindowsControlButton(false);
    /** Close button. */
    private final WindowsControlButton closeButton = new WindowsControlButton(true);
    /** Tracks window activation and state. */
    private final WindowHandler windowHandler = new WindowHandler();
    /** Refreshes buttons on window property changes. */
    private final PropertyChangeListener windowPropertyHandler = e -> {
        updateButtons();
        repaint();
    };
    /** Window currently decorated; null while not attached. */
    private @Nullable Window window;

    /**
     * Creates the title pane.
     *
     * @param root decorated root pane, not null.
     */
    public WindowsTitlePane(JRootPane root) {
        this.root = root;
        setOpaque(true);
        setFont(EveFonts.header());
        minimizeButton.setIcon(WindowsIcons.minimize(GLYPH, EvePalette.TEXT));
        minimizeButton.setToolTipText(UIManager.getString("InternalFrameTitlePane.minimizeButtonAccessibleName"));
        minimizeButton.addActionListener(e -> iconify());
        maximizeButton.addActionListener(e -> toggleMaximized());
        closeButton.setIcon(WindowsIcons.close(GLYPH, EvePalette.TEXT));
        closeButton.setToolTipText(UIManager.getString("InternalFrameTitlePane.closeButtonAccessibleName"));
        closeButton.addActionListener(e -> close());
        add(minimizeButton);
        add(maximizeButton);
        add(closeButton);
        final DragHandler drag = new DragHandler();
        addMouseListener(drag);
        addMouseMotionListener(drag);
        updateButtons();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        uninstallWindowListeners();
        final Window w = SwingUtilities.getWindowAncestor(this);
        if (w != null) {
            window = w;
            w.addWindowListener(windowHandler);
            w.addWindowStateListener(windowHandler);
            w.addPropertyChangeListener(windowPropertyHandler);
        }
        updateButtons();
    }

    @Override
    public void removeNotify() {
        uninstallWindowListeners();
        super.removeNotify();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(160, HEIGHT);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(60, HEIGHT);
    }

    @Override
    public void doLayout() {
        final int y = (getHeight() - closeButton.getPreferredSize().height) / 2;
        int x = getWidth() - 3;
        for (WindowsControlButton button : List.of(closeButton, maximizeButton, minimizeButton)) {
            if (button.isVisible()) {
                final Dimension size = button.getPreferredSize();
                x -= size.width;
                button.setBounds(x, y, size.width, size.height);
                x -= 1;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        final boolean active = isActive();
        final int w = getWidth();
        final int h = getHeight();
        if (active) {
            EvePaint.fillGradient(g, EvePalette.RAISED, EvePalette.PANEL, 0, 0, w, h);
        } else {
            g.setColor(EvePalette.PANEL);
            g.fillRect(0, 0, w, h);
        }
        g.setColor(EvePalette.BORDER);
        g.fillRect(0, h - 1, w, 1);
        EvePaint.drawMarker(g, active ? accent() : EvePalette.BORDER_BRIGHT, 6, (h - 12) / 2, 12);

        int x = 14;
        final @Nullable Image icon = windowIcon();
        if (icon != null) {
            g.drawImage(icon, x, (h - 16) / 2, 16, 16, this);
            x += 22;
        }
        final String title = title().toUpperCase(Locale.ROOT);
        if (title.isEmpty()) {
            return;
        }
        final Graphics2D g2 = EvePaint.smooth(g);
        try {
            final Font font = getFont();
            g2.setFont(font);
            final FontMetrics fm = g2.getFontMetrics();
            final int available = leftmostButtonX() - x - 8;
            g2.setColor(active ? EvePalette.TEXT_BRIGHT : EvePalette.TEXT_DIM);
            g2.drawString(clip(title, fm, available), x, (h - fm.getHeight()) / 2 + fm.getAscent());
        } finally {
            g2.dispose();
        }
    }

    private void uninstallWindowListeners() {
        final Window w = window;
        if (w != null) {
            w.removeWindowListener(windowHandler);
            w.removeWindowStateListener(windowHandler);
            w.removePropertyChangeListener(windowPropertyHandler);
            window = null;
        }
    }

    private void updateButtons() {
        final Window w = window;
        final boolean frame = w instanceof Frame || (w == null && root.getWindowDecorationStyle() == JRootPane.FRAME);
        final boolean resizable = w instanceof Frame f ? f.isResizable() : frame;
        minimizeButton.setVisible(frame);
        maximizeButton.setVisible(frame && resizable);
        final boolean maximized = isMaximized();
        maximizeButton.setIcon(maximized
                ? WindowsIcons.restore(GLYPH, EvePalette.TEXT)
                : WindowsIcons.maximize(GLYPH, EvePalette.TEXT));
        maximizeButton.setToolTipText(UIManager.getString(maximized
                ? "InternalFrameTitlePane.restoreButtonAccessibleName"
                : "InternalFrameTitlePane.maximizeButtonAccessibleName"));
        revalidate();
        repaint();
    }

    private int leftmostButtonX() {
        int x = getWidth();
        for (WindowsControlButton button : List.of(closeButton, maximizeButton, minimizeButton)) {
            if (button.isVisible()) {
                x = Math.min(x, button.getX());
            }
        }
        return x;
    }

    private boolean isActive() {
        final Window w = window;
        return w == null || w.isActive();
    }

    private boolean isMaximized() {
        return window instanceof Frame f && (f.getExtendedState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH;
    }

    private Color accent() {
        return root.getBorder() instanceof WindowsFrameBorder border ? border.getAccent() : EvePalette.ACCENT;
    }

    private String title() {
        final Window w = window;
        final @Nullable String title;
        if (w instanceof Frame f) {
            title = f.getTitle();
        } else if (w instanceof Dialog d) {
            title = d.getTitle();
        } else {
            title = root.getClientProperty(TITLE_PROPERTY) instanceof String s ? s : null;
        }
        return title == null ? "" : title;
    }

    private @Nullable Image windowIcon() {
        if (window instanceof Frame f) {
            final List<Image> images = f.getIconImages();
            if (!images.isEmpty()) {
                return images.get(0);
            }
        }
        return null;
    }

    private static String clip(String text, FontMetrics fm, int available) {
        if (fm.stringWidth(text) <= available) {
            return text;
        }
        final String ellipsis = "...";
        int end = text.length();
        while (end > 0 && fm.stringWidth(text.substring(0, end) + ellipsis) > available) {
            end--;
        }
        return end == 0 ? "" : text.substring(0, end) + ellipsis;
    }

    private void iconify() {
        if (window instanceof Frame f) {
            f.setExtendedState(f.getExtendedState() | Frame.ICONIFIED);
        }
    }

    private void toggleMaximized() {
        if (!(window instanceof Frame f) || !f.isResizable()) {
            return;
        }
        final int state = f.getExtendedState();
        if ((state & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
            f.setExtendedState(state & ~Frame.MAXIMIZED_BOTH);
        } else {
            f.setMaximizedBounds(maximizedBounds(f));
            f.setExtendedState(state | Frame.MAXIMIZED_BOTH);
        }
    }

    /**
     * Screen area minus taskbars/panels, so undecorated frames do not cover them when maximized.
     */
    private static Rectangle maximizedBounds(Frame frame) {
        final GraphicsConfiguration gc = frame.getGraphicsConfiguration();
        final Rectangle screen = gc.getBounds();
        final Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(gc);
        final Rectangle bounds = new Rectangle(
                insets.left,
                insets.top,
                screen.width - insets.left - insets.right,
                screen.height - insets.top - insets.bottom
        );
        // Windows treats maximized bounds as relative to the frame's screen, other platforms as absolute.
        if (!System.getProperty("os.name", "").startsWith("Windows")) {
            bounds.translate(screen.x, screen.y);
        }
        return bounds;
    }

    private void close() {
        final Window w = window;
        if (w != null) {
            w.dispatchEvent(new WindowEvent(w, WindowEvent.WINDOW_CLOSING));
        }
    }

    /**
     * Tracks window activation and state to repaint the bar and swap the maximize icon.
     */
    private final class WindowHandler extends WindowAdapter {
        @Override
        public void windowActivated(WindowEvent e) {
            repaintFrame();
        }

        @Override
        public void windowDeactivated(WindowEvent e) {
            repaintFrame();
        }

        @Override
        public void windowStateChanged(WindowEvent e) {
            updateButtons();
        }

        private void repaintFrame() {
            repaint();
            root.repaint();
        }
    }

    /**
     * Moves the window by dragging the title bar; double-click toggles maximization.
     */
    private final class DragHandler extends MouseInputAdapter {
        private @Nullable Point offset;

        @Override
        public void mousePressed(MouseEvent e) {
            final Window w = window;
            if (w == null || !SwingUtilities.isLeftMouseButton(e) || isMaximized()) {
                offset = null;
                return;
            }
            final Point screen = e.getLocationOnScreen();
            offset = new Point(screen.x - w.getX(), screen.y - w.getY());
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            final Window w = window;
            final Point start = offset;
            if (w != null && start != null) {
                final Point screen = e.getLocationOnScreen();
                w.setLocation(screen.x - start.x, screen.y - start.y);
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            offset = null;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                toggleMaximized();
            }
        }
    }
}

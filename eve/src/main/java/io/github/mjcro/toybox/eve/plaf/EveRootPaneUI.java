package io.github.mjcro.toybox.eve.plaf;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.util.function.Function;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JRootPane;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicRootPaneUI;

import org.jspecify.annotations.Nullable;

/**
 * Eve UI delegate for {@link javax.swing.JRootPane}.
 *
 * <p>When the root pane has a window decoration style other than
 * {@link JRootPane#NONE} (see {@link javax.swing.JFrame#setDefaultLookAndFeelDecorated(boolean)}),
 * installs the Eve window frame: a {@link WindowsFrameBorder} chosen by decoration
 * style, a {@link WindowsTitlePane} above the menu bar and content, and edge/corner
 * resizing of the window through the border area.
 */
public class EveRootPaneUI extends BasicRootPaneUI {
    private static final int CORNER = 14;
    private final ResizeHandler resizeHandler = new ResizeHandler();
    private @Nullable JRootPane root;
    private @Nullable WindowsTitlePane titlePane;
    private @Nullable LayoutManager savedLayout;
    private @Nullable Window window;

    /**
     * Creates the delegate; prefer {@link #createUI(javax.swing.JComponent)}.
     */
    public EveRootPaneUI() {
    }

    /**
     * Creates the UI delegate; called reflectively by {@link javax.swing.UIManager}.
     *
     * @param c component to create the delegate for, not null.
     * @return new non-null delegate; delegates are stateful and never shared.
     */
    public static ComponentUI createUI(JComponent c) {
        return new EveRootPaneUI();
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        final JRootPane rootPane = (JRootPane) c;
        root = rootPane;
        if (rootPane.getWindowDecorationStyle() != JRootPane.NONE) {
            installDecorations(rootPane);
        }
    }

    @Override
    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);
        uninstallDecorations((JRootPane) c);
        root = null;
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {
        super.propertyChange(e);
        final JRootPane rootPane = root;
        if (rootPane == null) {
            return;
        }
        final String name = e.getPropertyName();
        if ("windowDecorationStyle".equals(name)) {
            uninstallDecorations(rootPane);
            if (rootPane.getWindowDecorationStyle() != JRootPane.NONE) {
                installDecorations(rootPane);
            }
        } else if ("ancestor".equals(name)) {
            uninstallWindowListeners();
            if (rootPane.getWindowDecorationStyle() != JRootPane.NONE) {
                installWindowListeners(rootPane);
            }
        }
    }

    private void installDecorations(JRootPane rootPane) {
        LookAndFeel.installBorder(rootPane, borderKey(rootPane.getWindowDecorationStyle()));
        final WindowsTitlePane pane = new WindowsTitlePane(rootPane);
        rootPane.getLayeredPane().add(pane, JLayeredPane.FRAME_CONTENT_LAYER);
        titlePane = pane;
        savedLayout = rootPane.getLayout();
        rootPane.setLayout(new DecoratedLayout());
        installWindowListeners(rootPane);
        rootPane.revalidate();
        rootPane.repaint();
    }

    private void uninstallDecorations(JRootPane rootPane) {
        uninstallWindowListeners();
        LookAndFeel.uninstallBorder(rootPane);
        final WindowsTitlePane pane = titlePane;
        if (pane != null) {
            rootPane.getLayeredPane().remove(pane);
            titlePane = null;
        }
        final LayoutManager layout = savedLayout;
        if (layout != null) {
            rootPane.setLayout(layout);
            savedLayout = null;
        }
        rootPane.revalidate();
        rootPane.repaint();
    }

    private void installWindowListeners(JRootPane rootPane) {
        final Container parent = rootPane.getParent();
        final @Nullable Window w = parent instanceof Window pw ? pw : SwingUtilities.getWindowAncestor(rootPane);
        if (w != null) {
            window = w;
            w.addMouseListener(resizeHandler);
            w.addMouseMotionListener(resizeHandler);
        }
    }

    private void uninstallWindowListeners() {
        final Window w = window;
        if (w != null) {
            w.removeMouseListener(resizeHandler);
            w.removeMouseMotionListener(resizeHandler);
            w.setCursor(Cursor.getDefaultCursor());
            window = null;
        }
    }

    private static String borderKey(int style) {
        return switch (style) {
            case JRootPane.PLAIN_DIALOG -> "RootPane.plainDialogBorder";
            case JRootPane.INFORMATION_DIALOG -> "RootPane.informationDialogBorder";
            case JRootPane.ERROR_DIALOG -> "RootPane.errorDialogBorder";
            case JRootPane.COLOR_CHOOSER_DIALOG -> "RootPane.colorChooserDialogBorder";
            case JRootPane.FILE_CHOOSER_DIALOG -> "RootPane.fileChooserDialogBorder";
            case JRootPane.QUESTION_DIALOG -> "RootPane.questionDialogBorder";
            case JRootPane.WARNING_DIALOG -> "RootPane.warningDialogBorder";
            default -> "RootPane.frameBorder";
        };
    }

    /**
     * Root pane layout with the title pane on top of the layered pane, followed by the menu bar and content.
     */
    private final class DecoratedLayout implements LayoutManager2 {
        @Override
        public Dimension preferredLayoutSize(Container parent) {
            return size((JRootPane) parent, Component::getPreferredSize);
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            return size((JRootPane) parent, Component::getMinimumSize);
        }

        @Override
        public Dimension maximumLayoutSize(Container target) {
            return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
        }

        @Override
        public void layoutContainer(Container parent) {
            final JRootPane rootPane = (JRootPane) parent;
            final Insets i = rootPane.getInsets();
            final int w = rootPane.getWidth() - i.left - i.right;
            final int h = rootPane.getHeight() - i.top - i.bottom;
            rootPane.getLayeredPane().setBounds(i.left, i.top, w, h);
            rootPane.getGlassPane().setBounds(i.left, i.top, w, h);
            int y = 0;
            final WindowsTitlePane pane = titlePane;
            if (pane != null) {
                final int th = pane.getPreferredSize().height;
                pane.setBounds(0, 0, w, th);
                y += th;
            }
            if (rootPane.getJMenuBar() != null) {
                final int mh = rootPane.getJMenuBar().getPreferredSize().height;
                rootPane.getJMenuBar().setBounds(0, y, w, mh);
                y += mh;
            }
            if (rootPane.getContentPane() != null) {
                rootPane.getContentPane().setBounds(0, y, w, Math.max(0, h - y));
            }
        }

        private Dimension size(JRootPane rootPane, Function<Component, Dimension> metric) {
            final Insets i = rootPane.getInsets();
            int width = 0;
            int height = 0;
            final WindowsTitlePane pane = titlePane;
            if (pane != null) {
                final Dimension d = metric.apply(pane);
                width = d.width;
                height += d.height;
            }
            if (rootPane.getJMenuBar() != null) {
                final Dimension d = metric.apply(rootPane.getJMenuBar());
                width = Math.max(width, d.width);
                height += d.height;
            }
            if (rootPane.getContentPane() != null) {
                final Dimension d = metric.apply(rootPane.getContentPane());
                width = Math.max(width, d.width);
                height += d.height;
            }
            return new Dimension(width + i.left + i.right, height + i.top + i.bottom);
        }

        @Override
        public void addLayoutComponent(String name, Component comp) {
        }

        @Override
        public void addLayoutComponent(Component comp, Object constraints) {
        }

        @Override
        public void removeLayoutComponent(Component comp) {
        }

        @Override
        public float getLayoutAlignmentX(Container target) {
            return 0.5f;
        }

        @Override
        public float getLayoutAlignmentY(Container target) {
            return 0.5f;
        }

        @Override
        public void invalidateLayout(Container target) {
        }
    }

    /**
     * Resizes the window by dragging its border area. Listens on the window itself:
     * the root pane has no mouse listeners, so events over its border reach the window.
     */
    private final class ResizeHandler extends MouseInputAdapter {
        private int cursorType = Cursor.DEFAULT_CURSOR;
        private @Nullable Point dragStart;
        private @Nullable Rectangle startBounds;

        @Override
        public void mouseMoved(MouseEvent e) {
            final Window w = window;
            if (w == null || dragStart != null) {
                return;
            }
            cursorType = isResizable(w) ? cursorAt(w, e.getX(), e.getY()) : Cursor.DEFAULT_CURSOR;
            w.setCursor(Cursor.getPredefinedCursor(cursorType));
        }

        @Override
        public void mouseExited(MouseEvent e) {
            final Window w = window;
            if (w != null && dragStart == null) {
                cursorType = Cursor.DEFAULT_CURSOR;
                w.setCursor(Cursor.getDefaultCursor());
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            final Window w = window;
            if (w != null && cursorType != Cursor.DEFAULT_CURSOR && SwingUtilities.isLeftMouseButton(e)) {
                dragStart = e.getLocationOnScreen();
                startBounds = w.getBounds();
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            final Window w = window;
            final Point start = dragStart;
            final Rectangle from = startBounds;
            if (w == null || start == null || from == null) {
                return;
            }
            final Point now = e.getLocationOnScreen();
            final int dx = now.x - start.x;
            final int dy = now.y - start.y;
            final Dimension min = w.getMinimumSize();
            final int minW = Math.max(min.width, 120);
            final int minH = Math.max(min.height, 60);
            int x = from.x;
            int y = from.y;
            int width = from.width;
            int height = from.height;
            if (isWest(cursorType)) {
                width = Math.max(minW, from.width - dx);
                x = from.x + from.width - width;
            } else if (isEast(cursorType)) {
                width = Math.max(minW, from.width + dx);
            }
            if (isNorth(cursorType)) {
                height = Math.max(minH, from.height - dy);
                y = from.y + from.height - height;
            } else if (isSouth(cursorType)) {
                height = Math.max(minH, from.height + dy);
            }
            w.setBounds(x, y, width, height);
            w.validate();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            dragStart = null;
            startBounds = null;
        }

        private int cursorAt(Window w, int x, int y) {
            final JRootPane rootPane = root;
            if (rootPane == null) {
                return Cursor.DEFAULT_CURSOR;
            }
            // Coordinates are window-relative; translate into the root pane.
            final Point p = SwingUtilities.convertPoint(w, x, y, rootPane);
            final Insets i = rootPane.getInsets();
            final int width = rootPane.getWidth();
            final int height = rootPane.getHeight();
            if (p.x < 0 || p.y < 0 || p.x >= width || p.y >= height) {
                return Cursor.DEFAULT_CURSOR;
            }
            final boolean west = p.x < i.left;
            final boolean east = p.x >= width - i.right;
            final boolean north = p.y < i.top;
            final boolean south = p.y >= height - i.bottom;
            if (!west && !east && !north && !south) {
                return Cursor.DEFAULT_CURSOR;
            }
            final boolean nearWest = p.x < CORNER;
            final boolean nearEast = p.x >= width - CORNER;
            final boolean nearNorth = p.y < CORNER;
            final boolean nearSouth = p.y >= height - CORNER;
            if (nearNorth && nearWest) {
                return Cursor.NW_RESIZE_CURSOR;
            }
            if (nearNorth && nearEast) {
                return Cursor.NE_RESIZE_CURSOR;
            }
            if (nearSouth && nearWest) {
                return Cursor.SW_RESIZE_CURSOR;
            }
            if (nearSouth && nearEast) {
                return Cursor.SE_RESIZE_CURSOR;
            }
            if (north) {
                return Cursor.N_RESIZE_CURSOR;
            }
            if (south) {
                return Cursor.S_RESIZE_CURSOR;
            }
            return west ? Cursor.W_RESIZE_CURSOR : Cursor.E_RESIZE_CURSOR;
        }

        private static boolean isResizable(Window w) {
            if (w instanceof Frame f) {
                return f.isResizable() && (f.getExtendedState() & Frame.MAXIMIZED_BOTH) != Frame.MAXIMIZED_BOTH;
            }
            return w instanceof Dialog d && d.isResizable();
        }

        private static boolean isWest(int type) {
            return type == Cursor.W_RESIZE_CURSOR || type == Cursor.NW_RESIZE_CURSOR || type == Cursor.SW_RESIZE_CURSOR;
        }

        private static boolean isEast(int type) {
            return type == Cursor.E_RESIZE_CURSOR || type == Cursor.NE_RESIZE_CURSOR || type == Cursor.SE_RESIZE_CURSOR;
        }

        private static boolean isNorth(int type) {
            return type == Cursor.N_RESIZE_CURSOR || type == Cursor.NW_RESIZE_CURSOR || type == Cursor.NE_RESIZE_CURSOR;
        }

        private static boolean isSouth(int type) {
            return type == Cursor.S_RESIZE_CURSOR || type == Cursor.SW_RESIZE_CURSOR || type == Cursor.SE_RESIZE_CURSOR;
        }
    }
}

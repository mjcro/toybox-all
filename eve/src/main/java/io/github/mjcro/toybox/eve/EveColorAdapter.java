package io.github.mjcro.toybox.eve;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.HierarchyEvent;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;
import javax.swing.text.JTextComponent;

import org.jspecify.annotations.Nullable;

/**
 * Recolors text components that bypass Look and Feel defaults.
 *
 * <p>Some third-party text components (e.g. RSyntaxTextArea's {@code RTextArea}) hardcode a
 * white background and black text as plain, non-{@link UIResource} colors, so no Look and Feel
 * can replace them and they render as white boxes in the dark Eve theme. While Eve is installed,
 * every text component that becomes displayable with exactly these hardcoded colors gets the Eve
 * surface background and text color instead. Colors set by application code to any other value
 * are never touched.
 *
 * <p>To keep a component's white/black colors, set its client property {@link #KEEP_COLORS}
 * to {@link Boolean#TRUE} before it is displayed.
 */
public final class EveColorAdapter {
    /** Client property key; {@link Boolean#TRUE} excludes the component from recoloring. */
    public static final String KEEP_COLORS = "Eve.keepColors";

    private static final AWTEventListener LISTENER = EveColorAdapter::onHierarchyEvent;
    private static boolean installed = false;

    private EveColorAdapter() {
    }

    /**
     * Starts recoloring: registers a global hierarchy listener and adapts components of already open windows.
     * Idempotent; invoked by {@link EveLookAndFeel#initialize()}.
     */
    static synchronized void install() {
        if (installed) {
            return;
        }
        Toolkit.getDefaultToolkit().addAWTEventListener(LISTENER, AWTEvent.HIERARCHY_EVENT_MASK);
        installed = true;
        SwingUtilities.invokeLater(() -> {
            for (Window window : Window.getWindows()) {
                adaptTree(window);
            }
        });
    }

    /**
     * Stops recoloring. Idempotent; invoked by {@link EveLookAndFeel#uninitialize()}.
     */
    static synchronized void uninstall() {
        if (!installed) {
            return;
        }
        Toolkit.getDefaultToolkit().removeAWTEventListener(LISTENER);
        installed = false;
    }

    /**
     * Recolors the text component if it still has hardcoded white background and black text.
     *
     * @param text text component, not null.
     * @return true if colors were replaced.
     */
    public static boolean adapt(JTextComponent text) {
        if (Boolean.TRUE.equals(text.getClientProperty(KEEP_COLORS))
                || !(UIManager.getLookAndFeel() instanceof EveLookAndFeel)
                || !isHardcoded(text.getBackground(), Color.WHITE)
                || !isHardcoded(text.getForeground(), Color.BLACK)) {
            return false;
        }
        // UIResource colors, so a later Look and Feel switch replaces them again.
        text.setBackground(EvePalette.SURFACE);
        text.setForeground(EvePalette.TEXT);
        return true;
    }

    private static void onHierarchyEvent(AWTEvent event) {
        if (event instanceof HierarchyEvent e
                && (e.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0
                && e.getComponent() instanceof JTextComponent text
                && text.isDisplayable()) {
            adapt(text);
        }
    }

    private static void adaptTree(Component component) {
        if (component instanceof JTextComponent text) {
            adapt(text);
        }
        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                adaptTree(child);
            }
        }
    }

    private static boolean isHardcoded(@Nullable Color color, Color expected) {
        return color != null && !(color instanceof UIResource) && color.getRGB() == expected.getRGB();
    }
}

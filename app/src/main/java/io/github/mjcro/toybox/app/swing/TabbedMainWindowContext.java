package io.github.mjcro.toybox.app.swing;

import io.github.mjcro.toybox.api.AbstractToy;
import io.github.mjcro.toybox.api.Environment;
import io.github.mjcro.toybox.api.Event;
import io.github.mjcro.toybox.api.Label;
import io.github.mjcro.toybox.api.events.SetWindowHintEvent;
import io.github.mjcro.toybox.api.events.SetWindowLabelEvent;
import io.github.mjcro.toybox.api.events.ShowToyEvent;
import io.github.mjcro.toybox.api.util.Util;
import io.github.mjcro.toybox.swing.hint.Hints;
import io.github.mjcro.toybox.swing.prefab.ToyBoxButtons;
import io.github.mjcro.toybox.swing.prefab.ToyBoxIcons;
import io.github.mjcro.toybox.swing.prefab.ToyBoxLabels;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.Optional;

/**
 * Context implementation for the tabbed main window, managing per-tab lifecycle
 * and event dispatching for individual toys.
 */
public class TabbedMainWindowContext extends AbstractWindowContext<TabbedMainWindow> {
    private @Nullable TabComponent tab = null;

    /**
     * Constructs the initial context for a tabbed main window.
     *
     * @param environment the application environment
     * @param mdi         the tabbed main window
     */
    TabbedMainWindowContext(@NonNull Environment environment, @NonNull TabbedMainWindow mdi) {
        super(environment, mdi, new JPopupMenu(), null);
        this.popupMenu.addPopupMenuListener(new OnPopup());
    }

    /**
     * Constructs a child context with initial data from a parent context.
     *
     * @param previous    the parent context
     * @param initialData the initial data for the new context
     */
    private TabbedMainWindowContext(@NonNull TabbedMainWindowContext previous, @Nullable Object initialData) {
        super(previous.getEnvironment(), previous.mainWindow, previous.popupMenu, initialData);
    }

    /**
     * Creates a new context sharing this context's window but carrying the given initial data.
     *
     * @param data the initial data for the new context
     * @return a new context with the given initial data
     */
    @NonNull TabbedMainWindowContext withInitialData(@Nullable Object data) {
        return new TabbedMainWindowContext(this, data);
    }

    @Override
    public void sendEvent(@Nullable Event event) {
        if (event == null) {
            return;
        }

        SwingUtilities.invokeLater(() -> {
            if (event instanceof SetWindowHintEvent) {
                String hint = ((SetWindowHintEvent) event).getHint();
                if (tab != null) {
                    tab.setHint(hint);
                }
            } else if (event instanceof SetWindowLabelEvent) {
                Optional<Label> opt = ((SetWindowLabelEvent) event).getLabel();
                if (tab != null && opt.isPresent()) {
                    opt.ifPresent(tab::setLabel);
                }
            } else if (event instanceof ShowToyEvent) {
                ShowToyEvent e = (ShowToyEvent) event;
                TabbedMainWindowContext context = withInitialData(e.getInitialData().orElse(null));
                context.showInContext(e.getToy());
            }
        });

        getEnvironment().handleEvent(this, event);
    }

    /**
     * Opens the given toy in a new tab within this context's window.
     *
     * @param toy the toy to display
     */
    private void showInContext(@NonNull AbstractToy toy) {
        JPanel panel = buildToyPanel(toy);
        attachPopup(panel);
        mainWindow.tabbedPane.addTab(
                toy.getLabel().getName(),
                panel
        );
        int index = mainWindow.tabbedPane.getTabCount() - 1;
        mainWindow.tabbedPane.setSelectedIndex(index);

        tab = new TabComponent(toy);
        tab.setOnCloseClick(e -> {
            for (int i = 0; i < mainWindow.tabbedPane.getTabCount(); i++) {
                if (mainWindow.tabbedPane.getTabComponentAt(i) == tab) {
                    mainWindow.tabbedPane.removeTabAt(i);
                    break;
                }
            }
        });
        mainWindow.tabbedPane.setTabComponentAt(index, tab);
    }

    /**
     * Custom tab component displaying the toy label, an optional hint, and a close button.
     */
    private static class TabComponent extends JPanel {
        private final @NonNull JLabel label = ToyBoxLabels.create();
        private final @NonNull JLabel hint = ToyBoxLabels.create();
        private final @NonNull JButton close = ToyBoxButtons.create();

        /**
         * Constructs a tab component for the given toy.
         *
         * @param toy the toy whose label to display
         */
        public TabComponent(@NonNull AbstractToy toy) {
            super(new BorderLayout());

            super.setOpaque(false);
            close.setBorderPainted(false);
            close.setBorder(null);
            close.setMargin(new Insets(0, 0, 0, 0));
            close.setContentAreaFilled(false);
            close.setToolTipText("Close tab");
            ToyBoxIcons.get("TitlePane.small.closeIcon").ifPresent(close::setIcon);
            super.add(close, BorderLayout.LINE_END);

            setLabel(toy.getLabel());
            Hints.TEXT_MINI.apply(hint);
            hint.setForeground(new Color(117, 117, 117));
            JPanel labels = new JPanel(new BorderLayout());
            labels.setBorder(new EmptyBorder(2, 0, 2, 6));
            labels.setOpaque(false);
            labels.add(label, BorderLayout.PAGE_START);
            labels.add(hint, BorderLayout.PAGE_END);
            super.add(labels, BorderLayout.LINE_START);
        }

        /**
         * Registers a listener to be called when the close button is clicked.
         *
         * @param l the action listener
         */
        public void setOnCloseClick(@NonNull ActionListener l) {
            close.addActionListener(l);
        }

        /**
         * Updates the tab's displayed label text and icon.
         *
         * @param l the label to display, or null to leave unchanged
         */
        public void setLabel(@Nullable Label l) {
            if (l != null) {
                label.setText(l.getName());
                l.getIconURI().flatMap(ToyBoxIcons::getSmall).ifPresent(label::setIcon);
            }
        }

        /**
         * Sets the hint text displayed below the tab label.
         *
         * @param s the hint text, or null/blank to clear
         */
        public void setHint(@Nullable String s) {
            if (s == null || s.isBlank()) {
                hint.setText(null);
            } else {
                s = s.toUpperCase(Locale.ROOT);
                if (s.length() > 20) {
                    s = s.substring(0, 20);
                }
                hint.setText(s.toUpperCase(Locale.ROOT));
                hint.setMaximumSize(new Dimension(100, Short.MAX_VALUE));
                hint.setPreferredSize(new Dimension(100, hint.getPreferredSize().height));
            }
        }
    }
}

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

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.Optional;

public class TabbedMainWindowContext extends AbstractWindowContext<TabbedMainWindow> {
    private TabComponent tab = null;

    TabbedMainWindowContext(Environment environment, TabbedMainWindow mdi) {
        super(environment, mdi, new JPopupMenu(), null);
        this.popupMenu.addPopupMenuListener(new OnPopup());
    }

    private TabbedMainWindowContext(TabbedMainWindowContext previous, Object initialData) {
        super(previous.getEnvironment(), previous.mainWindow, previous.popupMenu, initialData);
    }

    TabbedMainWindowContext withInitialData(Object data) {
        return new TabbedMainWindowContext(this, data);
    }

    @Override
    public void sendEvent(Event event) {
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

    private void showInContext(AbstractToy toy) {
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

    private static class TabComponent extends JPanel {
        private final JLabel label = ToyBoxLabels.create();
        private final JLabel hint = ToyBoxLabels.create();
        private final JButton close = ToyBoxButtons.create();

        public TabComponent(AbstractToy toy) {
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

        public void setOnCloseClick(ActionListener l) {
            close.addActionListener(l);
        }

        public void setLabel(Label l) {
            if (l != null) {
                label.setText(l.getName());
                l.getIconURI().flatMap(ToyBoxIcons::getSmall).ifPresent(label::setIcon);
            }
        }

        public void setHint(String s) {
            if (Util.isBlank(s)) {
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

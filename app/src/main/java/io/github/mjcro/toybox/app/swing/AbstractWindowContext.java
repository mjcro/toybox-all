package io.github.mjcro.toybox.app.swing;

import io.github.mjcro.interfaces.Decorator;
import io.github.mjcro.interfaces.strings.WithName;
import io.github.mjcro.interfaces.strings.WithText;
import io.github.mjcro.toybox.api.Action;
import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Environment;
import io.github.mjcro.toybox.api.Labeled;
import io.github.mjcro.toybox.app.Application;
import io.github.mjcro.toybox.swing.Components;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.JTextComponent;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
abstract class AbstractWindowContext<T extends Component> implements Context {
    @Getter
    @NonNull
    private final Environment environment;
    @NonNull
    protected final T mainWindow;
    @NonNull
    protected final JPopupMenu popupMenu;
    private final Object initialData;

    @Override
    public final Optional<Object> getInitialData() {
        return Optional.ofNullable(initialData);
    }

    protected void attachPopup(JPanel panel) {
        panel.setComponentPopupMenu(popupMenu);
        Components.setInheritedPopupRecursively(panel);
    }

    private void initPopup(Object target) {
        popupMenu.removeAll();
        fillPopup(target);
    }

    private void fillPopup(Object target) {
        System.out.println(target.getClass());
        if (target instanceof Decorator<?>) {
            fillPopup(((Decorator<?>) target).getDecorated());
        }
        if (target instanceof Instant) {
            Instant i = (Instant) target;
            addPopupText(elapsedSinceHuman(i));
            ZoneId zone = ZoneId.systemDefault();
            addPopupText(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss").withZone(zone).format(i) + " @" + zone.getDisplayName(TextStyle.NARROW, Locale.ROOT));
            addPopupElement(io.github.mjcro.toybox.api.Action.ofName("Copy instant unix", () -> getEnvironment().clipboardPut(String.valueOf(i.getEpochSecond()))));
            addPopupElement(io.github.mjcro.toybox.api.Action.ofName("Copy instant RFC-1123", () -> getEnvironment().clipboardPut(DateTimeFormatter.RFC_1123_DATE_TIME.withZone(ZoneOffset.UTC).format(i))));
            addPopupElement(io.github.mjcro.toybox.api.Action.ofName("Copy instant ISO", () -> getEnvironment().clipboardPut(DateTimeFormatter.ISO_INSTANT.format(i))));
        }
        if (target instanceof JTextComponent) {
            JTextComponent x = (JTextComponent) target;
            String selected = x.getSelectedText();
            if (selected == null || selected.isBlank()) {
                addPopupElement(io.github.mjcro.toybox.api.Action.ofName("Copy", () -> getEnvironment().clipboardPut(x.getText())));
            } else {
                addPopupElement(io.github.mjcro.toybox.api.Action.ofName("Copy Selected", () -> getEnvironment().clipboardPut(selected)));
                addPopupElement(io.github.mjcro.toybox.api.Action.ofName("Copy All", () -> getEnvironment().clipboardPut(x.getText())));
            }
            if (x.isEditable()) {
                addPopupElement(io.github.mjcro.toybox.api.Action.ofName("Paste", () -> getEnvironment().clipboardGetString().ifPresent(x::replaceSelection)));
            }
        }
        if (target instanceof JTable) {
            JTable x = (JTable) target;
            int row = x.getSelectedRow();
            int column = x.getSelectedColumn();
            if (row >= 0 && column >= 0) {
                Object v = x.getValueAt(row, column);
                if (v != null) {
                    fillPopup(v);
                    return;
                }
            }
        }
        if (target instanceof JTree) {
            JTree x = (JTree) target;
            Object v = x.getLastSelectedPathComponent();
            if (v instanceof DefaultMutableTreeNode) {
                v = ((DefaultMutableTreeNode) v).getUserObject();
                if (v != null) {
                    fillPopup(v);
                    return;
                }
            }
            if (v != null) {
                fillPopup(v);
                return;
            }
        }
        if (target instanceof CharSequence) {
            CharSequence x = (CharSequence) target;
            addPopupElement(io.github.mjcro.toybox.api.Action.ofName("Copy", () -> getEnvironment().clipboardPut(x)));
            if (x.length() < 2048) {
                String s = x.toString();
                if (s.startsWith("http://") || s.startsWith("https://")) {
                    addPopupElement(io.github.mjcro.toybox.api.Action.ofName("Open URL", () -> getEnvironment().openUrl(s)));
                }
            }
        }
        if (target instanceof Enum) {
            CharSequence x;
            if (target instanceof WithName) {
                x = ((WithName) target).getName();
            } else if (target instanceof WithText) {
                x = ((WithText) target).getText();
            } else {
                x = ((Enum<?>) target).name();
            }
            addPopupElement(io.github.mjcro.toybox.api.Action.ofName("Copy", () -> getEnvironment().clipboardPut(x)));
        }
        if (target instanceof Number) {
            Number x = (Number) target;
            addPopupElement(io.github.mjcro.toybox.api.Action.ofName("Copy", () -> getEnvironment().clipboardPut(String.valueOf(x))));
        }

        if (target instanceof Component && Application.DEBUG_COMPONENTS) {
            if (popupMenu.getComponents() != null && popupMenu.getComponents().length > 0) {
                popupMenu.addSeparator();
            }
            Component c = (Component) target;
            addPopupText("Component " + c.getClass().getName());
            Rectangle bounds = c.getBounds();
            addPopupText(String.format(Locale.ROOT, "x:%d,y:%d w:%d,h:%d", bounds.x, bounds.y, bounds.width, bounds.height));
        }

        // Using hooks
        for (Environment.PopupHook hook : getEnvironment().getPopupHooks()) {
            List<Labeled> items = hook.onPopup(this, target);
            if (items != null && !items.isEmpty()) {
                if (popupMenu.getComponents() != null && popupMenu.getComponents().length > 0) {
                    popupMenu.addSeparator();
                }
                for (Labeled item : items) {
                    if (item instanceof Action) {
                        addPopupElement((Action) item);
                    } else {
                        addPopupText(item.getName());
                    }
                }
            }
        }
    }

    private void addPopupElement(Action action) {
        JMenuItem item = new JMenuItem(action.getLabel().getName());
        item.addActionListener(e -> action.run());
        popupMenu.add(item);
    }

    private void addPopupText(String s) {
        JMenuItem item = new JMenuItem(s);
        item.setEnabled(false);
        popupMenu.add(item);
    }

    /* non-static */ class OnPopup implements PopupMenuListener {
        @Override
        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            if (e.getSource() == popupMenu) {
                Component invoker = popupMenu.getInvoker();
                if (invoker != null) {
                    initPopup(invoker);
                }
            }
        }

        @Override
        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {

        }

        @Override
        public void popupMenuCanceled(PopupMenuEvent e) {

        }
    }

    private static String elapsedSinceHuman(Instant instant) {
        if (instant == null || instant.getEpochSecond() == 0) {
            return "-";
        }

        Instant now = Instant.now();
        boolean past = now.compareTo(instant) > 0;
        Duration elapsed = past ? Duration.between(instant, now) : Duration.between(now, instant);
        String humanReadable;
        if (elapsed.toMinutes() <= 2) {
            humanReadable = elapsed.toSeconds() + "s";
        } else if (elapsed.toHours() <= 2) {
            humanReadable = elapsed.toMinutes() + "m";
        } else if (elapsed.toDays() <= 2) {
            humanReadable = elapsed.toHours() + "h";
        } else if (elapsed.toDays() < 500) {
            humanReadable = elapsed.toDays() + "d";
        } else if (elapsed.toDays() < 3000) {
            humanReadable = (elapsed.toDays() / 365) + "y";
        } else {
            humanReadable = "inf";
        }

        return (past ? "" : "in ") + humanReadable + (past ? " ago" : "");
    }
}

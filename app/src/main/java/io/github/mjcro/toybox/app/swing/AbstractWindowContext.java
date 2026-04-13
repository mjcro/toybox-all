package io.github.mjcro.toybox.app.swing;

import io.github.mjcro.interfaces.Decorator;
import io.github.mjcro.interfaces.strings.WithName;
import io.github.mjcro.interfaces.strings.WithText;
import io.github.mjcro.toybox.api.AbstractToy;
import io.github.mjcro.toybox.api.Action;
import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Environment;
import io.github.mjcro.toybox.api.Labeled;
import io.github.mjcro.toybox.app.Application;
import io.github.mjcro.toybox.swing.Components;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.JTextComponent;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.Component;
import java.awt.Rectangle;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Abstract base class for window contexts that associate a toy panel
 * with its environment, popup menu, and initial data.
 *
 * @param <T> the main window component type
 */
abstract class AbstractWindowContext<T extends Component> implements Context {

    private final @NonNull Environment environment;
    protected final @NonNull T mainWindow;
    protected final @NonNull JPopupMenu popupMenu;
    private final @Nullable Object initialData;

    /**
     * Creates a new window context.
     *
     * @param environment the application environment
     * @param mainWindow  the main window component
     * @param popupMenu   the shared popup menu
     * @param initialData optional initial data for the toy
     */
    protected AbstractWindowContext(
            @NonNull Environment environment,
            @NonNull T mainWindow,
            @NonNull JPopupMenu popupMenu,
            @Nullable Object initialData
    ) {
        this.environment = Objects.requireNonNull(environment, "environment");
        this.mainWindow = Objects.requireNonNull(mainWindow, "mainWindow");
        this.popupMenu = Objects.requireNonNull(popupMenu, "popupMenu");
        this.initialData = initialData;
    }

    @Override
    public @NonNull Environment getEnvironment() {
        return environment;
    }

    @Override
    public final @NonNull Optional<@NonNull Object> getInitialData() {
        return Optional.ofNullable(initialData);
    }

    /**
     * Attaches the shared popup menu to the given panel and all its children.
     *
     * @param panel the panel to attach the popup to
     */
    protected void attachPopup(@NonNull JPanel panel) {
        panel.setComponentPopupMenu(popupMenu);
        Components.setInheritedPopupRecursively(panel);
    }

    private void initPopup(@NonNull Object target) {
        popupMenu.removeAll();
        fillPopup(target);
    }

    /**
     * Builds the UI panel for the given toy.
     *
     * @param toy the toy to build
     * @return the constructed panel
     */
    protected @NonNull JPanel buildToyPanel(@Nullable AbstractToy toy) {
        if (toy == null) {
            throw new IllegalArgumentException("Unable to build panel for null toy");
        }
        JPanel panel;
        try {
            panel = toy.build(this);
        } catch (RuntimeException e) {
            throw new IllegalStateException(
                    String.format("Error building panel for toy '%s' %s", toy.getName(), toy.getClass().getName()),
                    e
            );
        }

        if (panel == null) {
            throw new IllegalStateException(
                    String.format("Toy '%s' %s created null panel - did you forget to override \"build\" method?", toy.getName(), toy.getClass().getName())
            );
        }

        return panel;
    }

    private void fillPopup(@Nullable Object target) {
        if (target instanceof Decorator<?>) {
            fillPopup(((Decorator<?>) target).getDecorated());
        }
        if (target instanceof Instant) {
            Instant i = (Instant) target;
            addPopupText(elapsedSinceHuman(i));
            ZoneId zone = ZoneId.systemDefault();
            addPopupText(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss").withZone(zone).format(i) + " @" + zone.getDisplayName(TextStyle.NARROW, Locale.ROOT));
            addPopupElement(Action.ofName("Copy instant unix", () -> getEnvironment().clipboardPut(String.valueOf(i.getEpochSecond()))));
            addPopupElement(Action.ofName("Copy instant RFC-1123", () -> getEnvironment().clipboardPut(DateTimeFormatter.RFC_1123_DATE_TIME.withZone(ZoneOffset.UTC).format(i))));
            addPopupElement(Action.ofName("Copy instant ISO", () -> getEnvironment().clipboardPut(DateTimeFormatter.ISO_INSTANT.format(i))));
        }
        if (target instanceof JTextComponent) {
            JTextComponent x = (JTextComponent) target;
            String selected = x.getSelectedText();
            if (selected == null || selected.isBlank()) {
                addPopupElement(Action.ofName("Copy", () -> getEnvironment().clipboardPut(x.getText())));
            } else {
                addPopupElement(Action.ofName("Copy Selected", () -> getEnvironment().clipboardPut(selected)));
                addPopupElement(Action.ofName("Copy All", () -> getEnvironment().clipboardPut(x.getText())));
            }
            if (x.isEditable()) {
                addPopupElement(Action.ofName("Paste", () -> getEnvironment().clipboardGetString().ifPresent(x::replaceSelection)));
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
            addPopupElement(Action.ofName("Copy", () -> getEnvironment().clipboardPut(x)));
            if (x.length() < 2048) {
                String s = x.toString();
                if (s.startsWith("http://") || s.startsWith("https://")) {
                    addPopupElement(Action.ofName("Open URL", () -> getEnvironment().openUrl(s)));
                }
            }
        }
        if (target instanceof Map.Entry<?, ?>) {
            Object key = ((Map.Entry<?, ?>) target).getKey();
            Object value = ((Map.Entry<?, ?>) target).getValue();
            if (key instanceof CharSequence) {
                addPopupElement(Action.ofName("Copy key", () -> getEnvironment().clipboardPut(key.toString())));
            }
            if (value instanceof CharSequence) {
                addPopupElement(Action.ofName("Copy value", () -> getEnvironment().clipboardPut(key.toString())));
            }
        }

        if (target instanceof Class<?>) {
            addPopupElement(Action.ofName("Copy class name", () -> getEnvironment().clipboardPut(((Class<?>) target).getName())));
        }

        if (target instanceof Exception) {
            addPopupElement(Action.ofName("Copy exception class name", () -> getEnvironment().clipboardPut(target.getClass().getName())));
            addPopupElement(Action.ofName("Copy exception message", () -> getEnvironment().clipboardPut(String.valueOf(((Exception) target).getMessage()))));
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
            addPopupElement(Action.ofName("Copy", () -> getEnvironment().clipboardPut(x)));
        }
        if (target instanceof Number) {
            Number x = (Number) target;
            addPopupElement(Action.ofName("Copy", () -> getEnvironment().clipboardPut(String.valueOf(x))));
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
        if (target == null) {
            return;
        }
        for (Environment.PopupHook hook : getEnvironment().getPopupHooks()) {
            List<Labeled> items = hook.onPopup(this, target);
            if (!items.isEmpty()) {
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

    private void addPopupElement(@NonNull Action action) {
        JMenuItem item = new JMenuItem(action.getLabel().getName());
        item.addActionListener(e -> action.run());
        popupMenu.add(item);
    }

    private void addPopupText(@NonNull String s) {
        JMenuItem item = new JMenuItem(s);
        item.setEnabled(false);
        popupMenu.add(item);
    }

    /**
     * Popup menu listener that initializes the popup content based on the invoker.
     */
    /* non-static */ class OnPopup implements PopupMenuListener {

        @Override
        public void popupMenuWillBecomeVisible(@NonNull PopupMenuEvent e) {
            if (e.getSource() == popupMenu) {
                Component invoker = popupMenu.getInvoker();
                if (invoker != null) {
                    initPopup(invoker);
                }
            }
        }

        @Override
        public void popupMenuWillBecomeInvisible(@NonNull PopupMenuEvent e) {
        }

        @Override
        public void popupMenuCanceled(@NonNull PopupMenuEvent e) {
        }
    }

    private static @NonNull String elapsedSinceHuman(@Nullable Instant instant) {
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

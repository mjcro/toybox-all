package io.github.mjcro.toybox.swing.widgets.panels;

import io.github.mjcro.toybox.api.Action;
import io.github.mjcro.toybox.swing.hint.Hints;
import io.github.mjcro.toybox.swing.prefab.ToyBoxButtons;
import org.jspecify.annotations.NonNull;

import javax.swing.JButton;
import javax.swing.JSeparator;
import java.awt.Component;
import java.awt.event.ActionListener;

/**
 * A horizontal panel that displays action buttons with optional separators.
 * Actions are rendered as buttons and can be enabled or disabled as a group.
 */
public class ActionBar extends HorizontalComponentsPanel {
    /**
     * Creates a new transparent action bar with normal padding.
     */
    public ActionBar() {
        super();
        setOpaque(false);
        Hints.PADDING_NORMAL.apply(this);
    }

    /**
     * Adds a button for the given action to this bar.
     *
     * @param action the action whose label and behavior define the button
     */
    public void addActionButton(@NonNull Action action) {
        final JButton button = ToyBoxButtons.create(action.getLabel().getName(), (ActionListener) e -> action.run());
        action.getLabel().getStyle().ifPresent($ -> new Hints.LaFStyle($).apply(button));
        add(button);
    }

    /**
     * Adds a visual separator between buttons.
     */
    public void addSeparator() {
        add(new JSeparator());
    }

    @Override
    public void setEnabled(boolean enabled) {
        for (final Component component : getComponents()) {
            if (component instanceof JButton) {
                component.setEnabled(enabled);
            }
        }
    }

    /**
     * Removes all components from this action bar.
     */
    public void clear() {
        super.removeAll();
    }
}

package io.github.mjcro.toybox.swing.widgets.panels;

import io.github.mjcro.toybox.api.Action;
import io.github.mjcro.toybox.swing.hint.Hints;
import io.github.mjcro.toybox.swing.prefab.ToyBoxButtons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ActionBar extends HorizontalComponentsPanel {
    public ActionBar() {
        super();
        setOpaque(false);
        Hints.PADDING_NORMAL.apply(this);
    }

    public void addActionButton(Action action) {
        JButton button = ToyBoxButtons.create(action.getLabel().getName(), (ActionListener) e -> action.run());
        action.getLabel().getStyle().ifPresent($ -> new Hints.LaFStyle($).apply(button));
        add(button);
    }

    public void addSeparator() {
        add(new JSeparator());
    }

    @Override
    public void setEnabled(boolean enabled) {
        for (Component component : getComponents()) {
            if (component instanceof JButton) {
                component.setEnabled(enabled);
            }
        }
    }

    public void clear() {
        super.removeAll();
    }
}

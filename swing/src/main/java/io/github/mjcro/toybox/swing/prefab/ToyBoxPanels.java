package io.github.mjcro.toybox.swing.prefab;

import io.github.mjcro.toybox.api.dev.Recommended;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Map;

/**
 * Contains utility methods to use for internal panels construction.
 */
@Recommended
public class ToyBoxPanels {
    /**
     * Constructs panel with border and title, containing given component.
     *
     * @param title     Panel title.
     * @param component Component to add.
     * @return Panel.
     */
    public static JPanel titledBordered(String title, Component component) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder(new EtchedBorder(), " " + title + " "));
        panel.add(component);
        return panel;
    }

    /**
     * Constructs panel containing given components positioned
     * in equal-width grid within one row.
     *
     * @param gap        Gap between components.
     * @param components Components to add.
     * @return Panel.
     */
    public static JPanel horizontalGrid(int gap, Component... components) {
        JPanel panel = new JPanel();
        if (components != null && components.length > 0) {
            panel.setLayout(new GridLayout(1, components.length, gap, gap));
            for (Component c : components) {
                panel.add(c);
            }
        }
        return panel;
    }

    /**
     * Constructs panel containing given components positioned
     * one on top of another, like rows. Each component can have
     * own height but all components will have same width.
     *
     * @param gap        Vertical gap between components.
     * @param components Components to add.
     * @return Panel.
     */
    public static JPanel verticalRows(int gap, Component... components) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        if (components != null) {
            for (int i = 0; i < components.length; i++) {
                if (gap > 0 && i > 0) {
                    panel.add(Box.createVerticalStrut(gap));
                }
                panel.add(components[i]);
            }
        }
        return panel;
    }

    /**
     * Constructs panel containing given components positioned
     * one on top of another, like rows. Each component can have
     * own height but all components will have same width.
     *
     * @param components Components to add.
     * @return Panel.
     */
    public static JPanel verticalRows(Component... components) {
        return verticalRows(0, components);
    }

    /**
     * Constructs panel containing elements in two columns, where
     * first (left) column will have minimal size while second (right)
     * column will have all the space left.
     *
     * @param entries Components to add.
     * @return Panel.
     */
    @SafeVarargs
    public static JPanel twoColumnsRight(Map.Entry<Component, Component>... entries) {
        JPanel panel = new JPanel();
        panel.setLayout(new MigLayout());
        if (entries != null) {
            for (Map.Entry<Component, Component> entry : entries) {
                panel.add(entry.getKey());
                panel.add(entry.getValue(), "w 100%,wrap");
            }
        }
        return panel;
    }

    /**
     * Private constructor for utility class.
     */
    private ToyBoxPanels() {
    }
}

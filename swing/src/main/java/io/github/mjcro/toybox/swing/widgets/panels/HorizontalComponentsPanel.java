package io.github.mjcro.toybox.swing.widgets.panels;

import io.github.mjcro.toybox.swing.Components;
import io.github.mjcro.toybox.swing.layouts.InlineBlockLayout;
import io.github.mjcro.toybox.swing.prefab.ToyBoxButtons;

import javax.swing.*;
import java.awt.*;

public class HorizontalComponentsPanel extends JPanel {
    public HorizontalComponentsPanel() {
        setLayout(new InlineBlockLayout());
    }

    public static void main(String[] args) {
        HorizontalComponentsPanel x = new HorizontalComponentsPanel();
        x.add(ToyBoxButtons.create("Hello, world"));
        x.add(ToyBoxButtons.create("Foo"));
        x.add(ToyBoxButtons.create("Lorem ipsum"));
        x.add(ToyBoxButtons.create("Bar"));
        x.add(ToyBoxButtons.create("Lorem ipsum dolor sit amet"));
        x.add(ToyBoxButtons.create("Lorem ipsum dolor sit amet"));
        x.add(ToyBoxButtons.create("Lorem ipsum dolor sit amet"));

        JPanel y = new JPanel();
        y.setLayout(new BorderLayout());
        y.add(new JPanel(), BorderLayout.CENTER);
        y.add(x, BorderLayout.PAGE_START);

        Components.show(y);
    }
}

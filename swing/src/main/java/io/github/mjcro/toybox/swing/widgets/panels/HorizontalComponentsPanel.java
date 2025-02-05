package io.github.mjcro.toybox.swing.widgets.panels;

import io.github.mjcro.toybox.swing.Components;
import io.github.mjcro.toybox.swing.factories.ButtonsFactory;
import io.github.mjcro.toybox.swing.layouts.InlineBlockLayout;

import javax.swing.*;
import java.awt.*;

public class HorizontalComponentsPanel extends JPanel {
    public HorizontalComponentsPanel() {
        setLayout(new InlineBlockLayout());
    }

    public static void main(String[] args) {
        HorizontalComponentsPanel x = new HorizontalComponentsPanel();
        x.add(ButtonsFactory.create("Hello, world"));
        x.add(ButtonsFactory.create("Foo"));
        x.add(ButtonsFactory.create("Lorem ipsum"));
        x.add(ButtonsFactory.create("Bar"));
        x.add(ButtonsFactory.create("Lorem ipsum dolor sit amet"));
        x.add(ButtonsFactory.create("Lorem ipsum dolor sit amet"));
        x.add(ButtonsFactory.create("Lorem ipsum dolor sit amet"));

        JPanel y = new JPanel();
        y.setLayout(new BorderLayout());
        y.add(new JPanel(), BorderLayout.CENTER);
        y.add(x, BorderLayout.PAGE_START);

        Components.show(y);
    }
}

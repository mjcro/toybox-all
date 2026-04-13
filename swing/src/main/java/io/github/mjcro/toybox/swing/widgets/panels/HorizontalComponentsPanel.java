package io.github.mjcro.toybox.swing.widgets.panels;

import io.github.mjcro.toybox.swing.Components;
import io.github.mjcro.toybox.swing.layouts.InlineBlockLayout;
import io.github.mjcro.toybox.swing.prefab.ToyBoxButtons;
import org.jspecify.annotations.NonNull;

import javax.swing.JPanel;
import java.awt.BorderLayout;

/**
 * A panel that lays out its child components horizontally using
 * {@link InlineBlockLayout}, wrapping to the next row when needed.
 */
public class HorizontalComponentsPanel extends JPanel {
    /**
     * Creates a new panel with inline-block layout.
     */
    public HorizontalComponentsPanel() {
        setLayout(new InlineBlockLayout());
    }

    /**
     * Demo entry point for testing the horizontal components panel.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(@NonNull String[] args) {
        final HorizontalComponentsPanel x = new HorizontalComponentsPanel();
        x.add(ToyBoxButtons.create("Hello, world"));
        x.add(ToyBoxButtons.create("Foo"));
        x.add(ToyBoxButtons.create("Lorem ipsum"));
        x.add(ToyBoxButtons.create("Bar"));
        x.add(ToyBoxButtons.create("Lorem ipsum dolor sit amet"));
        x.add(ToyBoxButtons.create("Lorem ipsum dolor sit amet"));
        x.add(ToyBoxButtons.create("Lorem ipsum dolor sit amet"));

        final JPanel y = new JPanel();
        y.setLayout(new BorderLayout());
        y.add(new JPanel(), BorderLayout.CENTER);
        y.add(x, BorderLayout.PAGE_START);

        Components.show(y);
    }
}

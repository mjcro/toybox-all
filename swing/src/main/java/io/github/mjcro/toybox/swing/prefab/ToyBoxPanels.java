package io.github.mjcro.toybox.swing.prefab;

import io.github.mjcro.toybox.api.dev.Recommended;
import net.miginfocom.swing.MigLayout;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Map;

/**
 * Contains utility methods for constructing common panel layouts.
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
    public static @NonNull JPanel titledBordered(@NonNull String title, @NonNull Component component) {
        final JPanel panel = new JPanel(new BorderLayout());
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
    public static @NonNull JPanel horizontalGrid(int gap, @Nullable Component... components) {
        final JPanel panel = new JPanel();
        if (components != null && components.length > 0) {
            panel.setLayout(new GridLayout(1, components.length, gap, gap));
            for (final Component c : components) {
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
    public static @NonNull JPanel verticalRows(int gap, @Nullable Component... components) {
        final JPanel panel = new JPanel();
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
    public static @NonNull JPanel verticalRows(@Nullable Component... components) {
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
    public static @NonNull JPanel twoColumnsRight(Map.@NonNull Entry<@NonNull Component, @NonNull Component> @Nullable ... entries) {
        final JPanel panel = new JPanel();
        panel.setLayout(new MigLayout());
        if (entries != null) {
            for (final Map.Entry<Component, Component> entry : entries) {
                panel.add(entry.getKey());
                panel.add(entry.getValue(), "w 100%,wrap");
            }
        }
        return panel;
    }

    /**
     * Constructs vertical split pane where top and bottom components
     * share height equally.
     *
     * @param top    Top component.
     * @param bottom Bottom component.
     * @return Split pane.
     */
    public static @NonNull JSplitPane verticalSplit(@NonNull Component top, @NonNull Component bottom) {
        return withRatio(new JSplitPane(JSplitPane.VERTICAL_SPLIT, top, bottom), .5d);
    }

    /**
     * Constructs vertical split pane where top component takes 40% of height
     * and bottom part holds fixed-height center component (e.g. toolbar)
     * above bottom component.
     * Components with non-empty titles are wrapped in titled border.
     *
     * @param topTitle    Top component title, optional.
     * @param top         Top component.
     * @param centerTitle Center component title, optional.
     * @param center      Fixed-height component placed above bottom one.
     * @param bottomTitle Bottom component title, optional.
     * @param bottom      Bottom component.
     * @return Split pane.
     */
    public static @NonNull JSplitPane verticalSplit(
            @Nullable String topTitle,
            @NonNull Component top,
            @Nullable String centerTitle,
            @NonNull Component center,
            @Nullable String bottomTitle,
            @NonNull Component bottom
    ) {
        final JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(titledBorderedIfPresent(centerTitle, center), BorderLayout.NORTH);
        bottomPanel.add(titledBorderedIfPresent(bottomTitle, bottom), BorderLayout.CENTER);

        return withRatio(
                new JSplitPane(JSplitPane.VERTICAL_SPLIT, titledBorderedIfPresent(topTitle, top), bottomPanel),
                .4d
        );
    }

    /**
     * Makes split pane start at given ratio and keep it on resize.
     *
     * @param splitPane Split pane to configure.
     * @param ratio     Share of space given to top (left) component, from 0 to 1.
     * @return Same split pane.
     */
    private static @NonNull JSplitPane withRatio(@NonNull JSplitPane splitPane, double ratio) {
        splitPane.setResizeWeight(ratio);
        // Proportional location is ignored until the pane has a size, so apply it on first resize
        splitPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (splitPane.getWidth() > 0 && splitPane.getHeight() > 0) {
                    splitPane.removeComponentListener(this);
                    splitPane.setDividerLocation(ratio);
                }
            }
        });
        return splitPane;
    }

    /**
     * Wraps component in titled border when title is present and not empty.
     *
     * @param title     Title, optional.
     * @param component Component to wrap.
     * @return Wrapped component or given one when title is absent.
     */
    private static @NonNull Component titledBorderedIfPresent(@Nullable String title, @NonNull Component component) {
        return title == null || title.isEmpty() ? component : titledBordered(title, component);
    }

    /**
     * Private constructor for utility class.
     */
    private ToyBoxPanels() {
    }
}

package io.github.mjcro.toybox.toys;

import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Label;
import io.github.mjcro.toybox.api.Menu;
import io.github.mjcro.toybox.api.Toy;
import io.github.mjcro.toybox.swing.Components;
import io.github.mjcro.toybox.swing.TypedDecorator;
import io.github.mjcro.toybox.swing.layouts.RowsLayout;
import io.github.mjcro.toybox.swing.prefab.ToyBoxButtons;
import io.github.mjcro.toybox.swing.prefab.ToyBoxPanels;
import io.github.mjcro.toybox.swing.prefab.ToyBoxTreeCellRenderers;
import org.jspecify.annotations.NonNull;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import java.awt.event.ActionEvent;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

/**
 * Toy that showcases available UI components such as buttons and tree renderers.
 */
public class ShowRoomComponentsToy implements Toy {
    @Override
    public @NonNull List<@NonNull Menu> getPath() {
        return List.of(Menu.TOYBOX_MENU, Menu.TOYBOX_DEVELOPMENT_MENU);
    }

    @Override
    public @NonNull Label getLabel() {
        return Label.ofIconAndName("fam://color_swatch", "Components showroom");
    }

    @Override
    public @NonNull JPanel build(@NonNull Context context) {
        return new Panel();
    }

    /**
     * Panel displaying the component showcase.
     */
    private static class Panel extends JPanel {
        /**
         * Constructs the showcase panel with buttons and tree examples.
         */
        public Panel() {
            setLayout(new RowsLayout());

            add(buildButtonsPanel());
            add(buildJTreePanel());
            add(buildJTreePanel());
        }

        /**
         * Builds the buttons showcase panel.
         *
         * @return a panel containing button examples
         */
        private @NonNull JPanel buildButtonsPanel() {
            return ToyBoxPanels.titledBordered("Buttons", ToyBoxPanels.horizontalGrid(
                    8,
                    ToyBoxButtons.create("Common"),
                    ToyBoxButtons.createPrimary("Primary", this::nothing),
                    ToyBoxButtons.createSuccess("Success", this::nothing),
                    ToyBoxButtons.createConfirm("Confirm", this::nothing),
                    ToyBoxButtons.createAdd("Add", this::nothing),
                    ToyBoxButtons.createWarning("Warning", this::nothing),
                    ToyBoxButtons.createDanger("Danger", this::nothing),
                    ToyBoxButtons.createDelete("Delete", this::nothing),
                    ToyBoxButtons.createCancel("Cancel", this::nothing)
            ));
        }

        /**
         * Builds the JTree showcase panel with custom cell renderers.
         *
         * @return a panel containing a tree example
         */
        private @NonNull JPanel buildJTreePanel() {
            JTree tree = new JTree();
            tree.setCellRenderer(ToyBoxTreeCellRenderers.typeSelector(Map.ofEntries(
                    new AbstractMap.SimpleEntry<>(TreeNodeType.NORMAL, new DefaultTreeCellRenderer()),
                    new AbstractMap.SimpleEntry<>(TreeNodeType.ICON, ToyBoxTreeCellRenderers.createWithIcon("fam://sport_8ball"))
            )));

            DefaultMutableTreeNode root = TreeNodeType.NORMAL.n("Root");
            root.add(TreeNodeType.NORMAL.n("Normal"));
            root.add(TreeNodeType.ICON.n("With icon"));

            tree.setModel(new DefaultTreeModel(root));
            JScrollPane pane = new JScrollPane(tree);
            Components.setMaxHeight(pane, 120);
            return ToyBoxPanels.titledBordered("JTree", pane);
        }

        /**
         * No-op action handler for demo buttons.
         *
         * @param e the action event
         */
        private void nothing(@NonNull ActionEvent e) {
        }
    }

    /**
     * Enumeration of tree node types used in the showcase to demonstrate
     * custom cell renderers.
     */
    private enum TreeNodeType {
        NORMAL,
        ICON;

        /**
         * Wraps a string value in a typed decorator with this node type.
         *
         * @param value the display value
         * @return a typed decorator
         */
        @NonNull TypedDecorator<@NonNull TreeNodeType, @NonNull String> v(@NonNull String value) {
            return new TypedDecorator<>(this, value);
        }

        /**
         * Creates a tree node with this type and the given display value.
         *
         * @param value the display value
         * @return a new mutable tree node
         */
        @NonNull DefaultMutableTreeNode n(@NonNull String value) {
            return new DefaultMutableTreeNode(v(value));
        }
    }
}

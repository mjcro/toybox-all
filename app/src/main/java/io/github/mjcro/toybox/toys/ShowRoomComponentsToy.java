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

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import java.awt.event.ActionEvent;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

public class ShowRoomComponentsToy implements Toy {
    @Override
    public List<Menu> getPath() {
        return List.of(Menu.TOYBOX_MENU, Menu.TOYBOX_DEVELOPMENT_MENU);
    }

    @Override
    public Label getLabel() {
        return Label.ofIconAndName("fam://color_swatch", "Components showroom");
    }

    @Override
    public JPanel build(Context context) {
        return new Panel();
    }

    private static class Panel extends JPanel {
        public Panel() {
            setLayout(new RowsLayout());

            add(buildButtonsPanel());
            add(buildJTreePanel());
            add(buildJTreePanel());
        }

        private JPanel buildButtonsPanel() {
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

        private JPanel buildJTreePanel() {
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

        private void nothing(ActionEvent e) {
        }
    }

    private enum TreeNodeType {
        NORMAL,
        ICON;

        TypedDecorator<TreeNodeType, String> v(String value) {
            return new TypedDecorator<>(this, value);
        }

        DefaultMutableTreeNode n(String value) {
            return new DefaultMutableTreeNode(v(value));
        }
    }
}

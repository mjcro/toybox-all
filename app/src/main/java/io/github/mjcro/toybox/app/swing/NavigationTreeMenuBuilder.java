package io.github.mjcro.toybox.app.swing;

import io.github.mjcro.toybox.api.Action;
import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Menu;
import io.github.mjcro.toybox.api.Toy;
import io.github.mjcro.toybox.app.NavigationTree;
import io.github.mjcro.toybox.swing.prefab.ToyBoxIcons;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;

public class NavigationTreeMenuBuilder {
    public JMenuBar buildMenuBar(NavigationTree tree) {
        JMenuBar menu = new JMenuBar();
        buildMenu(menu, tree);
        return menu;
    }

    public JMenuBar buildMenuBar(Context context, Collection<Toy> toys) {
        NavigationTree tree = new NavigationTree();
        for (Toy toy : toys) {
            ArrayList<Menu> path = new ArrayList<>(toy.getPath());
            path.add(Menu.toy(context, toy));
            tree.addPath(path);
        }

        return buildMenuBar(tree);
    }

    private void buildMenu(JMenuBar parent, NavigationTree tree) {
        for (NavigationTree.Node node : tree.getRoot().getNested()) {
            JMenuItem item = buildMenuItem(node);

            if (node.hasNested()) {
                buildSubmenuRecursively(item, node);
            }

            parent.add(item);
        }
    }

    private void buildSubmenuRecursively(JMenuItem parent, NavigationTree.Node node) {
        for (final NavigationTree.Node subnode : node.getNested()) {
            JMenuItem item = buildMenuItem(subnode);

            if (subnode.hasNested()) {
                buildSubmenuRecursively(item, subnode);
            }

            parent.add(item);
        }
    }

    private JMenuItem buildMenuItem(NavigationTree.Node node) {
        JMenuItem item = node.hasNested() ? new JMenu() : new JMenuItem();
        item.setText(node.getMenu().getName());
        node.getMenu().getLabel().getIconURI().flatMap(ToyBoxIcons::getSmall).ifPresent(item::setIcon);
        if (!node.hasNested() && node.getMenu() instanceof Action) {
            Action action = (Action) node.getMenu();
            item.addActionListener(e -> SwingUtilities.invokeLater(action));
        }
        return item;
    }
}

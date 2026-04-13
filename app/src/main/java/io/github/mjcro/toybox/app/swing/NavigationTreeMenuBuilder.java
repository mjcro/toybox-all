package io.github.mjcro.toybox.app.swing;

import io.github.mjcro.toybox.api.Action;
import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Menu;
import io.github.mjcro.toybox.api.Toy;
import io.github.mjcro.toybox.app.NavigationTree;
import io.github.mjcro.toybox.swing.prefab.ToyBoxIcons;
import org.jspecify.annotations.NonNull;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Builds Swing menu bars from a navigation tree or a collection of toys.
 */
public class NavigationTreeMenuBuilder {
    /**
     * Builds a menu bar from the given navigation tree.
     *
     * @param tree the navigation tree to convert
     * @return a populated menu bar
     */
    public @NonNull JMenuBar buildMenuBar(@NonNull NavigationTree tree) {
        JMenuBar menu = new JMenuBar();
        buildMenu(menu, tree);
        return menu;
    }

    /**
     * Builds a menu bar from the given collection of toys using the provided context.
     *
     * @param context the context used to create menu actions
     * @param toys    the toys to include in the menu
     * @return a populated menu bar
     */
    public @NonNull JMenuBar buildMenuBar(@NonNull Context context, @NonNull Collection<@NonNull Toy> toys) {
        NavigationTree tree = new NavigationTree();
        for (Toy toy : toys) {
            ArrayList<Menu> path = new ArrayList<>(toy.getPath());
            path.add(Menu.toy(context, toy));
            tree.addPath(path);
        }

        return buildMenuBar(tree);
    }

    /**
     * Populates a menu bar from the root nodes of a navigation tree.
     *
     * @param parent the menu bar to populate
     * @param tree   the navigation tree source
     */
    private void buildMenu(@NonNull JMenuBar parent, @NonNull NavigationTree tree) {
        for (NavigationTree.Node node : tree.getRoot().getNested()) {
            JMenuItem item = buildMenuItem(node);

            if (node.hasNested()) {
                buildSubmenuRecursively(item, node);
            }

            parent.add(item);
        }
    }

    /**
     * Recursively builds nested submenus for the given parent menu item.
     *
     * @param parent the parent menu item to attach children to
     * @param node   the navigation node whose children to process
     */
    private void buildSubmenuRecursively(@NonNull JMenuItem parent, NavigationTree.@NonNull Node node) {
        for (final NavigationTree.Node subnode : node.getNested()) {
            JMenuItem item = buildMenuItem(subnode);

            if (subnode.hasNested()) {
                buildSubmenuRecursively(item, subnode);
            }

            parent.add(item);
        }
    }

    /**
     * Creates a menu item from a single navigation node.
     *
     * @param node the navigation node
     * @return a JMenu if the node has children, otherwise a JMenuItem
     */
    private @NonNull JMenuItem buildMenuItem(NavigationTree.@NonNull Node node) {
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

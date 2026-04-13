package io.github.mjcro.toybox.swing.widgets;

import io.github.mjcro.toybox.swing.Components;
import io.github.mjcro.toybox.swing.TypedDecorator;
import io.github.mjcro.toybox.swing.hint.Hints;
import io.github.mjcro.toybox.swing.prefab.ToyBoxIcons;
import io.github.mjcro.toybox.swing.prefab.ToyBoxLaF;
import io.github.mjcro.toybox.swing.prefab.ToyBoxLabels;
import io.github.mjcro.toybox.swing.prefab.ToyBoxTreeCellRenderers;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.Collection;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Tree component that renders parsed JSON-like data structures
 * (maps, collections, and primitives) in a navigable tree.
 */
public class JsonJTree extends JTree {
    /**
     * Creates a tree pre-populated with the given data.
     *
     * @param data the data to display, or {@code null} for an empty tree
     */
    public JsonJTree(@Nullable Object data) {
        super(new DefaultTreeModel(null));
        setRootVisible(false);
        setCellRenderer(new Renderer());
        setData(data);
    }

    /**
     * Creates an empty JSON tree.
     */
    public JsonJTree() {
        this(null);
    }

    /**
     * Replaces the displayed data.
     *
     * @param data the data to display, or {@code null} for an empty tree
     */
    public void setData(@Nullable Object data) {
        final DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        setDataRecursively(root, data, null);
        setModel(new DefaultTreeModel(root));
    }

    /**
     * Expands all rows in the tree.
     */
    public void openAll() {
        for (int i = 0; i < getRowCount(); i++) {
            expandRow(i);
        }
    }

    /**
     * Recursively populates the tree model from the given data.
     *
     * @param parent  the parent tree node
     * @param data    the data element to add
     * @param keyName optional key name for labeling container nodes
     */
    private void setDataRecursively(@NonNull DefaultMutableTreeNode parent, @Nullable Object data, @Nullable String keyName) {
        if (data == null) {
            parent.add(new DefaultMutableTreeNode(null));
        } else if (data instanceof Map<?, ?>) {
            final Map<?, ?> map = (Map<?, ?>) data;
            final DefaultMutableTreeNode node = new DefaultMutableTreeNode(new TypedDecorator<>(Type.OBJECT, keyName == null ? "object" : keyName + ":"));
            parent.add(node);
            for (final Map.Entry<?, ?> entry : map.entrySet()) {
                setDataRecursively(node, entry, null);
            }
        } else if (data instanceof Collection<?>) {
            final Collection<?> collection = (Collection<?>) data;
            final DefaultMutableTreeNode node = new DefaultMutableTreeNode(new TypedDecorator<>(Type.COLLECTION, keyName));
            parent.add(node);
            for (final Object o : collection) {
                setDataRecursively(node, o, null);
            }
        } else if (data instanceof Map.Entry<?, ?>) {
            final Map.Entry<?, ?> entry = (Map.Entry<?, ?>) data;
            final String key = entry.getKey().toString();
            final Object value = entry.getValue();
            if (value == null) {
                parent.add(new DefaultMutableTreeNode(data));
            } else {
                final Class<?> valueClass = value.getClass();
                if (CharSequence.class.isAssignableFrom(valueClass)
                        || Number.class.isAssignableFrom(valueClass)
                        || valueClass == Boolean.class
                ) {
                    parent.add(new DefaultMutableTreeNode(data));
                } else if (Map.class.isAssignableFrom(valueClass) || Collection.class.isAssignableFrom(valueClass)) {
                    setDataRecursively(parent, value, key);
                }
            }
        } else {
            parent.add(new DefaultMutableTreeNode(data));
        }
    }

    /**
     * Tree cell renderer for JSON-like data nodes, displaying key-value pairs,
     * objects, and collections with distinct styling.
     */
    public static class Renderer extends TypedDecoratorCustomTreeCellRenderer<Type> {
        private final @NonNull KeyValuePanel panelKeyValue = new KeyValuePanel();

        private final @NonNull Color colorString = UIManager.getColor("Actions.Green");
        private final @NonNull Color colorNumber = UIManager.getColor("Actions.Blue");
        private final @NonNull Color colorBoolean = UIManager.getColor("Actions.Yellow");
        private final @NonNull Color colorOther = UIManager.getColor("Actions.Red");

        /**
         * Creates a new JSON tree cell renderer.
         */
        Renderer() {
            super(new EnumMap<>(Map.of(
                    Type.OBJECT,
                    ToyBoxTreeCellRenderers.createWithIcon("fam://table"),
                    Type.COLLECTION,
                    ToyBoxTreeCellRenderers.createWithIcon("fam://text_list_bullets")
            )));

            Hints.treeIcon("fam://bullet_black").apply(this);

            Hints.BOLD.apply(panelKeyValue.value);

            ToyBoxIcons.get("fam://bullet_black").ifPresent(panelKeyValue::setIcon);
        }

        @Override
        public @NonNull Component getTreeCellRendererComponent(
                @NonNull JTree tree,
                @Nullable Object value,
                boolean selected,
                boolean expanded,
                boolean leaf,
                int row,
                boolean hasFocus
        ) {
            if (value instanceof DefaultMutableTreeNode) {
                value = ((DefaultMutableTreeNode) value).getUserObject();
            }

            if (value instanceof Map.Entry<?, ?>) {
                final Map.Entry<?, ?> kv = (Map.Entry<?, ?>) value;
                final Object v = kv.getValue();
                panelKeyValue.key.setText(kv.getKey() + ":  ");
                panelKeyValue.value.setText(v == null ? "null" : v.toString());
                panelKeyValue.key.setForeground(selected ? colorSelectedFg : colorNormalFg);
                panelKeyValue.value.setForeground(selected ? colorSelectedFg : getFgColorFor(v));
                return panelKeyValue;
            }

            return super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        }

        /**
         * Returns the foreground color appropriate for the given value type.
         *
         * @param v the value to inspect
         * @return the corresponding foreground color
         */
        private @NonNull Color getFgColorFor(@Nullable Object v) {
            if (v instanceof CharSequence) {
                return colorString;
            } else if (v instanceof Number) {
                return colorNumber;
            } else if (v instanceof Boolean) {
                return colorBoolean;
            } else if (v == null) {
                return colorOther;
            }
            return colorNormalFg;
        }

        /**
         * Panel that renders a key-value pair as two adjacent labels.
         */
        private static class KeyValuePanel extends JPanel {
            private final @NonNull JLabel key = ToyBoxLabels.create();
            private final @NonNull JLabel value = ToyBoxLabels.create();

            KeyValuePanel() {
                super(new FlowLayout(FlowLayout.LEFT, 0, 0));
                setOpaque(false);
                add(key);
                add(value);
            }

            /**
             * Sets the icon displayed before the key label.
             *
             * @param icon the icon to set
             */
            public void setIcon(@NonNull Icon icon) {
                key.setIcon(icon);
            }
        }
    }

    /**
     * Node type discriminator for JSON-like tree structures.
     */
    private enum Type {
        /** Represents a JSON object (map). */
        OBJECT,
        /** Represents a JSON array (collection). */
        COLLECTION;
    }

    /**
     * Demo entry point for visual testing of the JSON tree.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(@NonNull String @NonNull [] args) {
        ToyBoxLaF.initialize(false);

        final Map<@NonNull String, @Nullable Object> data = new LinkedHashMap<>();
        data.put("foo", "bar");
        data.put("active", true);
        data.put("empty", null);
        data.put("kek", java.util.List.of(3, 4, 5));
        data.put("kek2", java.util.List.of());
        data.put("another", Map.of("one", 1, "two", 2L));

        Components.show(new JScrollPane(new JsonJTree(data)));
    }
}

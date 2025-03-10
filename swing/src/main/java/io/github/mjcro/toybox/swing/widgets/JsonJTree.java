package io.github.mjcro.toybox.swing.widgets;

import io.github.mjcro.toybox.swing.Components;
import io.github.mjcro.toybox.swing.TypedDecorator;
import io.github.mjcro.toybox.swing.hint.Hints;
import io.github.mjcro.toybox.swing.prefab.ToyBoxIcons;
import io.github.mjcro.toybox.swing.prefab.ToyBoxLaF;
import io.github.mjcro.toybox.swing.prefab.ToyBoxLabels;
import io.github.mjcro.toybox.swing.prefab.ToyBoxTreeCellRenderers;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.util.Collection;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class JsonJTree extends JTree {
    public JsonJTree(Object data) {
        super(new DefaultTreeModel(null));
        setRootVisible(false);
        setCellRenderer(new Renderer());
        setData(data);
    }

    public JsonJTree() {
        this(null);
    }

    public void setData(Object data) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        setDataRecursively(root, data, null);
        setModel(new DefaultTreeModel(root));
    }

    public void openAll() {
        for (int i = 0; i < getRowCount(); i++) {
            expandRow(i);
        }
    }

    private void setDataRecursively(DefaultMutableTreeNode parent, Object data, String keyName) {
        if (data == null) {
            parent.add(new DefaultMutableTreeNode(null));
        } else if (data instanceof Map<?, ?>) {
            Map<?, ?> map = (Map<?, ?>) data;
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(new TypedDecorator<>(Type.OBJECT, keyName == null ? "object" : keyName + ":"));
            parent.add(node);
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                setDataRecursively(node, entry, null);
            }
        } else if (data instanceof Collection<?>) {
            Collection<?> collection = (Collection<?>) data;
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(new TypedDecorator<>(Type.COLLECTION, keyName));
            parent.add(node);
            for (Object o : collection) {
                setDataRecursively(node, o, null);
            }
        } else if (data instanceof Map.Entry<?, ?>) {
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) data;
            String key = entry.getKey().toString();
            Object value = entry.getValue();
            if (value == null) {
                parent.add(new DefaultMutableTreeNode(data));
            } else {
                Class<?> valueClass = value.getClass();
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

    public static class Renderer extends TypedDecoratorCustomTreeCellRenderer<Type> {
        private final KeyValuePanel panelKeyValue = new KeyValuePanel();

        private final Color colorString = UIManager.getColor("Actions.Green");
        private final Color colorNumber = UIManager.getColor("Actions.Blue");
        private final Color colorBoolean = UIManager.getColor("Actions.Yellow");
        private final Color colorOther = UIManager.getColor("Actions.Red");

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
        public Component getTreeCellRendererComponent(
                JTree tree,
                Object value,
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
                Map.Entry<?, ?> kv = (Map.Entry<?, ?>) value;
                Object v = kv.getValue();
                panelKeyValue.key.setText(kv.getKey() + ":  ");
                panelKeyValue.value.setText(v == null ? "null" : v.toString());
                panelKeyValue.key.setForeground(selected ? colorSelectedFg : colorNormalFg);
                panelKeyValue.value.setForeground(selected ? colorSelectedFg : getFgColorFor(v));
                return panelKeyValue;
            }

            return super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        }

        private Color getFgColorFor(Object v) {
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

        private static class KeyValuePanel extends JPanel {
            private final JLabel key = ToyBoxLabels.create();
            private final JLabel value = ToyBoxLabels.create();

            KeyValuePanel() {
                super(new FlowLayout(FlowLayout.LEFT, 0, 0));
                setOpaque(false);
                add(key);
                add(value);
            }

            public void setIcon(Icon icon) {
                key.setIcon(icon);
            }
        }
    }

    private enum Type {
        OBJECT, COLLECTION;
    }

    public static void main(String[] args) {
        ToyBoxLaF.initialize(false);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("foo", "bar");
        data.put("active", true);
        data.put("empty", null);
        data.put("kek", java.util.List.of(3, 4, 5));
        data.put("kek2", java.util.List.of());
        data.put("another", Map.of("one", 1, "two", 2L));

        Components.show(new JScrollPane(new JsonJTree(data)));
    }
}

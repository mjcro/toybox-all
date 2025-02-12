package io.github.mjcro.toybox.swing.widgets;

import io.github.mjcro.toybox.swing.Components;
import io.github.mjcro.toybox.swing.Icons;
import io.github.mjcro.toybox.swing.Styles;
import io.github.mjcro.toybox.swing.ToyboxLaF;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.util.Collection;
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
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(new ObjectContainer(keyName));
            parent.add(node);
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                setDataRecursively(node, entry, null);
            }
        } else if (data instanceof Collection<?>) {
            Collection<?> collection = (Collection<?>) data;
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(new CollectionContainer(keyName));
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

    public static class Renderer extends CustomTreeCellRenderer {
        private final JLabel labelContainerObject = new JLabel("");
        private final JLabel labelContainerCollection = new JLabel("");
        private final JLabel labelKeyOnly = new JLabel();
        private final JLabel label = new JLabel();
        private final KeyValuePanel panelKeyValue = new KeyValuePanel();

        private final Color colorString = UIManager.getColor("Actions.Green");
        private final Color colorNumber = UIManager.getColor("Actions.Blue");
        private final Color colorBoolean = UIManager.getColor("Actions.Yellow");
        private final Color colorOther = UIManager.getColor("Actions.Red");

        Renderer() {
            Styles.BOLD.apply(panelKeyValue.value);

            Icons.get("fam://table").ifPresent(labelContainerObject::setIcon);
            Icons.get("fam://text_list_bullets").ifPresent(labelContainerCollection::setIcon);
            Icons.get("fam://bullet_black").ifPresent(labelKeyOnly::setIcon);
            Icons.get("fam://bullet_black").ifPresent(panelKeyValue::setIcon);
            Icons.get("fam://bullet_black").ifPresent(label::setIcon);
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

            if (value instanceof ObjectContainer) {
                String keyName = ((ObjectContainer) value).key;
                labelContainerObject.setText(keyName == null ? "object" : keyName + ":");
                labelContainerObject.setForeground(selected ? colorSelectedFg : colorNormalFg);
                return labelContainerObject;
            } else if (value instanceof CollectionContainer) {
                String keyName = ((CollectionContainer) value).key;
                labelContainerCollection.setText(keyName == null ? "collection" : keyName + ":");
                labelContainerCollection.setForeground(selected ? colorSelectedFg : colorNormalFg);
                return labelContainerCollection;
            } else if (value instanceof KeyOnly) {
                KeyOnly ko = (KeyOnly) value;
                labelKeyOnly.setText(ko.key + ":");
                labelKeyOnly.setForeground(selected ? colorSelectedFg : colorNormalFg);
                return labelKeyOnly;
            } else if (value instanceof Map.Entry<?, ?>) {
                Map.Entry<?, ?> kv = (Map.Entry<?, ?>) value;
                Object v = kv.getValue();
                panelKeyValue.key.setText(kv.getKey() + ":  ");
                panelKeyValue.value.setText(v == null ? "null" : v.toString());
                panelKeyValue.key.setForeground(selected ? colorSelectedFg : colorNormalFg);
                panelKeyValue.value.setForeground(selected ? colorSelectedFg : getFgColorFor(v));
                return panelKeyValue;
            } else {
                label.setText(value == null ? "null" : value.toString());
                label.setForeground(selected ? colorSelectedFg : getFgColorFor(value));
                return label;
            }
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
            private final JLabel key = new JLabel();
            private final JLabel value = new JLabel();

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

    private static class KeyOnly {
        private final String key;

        private KeyOnly(String key) {
            this.key = key;
        }
    }

    private static class ObjectContainer {
        private final String key;

        private ObjectContainer(String key) {
            this.key = key;
        }
    }

    private static class CollectionContainer {
        private final String key;

        private CollectionContainer(String key) {
            this.key = key;
        }
    }

    public static void main(String[] args) {
        ToyboxLaF.initialize(false);

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

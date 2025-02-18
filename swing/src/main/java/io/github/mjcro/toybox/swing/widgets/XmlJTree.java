package io.github.mjcro.toybox.swing.widgets;

import io.github.mjcro.toybox.swing.Components;
import io.github.mjcro.toybox.swing.Icons;
import io.github.mjcro.toybox.swing.Styles;
import io.github.mjcro.toybox.swing.ToyboxLaF;
import io.github.mjcro.toybox.swing.TypedDecorator;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.EnumMap;
import java.util.Map;

public class XmlJTree extends JTree {
    public XmlJTree() {
        super(new DefaultTreeModel(null));
        setRootVisible(true);
        setCellRenderer(new Renderer());
    }

    public XmlJTree(Document data) {
        this();
        setData(data);
    }

    public XmlJTree(String data) throws ParserConfigurationException, IOException, SAXException {
        this();
        setData(data);
    }

    public void setData(Document data) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Document");
        setDataRecursively(root, data);
        setModel(new DefaultTreeModel(root));
    }

    public void setData(String data) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        setData(db.parse(new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8))));
    }

    public void openAll() {
        for (int i = 0; i < getRowCount(); i++) {
            expandRow(i);
        }
    }

    private void setDataRecursively(DefaultMutableTreeNode parent, Node data) {
        short nodeType = data.getNodeType();
        String nodeName = data.getNodeName();

        if (nodeType == Node.COMMENT_NODE) {
            return;
        }

        if (nodeType == Node.ELEMENT_NODE) {
            DefaultMutableTreeNode x = new DefaultMutableTreeNode(new TypedDecorator<>(Type.ELEMENT, nodeName));
            parent.add(x);
            parent = x;
        }
        if (nodeType == Node.TEXT_NODE) {
            String s = data.getTextContent();
            if (s != null && !s.isBlank()) {
                DefaultMutableTreeNode x = new DefaultMutableTreeNode(s.strip());
                parent.add(x);
            }
            return;
        }

        NamedNodeMap attributes = data.getAttributes();
        if (attributes != null && attributes.getLength() > 0) {
            for (int i = 0; i < attributes.getLength(); i++) {
                Node item = attributes.item(i);
                String name = item.getNodeName();

                Map.Entry<String, String> entry = new AbstractMap.SimpleEntry<>(name, item.getTextContent());

                if ("id".equalsIgnoreCase(name)) {
                    DefaultMutableTreeNode x = new DefaultMutableTreeNode(new TypedDecorator<>(Type.ATTR_ID, entry));
                    parent.add(x);
                } else if (name.startsWith("xmlns")) {
                    DefaultMutableTreeNode x = new DefaultMutableTreeNode(new TypedDecorator<>(Type.ATTR_NS, entry));
                    parent.add(x);
                } else {
                    DefaultMutableTreeNode x = new DefaultMutableTreeNode(new TypedDecorator<>(Type.ATTR, entry));
                    parent.add(x);
                }
            }
        }

        NodeList childNodes = data.getChildNodes();
        if (childNodes != null && childNodes.getLength() > 0) {
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node item = childNodes.item(i);
                setDataRecursively(parent, item);
            }
        }
    }

    public enum Type {
        ELEMENT, ATTR, ATTR_ID, ATTR_NS;
    }

    public static class Renderer extends TypedDecoratorCustomTreeCellRenderer<Type> {
        public Renderer() {
            super(new EnumMap<>(Map.of(
                    Type.ELEMENT,
                    hinted(Styles.treeIcon("fam://tag")),
                    Type.ATTR,
                    new KeyValueRenderer(Icons.get("fam://tag_blue").orElse(null)),
                    Type.ATTR_ID,
                    new KeyValueRenderer(Icons.get("fam://tag_green").orElse(null)),
                    Type.ATTR_NS,
                    new KeyValueRenderer(Icons.get("fam://tag_purple").orElse(null))
            )));
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
            return super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        }
    }

    private static class KeyValueRenderer extends CustomTreeCellRenderer {
        private final JPanel panel;
        private final JLabel key;
        private final JLabel value;

        private KeyValueRenderer(Icon icon) {
            this.panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            this.key = new JLabel();
            this.value = new JLabel();
            this.key.setIcon(icon);
            this.panel.add(this.key);
            this.panel.add(this.value);
            this.panel.setOpaque(false);
            Styles.ITALIC.apply(this.key);
        }

        @Override
        public Component getTreeCellRendererComponent(
                JTree tree,
                Object value,
                boolean sel,
                boolean expanded,
                boolean leaf,
                int row,
                boolean hasFocus
        ) {
            if (value instanceof Map.Entry<?, ?>) {
                Map.Entry<?, ?> entry = (Map.Entry<?, ?>) value;
                this.key.setText(entry.getKey() == null ? null : entry.getKey().toString() + ":");
                this.value.setText(entry.getValue() == null ? null : entry.getValue().toString());

                this.key.setForeground(sel ? colorSelectedFg : colorNormalFg);
                this.value.setForeground(sel ? colorSelectedFg : colorNormalFg);

                return panel;
            }
            return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        }
    }

    public static void main(String[] args) throws Exception {
        ToyboxLaF.initialize(false);

        var xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<EDoc></EDoc>";

        Components.show(new JScrollPane(new XmlJTree(xml)));
    }
}

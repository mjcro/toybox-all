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
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.Component;
import java.awt.FlowLayout;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.EnumMap;
import java.util.Map;

/**
 * A {@link JTree} that renders an XML {@link Document} as a navigable tree
 * with distinct icons for elements, attributes, ID attributes, and namespace attributes.
 */
public class XmlJTree extends JTree {
    /**
     * Creates a new empty XML tree.
     */
    public XmlJTree() {
        super(new DefaultTreeModel(null));
        setRootVisible(true);
        setCellRenderer(new Renderer());
    }

    /**
     * Creates a new XML tree displaying the given DOM document.
     *
     * @param data the XML document to display
     */
    public XmlJTree(@NonNull Document data) {
        this();
        setData(data);
    }

    /**
     * Creates a new XML tree by parsing the given XML string.
     *
     * @param data the XML string to parse and display
     * @throws ParserConfigurationException if the parser cannot be configured
     * @throws IOException                  if an I/O error occurs during parsing
     * @throws SAXException                 if the XML is malformed
     */
    public XmlJTree(@NonNull String data) throws ParserConfigurationException, IOException, SAXException {
        this();
        setData(data);
    }

    /**
     * Replaces the tree content with the given DOM document.
     *
     * @param data the XML document to display
     */
    public void setData(@NonNull Document data) {
        final DefaultMutableTreeNode root = new DefaultMutableTreeNode("Document");
        setDataRecursively(root, data);
        setModel(new DefaultTreeModel(root));
    }

    /**
     * Parses the given XML string and replaces the tree content.
     *
     * @param data the XML string to parse and display
     * @throws ParserConfigurationException if the parser cannot be configured
     * @throws IOException                  if an I/O error occurs during parsing
     * @throws SAXException                 if the XML is malformed
     */
    public void setData(@NonNull String data) throws ParserConfigurationException, IOException, SAXException {
        final DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        setData(db.parse(new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8))));
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
     * Recursively converts DOM nodes into tree nodes.
     *
     * @param parent the parent tree node to add children to
     * @param data   the DOM node to convert
     */
    private void setDataRecursively(@NonNull DefaultMutableTreeNode parent, @NonNull Node data) {
        final short nodeType = data.getNodeType();
        final String nodeName = data.getNodeName();

        if (nodeType == Node.COMMENT_NODE) {
            return;
        }

        if (nodeType == Node.ELEMENT_NODE) {
            final DefaultMutableTreeNode x = new DefaultMutableTreeNode(new TypedDecorator<>(Type.ELEMENT, nodeName));
            parent.add(x);
            parent = x;
        }
        if (nodeType == Node.TEXT_NODE) {
            final String s = data.getTextContent();
            if (s != null && !s.isBlank()) {
                final DefaultMutableTreeNode x = new DefaultMutableTreeNode(s.strip());
                parent.add(x);
            }
            return;
        }

        final NamedNodeMap attributes = data.getAttributes();
        if (attributes != null && attributes.getLength() > 0) {
            for (int i = 0; i < attributes.getLength(); i++) {
                final Node item = attributes.item(i);
                final String name = item.getNodeName();

                final Map.Entry<@NonNull String, @NonNull String> entry = new AbstractMap.SimpleEntry<>(name, item.getTextContent());

                if ("id".equalsIgnoreCase(name)) {
                    final DefaultMutableTreeNode x = new DefaultMutableTreeNode(new TypedDecorator<>(Type.ATTR_ID, entry));
                    parent.add(x);
                } else if (name.startsWith("xmlns")) {
                    final DefaultMutableTreeNode x = new DefaultMutableTreeNode(new TypedDecorator<>(Type.ATTR_NS, entry));
                    parent.add(x);
                } else {
                    final DefaultMutableTreeNode x = new DefaultMutableTreeNode(new TypedDecorator<>(Type.ATTR, entry));
                    parent.add(x);
                }
            }
        }

        final NodeList childNodes = data.getChildNodes();
        if (childNodes != null && childNodes.getLength() > 0) {
            for (int i = 0; i < childNodes.getLength(); i++) {
                final Node item = childNodes.item(i);
                setDataRecursively(parent, item);
            }
        }
    }

    /** Enum distinguishing XML node categories for rendering. */
    public enum Type {
        ELEMENT, ATTR, ATTR_ID, ATTR_NS;
    }

    /**
     * Default tree cell renderer for {@link XmlJTree} that unwraps
     * {@link DefaultMutableTreeNode} user objects before delegating.
     */
    public static class Renderer extends TypedDecoratorCustomTreeCellRenderer<Type> {
        /**
         * Creates a new renderer with icon mappings for each XML node type.
         */
        public Renderer() {
            super(new EnumMap<>(Map.of(
                    Type.ELEMENT,
                    ToyBoxTreeCellRenderers.createWithIcon("fam://tag"),
                    Type.ATTR,
                    new KeyValueRenderer(ToyBoxIcons.get("fam://tag_blue").orElse(null)),
                    Type.ATTR_ID,
                    new KeyValueRenderer(ToyBoxIcons.get("fam://tag_green").orElse(null)),
                    Type.ATTR_NS,
                    new KeyValueRenderer(ToyBoxIcons.get("fam://tag_purple").orElse(null))
            )));
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
            return super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        }
    }

    /**
     * Renders key-value attribute pairs as two side-by-side labels.
     */
    private static class KeyValueRenderer extends CustomTreeCellRenderer {
        private final @NonNull JPanel panel;
        private final @NonNull JLabel key;
        private final @NonNull JLabel value;

        /**
         * Creates a new key-value renderer with the given icon.
         *
         * @param icon the icon to display beside the key label, or {@code null} for no icon
         */
        private KeyValueRenderer(@Nullable Icon icon) {
            this.panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            this.key = ToyBoxLabels.create();
            this.value = ToyBoxLabels.create();
            this.key.setIcon(icon);
            this.panel.add(this.key);
            this.panel.add(this.value);
            this.panel.setOpaque(false);
            Hints.ITALIC.apply(this.key);
        }

        @Override
        public @NonNull Component getTreeCellRendererComponent(
                @NonNull JTree tree,
                @Nullable Object value,
                boolean sel,
                boolean expanded,
                boolean leaf,
                int row,
                boolean hasFocus
        ) {
            if (value instanceof Map.Entry<?, ?>) {
                final Map.Entry<?, ?> entry = (Map.Entry<?, ?>) value;
                this.key.setText(entry.getKey() == null ? null : entry.getKey().toString() + ":");
                this.value.setText(entry.getValue() == null ? null : entry.getValue().toString());

                this.key.setForeground(sel ? colorSelectedFg : colorNormalFg);
                this.value.setForeground(sel ? colorSelectedFg : colorNormalFg);

                return panel;
            }
            return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        }
    }

    /**
     * Demo entry point for testing the XML tree component.
     *
     * @param args command-line arguments (unused)
     * @throws Exception if XML parsing fails
     */
    public static void main(@NonNull String[] args) throws Exception {
        ToyBoxLaF.initialize(false);

        final var xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<EDoc></EDoc>";

        Components.show(new JScrollPane(new XmlJTree(xml)));
    }
}

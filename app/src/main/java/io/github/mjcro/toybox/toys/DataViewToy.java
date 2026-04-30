package io.github.mjcro.toybox.toys;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Label;
import io.github.mjcro.toybox.api.Menu;
import io.github.mjcro.toybox.api.Toy;
import io.github.mjcro.toybox.swing.Components;
import io.github.mjcro.toybox.swing.hint.Hints;
import io.github.mjcro.toybox.swing.prefab.ToyBoxButtons;
import io.github.mjcro.toybox.swing.prefab.ToyBoxLaF;
import io.github.mjcro.toybox.swing.prefab.ToyBoxTextComponents;
import io.github.mjcro.toybox.swing.util.Slf4jUtil;
import io.github.mjcro.toybox.swing.widgets.ExceptionDetailsJPanel;
import io.github.mjcro.toybox.swing.widgets.JsonJTree;
import io.github.mjcro.toybox.swing.widgets.XmlJTree;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Component;
import java.util.List;

/**
 * Toy that renders structured data (JSON, XML) as a tree or formatted text.
 */
public class DataViewToy implements Toy {
    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull List<@NonNull Menu> getPath() {
        return List.of(Menu.TOYBOX_BASIC_TOOLS_MENU);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull Label getLabel() {
        return Label.ofIconAndName("fam://tag", "Data view");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull JPanel build(@NonNull Context context) {
        return new Panel();
    }

    /**
     * Inner panel that provides the data-view UI with an input area,
     * mode chooser, and a split pane for the rendered result.
     */
    private static class Panel extends JPanel {
        private static final @NonNull Logger log = LoggerFactory.getLogger(Panel.class);

        private final @NonNull JTextArea input = ToyBoxTextComponents.createBigTextArea(Hints.TEXT_MONOSPACED);
        private final @NonNull JSplitPane pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        private final @NonNull JComboBox<@NonNull Mode> modeChooser = new JComboBox<>(new Mode[]{
                Mode.JSON_TREE,
                Mode.JSON_PRETTY,
                Mode.XML_TREE
        });

        /**
         * Constructs the data view panel.
         */
        Panel() {
            super(new BorderLayout());

            add(buildHeaderPanel(), BorderLayout.PAGE_START);
            add(buildContentPanel(), BorderLayout.CENTER);
        }

        /**
         * Builds the header panel containing the mode selector and apply button.
         *
         * @return the header panel
         */
        private @NonNull JPanel buildHeaderPanel() {
            JPanel panel = new JPanel();
            JButton apply = ToyBoxButtons.create("Apply", Hints.onAction(this::apply));
            panel.add(modeChooser);
            panel.add(apply);
            return panel;
        }

        /**
         * Builds the content panel containing the split pane with input and result areas.
         *
         * @return the content panel
         */
        private @NonNull JPanel buildContentPanel() {
            JPanel panel = new JPanel(new BorderLayout());
            pane.setResizeWeight(.5d);
            pane.add(new JScrollPane(input), JSplitPane.TOP);
            pane.add(new JPanel(), JSplitPane.BOTTOM);

            panel.add(pane);
            return panel;
        }

        /**
         * Replaces the bottom half of the split pane with the given component.
         *
         * @param component the component to display as the result
         */
        private void setResult(@NonNull Component component) {
            int loc = pane.getDividerLocation();
            pane.add(component, JSplitPane.BOTTOM);
            pane.setDividerLocation(loc);
            Components.setInheritedPopupRecursively(pane);
        }

        /**
         * Replaces the bottom half of the split pane with a read-only text area
         * showing the given string.
         *
         * @param string the text to display
         */
        private void setResult(@NonNull String string) {
            JTextArea textArea = ToyBoxTextComponents.createBigTextArea(
                    string,
                    Hints.NOT_EDITABLE_TEXT,
                    Hints.TEXT_MONOSPACED
            );
            setResult(new JScrollPane(textArea));
        }

        /**
         * Parses the input text according to the selected mode and displays the result.
         */
        private void apply() {
            try {
                switch ((Mode) modeChooser.getSelectedItem()) {
                    case JSON_TREE: {
                        ObjectMapper mapper = new ObjectMapper();
                        Object data = mapper.readValue(input.getText(), Object.class);
                        setResult(new JScrollPane(new JsonJTree(data)));
                    }
                    break;
                    case JSON_PRETTY: {
                        ObjectMapper mapper = new ObjectMapper();
                        Object data = mapper.readValue(input.getText(), Object.class);
                        setResult(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data));
                    }
                    break;
                    case XML_TREE:
                        setResult(new JScrollPane(new XmlJTree(input.getText())));
                        break;
                }
            } catch (Exception e) {
                log.error(Slf4jUtil.TOYBOX_MARKER, "Error parsing JSON", e);
                setResult(new ExceptionDetailsJPanel(e));
            }
        }
    }

    /**
     * Enumeration of supported data-view modes.
     */
    private enum Mode {
        /** Renders JSON as an expandable tree. */
        JSON_TREE("JSON tree"),
        /** Formats JSON with indentation. */
        JSON_PRETTY("Formatted JSON"),
        /** Renders XML as an expandable tree. */
        XML_TREE("XML tree");

        private final @NonNull String displayName;

        /**
         * Constructs a mode with the given display name.
         *
         * @param displayName the human-readable name
         */
        Mode(@NonNull String displayName) {
            this.displayName = displayName;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public @NonNull String toString() {
            return displayName;
        }
    }

    /**
     * Standalone entry point for testing the data view panel.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(@NonNull String[] args) {
        ToyBoxLaF.initialize(false);

        Components.show(new Panel());
    }
}

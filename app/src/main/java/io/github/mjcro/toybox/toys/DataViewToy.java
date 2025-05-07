package io.github.mjcro.toybox.toys;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Label;
import io.github.mjcro.toybox.api.Menu;
import io.github.mjcro.toybox.api.Toy;
import io.github.mjcro.toybox.swing.Components;
import io.github.mjcro.toybox.swing.hint.Hints;
import io.github.mjcro.toybox.swing.prefab.ToyBoxLaF;
import io.github.mjcro.toybox.swing.prefab.ToyBoxButtons;
import io.github.mjcro.toybox.swing.prefab.ToyBoxTextComponents;
import io.github.mjcro.toybox.swing.widgets.ExceptionDetailsJPanel;
import io.github.mjcro.toybox.swing.widgets.JsonJTree;
import io.github.mjcro.toybox.swing.widgets.XmlJTree;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.util.List;

@Slf4j
public class DataViewToy implements Toy {
    @Override
    public List<Menu> getPath() {
        return List.of(Menu.TOYBOX_BASIC_TOOLS_MENU);
    }

    @Override
    public Label getLabel() {
        return Label.ofIconAndName("fam://tag", "Data view");
    }

    @Override
    public JPanel build(Context context) {
        return new Panel();
    }

    private static class Panel extends JPanel {
        private final JTextArea input = ToyBoxTextComponents.createJTextArea();
        private final JSplitPane pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        private final JComboBox<Mode> modeChooser = new JComboBox<>(new Mode[]{
                Mode.JSON_TREE,
                Mode.JSON_PRETTY,
                Mode.XML_TREE
        });

        Panel() {
            super(new BorderLayout());

            add(buildHeaderPanel(), BorderLayout.PAGE_START);
            add(buildContentPanel(), BorderLayout.CENTER);
        }

        private JPanel buildHeaderPanel() {
            JPanel panel = new JPanel();
            JButton apply = ToyBoxButtons.create("Apply", Hints.onAction(this::apply));
            panel.add(modeChooser);
            panel.add(apply);
            return panel;
        }

        private JPanel buildContentPanel() {
            JPanel panel = new JPanel(new BorderLayout());
            pane.setResizeWeight(.5d);
            pane.add(new JScrollPane(input), JSplitPane.TOP);
            pane.add(new JPanel(), JSplitPane.BOTTOM);

            panel.add(pane);
            return panel;
        }

        private void setResult(Component component) {
            int loc = pane.getDividerLocation();
            pane.add(component, JSplitPane.BOTTOM);
            pane.setDividerLocation(loc);
            Components.setInheritedPopupRecursively(pane);
        }

        private void setResult(String string) {
            JTextArea textArea = ToyBoxTextComponents.createJTextArea(
                    string,
                    Hints.NOT_EDITABLE_TEXT,
                    Hints.TEXT_MONOSPACED
            );
            setResult(new JScrollPane(textArea));
        }

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
                log.error("Error parsing JSON", e);
                setResult(new ExceptionDetailsJPanel(e));
            }
        }
    }

    private enum Mode {
        JSON_TREE("JSON tree"),
        JSON_PRETTY("Formatted JSON"),
        XML_TREE("XML tree");

        private final String displayName;

        Mode(String displayName) {
            this.displayName = displayName;
        }


        @Override
        public String toString() {
            return displayName;
        }
    }

    public static void main(String[] args) {
        ToyBoxLaF.initialize(false);

        Components.show(new Panel());
    }
}

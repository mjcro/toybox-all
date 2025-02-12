package io.github.mjcro.toybox.toys;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Label;
import io.github.mjcro.toybox.api.Menu;
import io.github.mjcro.toybox.api.Toy;
import io.github.mjcro.toybox.swing.Components;
import io.github.mjcro.toybox.swing.Styles;
import io.github.mjcro.toybox.swing.ToyboxLaF;
import io.github.mjcro.toybox.swing.widgets.ExceptionDetailsJPanel;
import io.github.mjcro.toybox.swing.widgets.JsonJTree;
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
        return Label.ofIconAndName("fam://tag", "JSON view");
    }

    @Override
    public JPanel build(Context context) {
        return new Panel();
    }

    private static class Panel extends JPanel {
        private final JTextArea input = new JTextArea();
        private final JSplitPane pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        Panel() {
            super(new BorderLayout());

            add(buildHeaderPanel(), BorderLayout.PAGE_START);
            add(buildContentPanel(), BorderLayout.CENTER);
        }

        private JPanel buildHeaderPanel() {
            JPanel panel = new JPanel();
            JButton apply = new JButton("Apply");
            Styles.onAction(this::apply).apply(apply);
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
        }

        private void apply() {
            try {
                ObjectMapper mapper = new ObjectMapper();
                Object data = mapper.readValue(input.getText(), Object.class);
                setResult(new JScrollPane(new JsonJTree(data)));
            } catch (Exception e) {
                log.error("Error parsing JSON", e);
                setResult(new ExceptionDetailsJPanel(e));
            }
        }
    }

    public static void main(String[] args) {
        ToyboxLaF.initialize(false);

        Components.show(new Panel());
    }
}

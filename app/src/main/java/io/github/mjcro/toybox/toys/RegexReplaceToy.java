package io.github.mjcro.toybox.toys;

import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Label;
import io.github.mjcro.toybox.api.Menu;
import io.github.mjcro.toybox.api.Toy;
import io.github.mjcro.toybox.swing.Components;
import io.github.mjcro.toybox.swing.hint.Hints;
import io.github.mjcro.toybox.swing.prefab.ToyBoxButtons;
import io.github.mjcro.toybox.swing.prefab.ToyBoxLaF;
import io.github.mjcro.toybox.swing.prefab.ToyBoxLabels;
import io.github.mjcro.toybox.swing.widgets.ExceptionDetailsJPanel;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
public class RegexReplaceToy implements Toy {
    @Override
    public List<Menu> getPath() {
        return List.of(Menu.TOYBOX_BASIC_TOOLS_MENU);
    }

    @Override
    public Label getLabel() {
        return Label.ofIconAndName("fam://sum", "Regexp replace");
    }

    @Override
    public JPanel build(Context context) {
        return new Panel();
    }

    private static class Panel extends JPanel {
        private final JComboBox<Preset> preset = new JComboBox<>();
        private final JTextField
                pattern = new JTextField(),
                replacement = new JTextField();
        private final JTextArea input = new JTextArea();
        private final JTextArea output = new JTextArea();
        private final ExceptionDetailsJPanel exceptionDetails = new ExceptionDetailsJPanel();
        private final JSplitPane pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        public Panel() {
            super(new BorderLayout());

            add(buildHeader(), BorderLayout.PAGE_START);
            add(buildInputOutput(), BorderLayout.CENTER);
        }

        private Component buildHeader() {
            JPanel inputs = new JPanel(new MigLayout());

            preset.setEditable(false);
            preset.setModel(new DefaultComboBoxModel<>(new Preset[]{
                    new Preset("None", "", ""),
                    new Preset("Concat number lines using comma", "(\\d+)\\n", "$1,"),
            }));
            preset.addActionListener(e -> {
                Object item = preset.getSelectedItem();
                if (item instanceof Preset) {
                    Preset p = (Preset) item;
                    pattern.setText(p.pattern);
                    replacement.setText(p.replacement);
                }
            });

            Hints.setPreferredWidth(400).apply(pattern);
            Hints.setPreferredWidth(400).apply(replacement);

            inputs.add(ToyBoxLabels.create("Preset"));
            inputs.add(preset, "w 100%, wrap");
            inputs.add(ToyBoxLabels.create("Pattern"));
            inputs.add(pattern, "w 100%, wrap");
            inputs.add(ToyBoxLabels.create("Replacement"));
            inputs.add(replacement, "w 100%, wrap");

            JPanel header = new JPanel(new BorderLayout());
            header.add(inputs, BorderLayout.CENTER);

            JButton apply = ToyBoxButtons.createPrimary("Apply", this::onApply);

            JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttons.add(apply);
            header.add(buttons, BorderLayout.PAGE_END);

            return header;
        }

        private Component buildInputOutput() {
            output.setEditable(false);

            pane.add(new JScrollPane(input), JSplitPane.TOP);
            pane.add(new JScrollPane(output), JSplitPane.BOTTOM);
            pane.setResizeWeight(0.5);
            return pane;
        }

        private void setOutput(Component component) {
            int loc = pane.getDividerLocation();
            pane.add(component, JSplitPane.BOTTOM);
            pane.setDividerLocation(loc);
            Components.setInheritedPopupRecursively(pane);
        }

        private void onApply(final ActionEvent e) {
            Components.setEnabled(false, preset, pattern, replacement, input);

            try {
                Pattern p = Pattern.compile(pattern.getText());
                output.setText(p.matcher(input.getText()).replaceAll(replacement.getText().replace("\\n", "\n")));

                setOutput(new JScrollPane(output));
            } catch (Throwable ex) {
                log.error("Error applying regex", ex);
                exceptionDetails.setException(ex);
                setOutput(exceptionDetails);
            } finally {
                Components.setEnabled(true, preset, pattern, replacement, input);
            }
        }

        private static class Preset {
            private final String name;
            private final String pattern;
            private final String replacement;

            private Preset(String name, String pattern, String replacement) {
                this.name = name;
                this.pattern = pattern;
                this.replacement = replacement;
            }

            @Override
            public String toString() {
                return name;
            }
        }
    }

    public static void main(String[] args) {
        ToyBoxLaF.initialize(false);
        Components.show(new Panel());
    }
}

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
import io.github.mjcro.toybox.swing.prefab.ToyBoxPanels;
import io.github.mjcro.toybox.swing.prefab.ToyBoxTextComponents;
import io.github.mjcro.toybox.swing.util.Slf4jUtil;
import io.github.mjcro.toybox.swing.widgets.MultiViewTextAreaOrExceptionPanel;
import net.miginfocom.swing.MigLayout;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Toy that performs regular expression search-and-replace on text input.
 */
public class RegexReplaceToy implements Toy {
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
        return Label.ofIconAndName("fam://sum", "Regexp replace");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull JPanel build(@NonNull Context context) {
        return new Panel();
    }

    /**
     * Inner panel providing the regex replace UI with pattern/replacement fields,
     * preset selector, and split input/output areas.
     */
    private static class Panel extends JPanel {
        private static final @NonNull Logger log = LoggerFactory.getLogger(Panel.class);

        private final @NonNull JComboBox<@NonNull Preset> preset = new JComboBox<>();
        private final @NonNull JTextField
                pattern = ToyBoxTextComponents.createTextField(),
                replacement = ToyBoxTextComponents.createTextField();
        private final @NonNull JTextArea input = ToyBoxTextComponents.createBigTextArea();
        private final @NonNull MultiViewTextAreaOrExceptionPanel output = new MultiViewTextAreaOrExceptionPanel("");
        private final @NonNull JSplitPane pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        /**
         * Constructs the regex replace panel.
         */
        public Panel() {
            super(new BorderLayout());

            add(buildHeader(), BorderLayout.PAGE_START);
            add(buildInputOutput(), BorderLayout.CENTER);
        }

        /**
         * Builds the header component containing the preset selector, pattern/replacement
         * text fields, and the apply button.
         *
         * @return the header component
         */
        private @NonNull Component buildHeader() {
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

            return ToyBoxPanels.titledBordered("Settings", header);
        }

        /**
         * Builds the split pane containing the input and output text areas.
         *
         * @return the input/output component
         */
        private @NonNull Component buildInputOutput() {
            Hints.TEXT_MONOSPACED.apply(input);
            Hints.NOT_EDITABLE_TEXT.apply(output.getTextArea());
            Hints.TEXT_MONOSPACED.apply(output.getTextArea());

            pane.add(ToyBoxPanels.titledBordered("Input", new JScrollPane(input)));
            pane.add(ToyBoxPanels.titledBordered("Output", output));
            pane.setResizeWeight(0.5);
            return pane;
        }

        /**
         * Applies the regex replacement to the input text and displays the result.
         *
         * @param e the triggering action event
         */
        private void onApply(@NonNull ActionEvent e) {
            Components.setEnabled(false, preset, pattern, replacement, input);

            try {
                Pattern p = Pattern.compile(pattern.getText());
                String out = p.matcher(input.getText()).replaceAll(replacement.getText().replace("\\n", "\n"));
                output.setViewText(out);
            } catch (Throwable ex) {
                log.error(Slf4jUtil.TOYBOX_MARKER, "Error applying regex", ex);
                output.setViewException(ex);
            } finally {
                Components.setEnabled(true, preset, pattern, replacement, input);
            }
        }

        /**
         * A named preset containing pre-filled pattern and replacement values.
         */
        private static class Preset {
            private final @NonNull String name;
            private final @NonNull String pattern;
            private final @NonNull String replacement;

            /**
             * Constructs a preset.
             *
             * @param name        the display name
             * @param pattern     the regex pattern
             * @param replacement the replacement string
             */
            private Preset(@NonNull String name, @NonNull String pattern, @NonNull String replacement) {
                this.name = name;
                this.pattern = pattern;
                this.replacement = replacement;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public @NonNull String toString() {
                return name;
            }
        }
    }

    /**
     * Standalone entry point for testing the regex replace panel.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(@NonNull String[] args) {
        ToyBoxLaF.initialize(false);
        Components.show(new Panel());
    }
}

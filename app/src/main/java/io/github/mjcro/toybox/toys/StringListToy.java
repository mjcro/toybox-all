package io.github.mjcro.toybox.toys;

import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Label;
import io.github.mjcro.toybox.api.Menu;
import io.github.mjcro.toybox.api.Toy;
import io.github.mjcro.toybox.swing.hint.Hints;
import io.github.mjcro.toybox.swing.prefab.ToyBoxButtons;
import io.github.mjcro.toybox.swing.prefab.ToyBoxPanels;
import io.github.mjcro.toybox.swing.prefab.ToyBoxTextComponents;
import io.github.mjcro.toybox.swing.util.Slf4jUtil;
import io.github.mjcro.toybox.swing.widgets.panels.HorizontalComponentsPanel;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Toy for sorting, deduplicating, trimming and filtering string lists.
 */
public class StringListToy implements Toy {
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
        return Label.ofIconAndName("fam://text_linespacing", "String List Tools");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull JPanel build(@NonNull Context context) {
        Panel panel = new Panel();
        context.getInitialData()
                .filter($ -> $ instanceof CharSequence)
                .map(Object::toString)
                .ifPresent(panel::setSourceText);

        return panel;
    }

    /**
     * Inner panel providing the string-list manipulation UI with checkboxes
     * for sort, unique, trim and skip-empty, plus source/result text areas.
     */
    private static final class Panel extends JPanel {
        private static final @NonNull Logger log = LoggerFactory.getLogger(Panel.class);

        private final @NonNull JTextArea
                sourceText = ToyBoxTextComponents.createTextArea(),
                resultText = ToyBoxTextComponents.createTextArea(Hints.NOT_EDITABLE_TEXT);

        private final @NonNull JCheckBox
                useSort = new JCheckBox("Sorted"),
                useSkipEmptyLines = new JCheckBox("Skip empty lines"),
                useTrimSpaces = new JCheckBox("Trim spaces"),
                useUnique = new JCheckBox("Unique");

        /**
         * Constructs the string list panel.
         */
        Panel() {
            super(new BorderLayout());

            add(buildHeader(), BorderLayout.PAGE_START);
            add(buildTextArea(), BorderLayout.CENTER);
        }

        /**
         * Builds the header panel with option checkboxes and the apply button.
         *
         * @return the header panel
         */
        private @NonNull JPanel buildHeader() {
            JPanel panel = new JPanel(new BorderLayout());

            panel.add(ToyBoxButtons.createPrimary("Apply", this::onApply), BorderLayout.LINE_END);

            HorizontalComponentsPanel checkboxes = new HorizontalComponentsPanel();
            checkboxes.add(useSort);
            checkboxes.add(useUnique);
            checkboxes.add(useSkipEmptyLines);
            checkboxes.add(useTrimSpaces);

            useSkipEmptyLines.setSelected(true);
            useTrimSpaces.setSelected(true);

            panel.add(checkboxes, BorderLayout.CENTER);

            return ToyBoxPanels.titledBordered("Settings", panel);
        }

        /**
         * Builds the split pane containing source and result text areas.
         *
         * @return the text area component
         */
        private @NonNull Component buildTextArea() {
            JSplitPane pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            pane.setResizeWeight(.5d);
            pane.add(new JScrollPane(sourceText));
            pane.add(new JScrollPane(resultText));

            return pane;
        }

        /**
         * Applies the selected transformations to the source text and updates the result.
         *
         * @param e the triggering action event
         */
        private void onApply(@NonNull ActionEvent e) {
            String source = sourceText.getText();
            String[] lines = source.split("\n");

            Stream<String> stream = Arrays.stream(lines);
            if (useSkipEmptyLines.isSelected()) {
                stream = stream.filter(Predicate.not(String::isBlank));
            }
            if (useTrimSpaces.isSelected()) {
                stream = stream.map(String::strip);
            }
            if (useUnique.isSelected()) {
                stream = stream.distinct();
            }
            if (useSort.isSelected()) {
                stream = stream.sorted();
            }

            AtomicInteger lineCounter = new AtomicInteger();
            stream = stream.peek(s -> lineCounter.incrementAndGet());

            resultText.setText(stream.collect(Collectors.joining("\n")));
            log.info(
                    Slf4jUtil.TOYBOX_MARKER,
                    "Source text with {} bytes and {} lines mapped to {} lines",
                    source.getBytes(StandardCharsets.UTF_8).length,
                    lines.length,
                    lineCounter.get()
            );
        }

        /**
         * Sets the source text area content.
         *
         * @param text the text to set, may be {@code null}
         */
        public void setSourceText(@Nullable CharSequence text) {
            sourceText.setText(text == null ? null : text.toString());
        }
    }
}

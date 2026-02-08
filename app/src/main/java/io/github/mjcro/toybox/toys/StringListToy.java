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
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class StringListToy implements Toy {
    @Override
    public List<Menu> getPath() {
        return List.of(Menu.TOYBOX_BASIC_TOOLS_MENU);
    }

    @Override
    public Label getLabel() {
        return Label.ofIconAndName("fam://text_linespacing", "String List Tools");
    }

    @Override
    public JPanel build(Context context) {
        Panel panel = new Panel();
        context.getInitialData()
                .filter($ -> $ instanceof CharSequence)
                .map(Object::toString)
                .ifPresent(panel::setSourceText);

        return panel;
    }

    private static final class Panel extends JPanel {
        private final JTextArea
                sourceText = ToyBoxTextComponents.createJTextArea(),
                resultText = ToyBoxTextComponents.createJTextArea(Hints.NOT_EDITABLE_TEXT);

        private final JCheckBox
                useSort = new JCheckBox("Sorted"),
                useSkipEmptyLines = new JCheckBox("Skip empty lines"),
                useTrimSpaces = new JCheckBox("Trim spaces"),
                useUnique = new JCheckBox("Unique");

        Panel() {
            super(new BorderLayout());

            add(buildHeader(), BorderLayout.PAGE_START);
            add(buildTextArea(), BorderLayout.CENTER);
        }

        private JPanel buildHeader() {
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

        private Component buildTextArea() {
            JSplitPane pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            pane.setResizeWeight(.5d);
            pane.add(new JScrollPane(sourceText));
            pane.add(new JScrollPane(resultText));

            return pane;
        }

        private void onApply(final ActionEvent e) {
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

        public void setSourceText(final CharSequence text) {
            sourceText.setText(text == null ? null : text.toString());
        }
    }
}

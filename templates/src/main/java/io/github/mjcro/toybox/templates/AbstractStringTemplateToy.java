package io.github.mjcro.toybox.templates;

import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Environment;
import io.github.mjcro.toybox.api.Toy;
import io.github.mjcro.toybox.swing.Components;
import io.github.mjcro.toybox.swing.hint.Hints;
import io.github.mjcro.toybox.swing.prefab.ToyBoxButtons;
import io.github.mjcro.toybox.swing.prefab.ToyBoxPanels;
import io.github.mjcro.toybox.swing.prefab.ToyBoxTextComponents;
import io.github.mjcro.toybox.swing.util.Slf4jUtil;
import io.github.mjcro.toybox.swing.widgets.MultiViewTextAreaOrExceptionPanel;
import io.github.mjcro.toybox.swing.widgets.panels.HorizontalComponentsPanel;
import io.github.mjcro.toybox.swing.widgets.panels.VerticalRowsPanel;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

/**
 * Abstract base for toys that produce string output from data objects
 * with configurable bindings.
 */
public abstract class AbstractStringTemplateToy implements Toy {
    private static final @NonNull Logger log = LoggerFactory.getLogger(AbstractStringTemplateToy.class);

    /**
     * Returns the data object that provides bindings for this template.
     *
     * @param context the toy context
     * @return the string producer data object
     */
    protected abstract @NonNull StringProducer getDataObject(@NonNull Context context);

    @Override
    public @NonNull JPanel build(@NonNull Context context) {
        return new Panel(context.getEnvironment(), getDataObject(context));
    }

    /**
     * Panel that renders template bindings and output.
     */
    public static class Panel extends JPanel {
        private final @NonNull Environment environment;
        private final @NonNull JPanel inputs;
        private final @NonNull MultiViewTextAreaOrExceptionPanel output = new MultiViewTextAreaOrExceptionPanel();
        private final @NonNull JButton applyButton;
        private final @NonNull JButton copyToClipButton;
        private final @NonNull JButton saveToFileButton;
        private final @NonNull JCheckBox autoCheckbox = new JCheckBox("auto");

        private @NonNull List<@NonNull Binding> bindings = new ArrayList<>();
        private @Nullable StringProducer object = null;

        private final @NonNull JTextField hash;

        /**
         * Creates a new template panel.
         *
         * @param environment the application environment
         * @param dataObject  the initial data object, or {@code null}
         */
        public Panel(@NonNull Environment environment, @Nullable StringProducer dataObject) {
            super.setLayout(new BorderLayout());
            Hints.PADDING_NORMAL.apply(this);
            this.environment = environment;

            // Outputs block
            if (dataObject != null) {
                dataObject.getInitialString().ifPresent(output::setViewText);
            }
            Hints.NOT_EDITABLE_TEXT.apply(output.getTextArea());
            Hints.TEXT_MONOSPACED.apply(output.getTextArea());
            super.add(output, BorderLayout.CENTER);

            // Inputs
            JPanel top = new JPanel();
            top.setLayout(new BorderLayout());
            inputs = new JPanel(new BorderLayout());
            top.add(inputs, BorderLayout.CENTER);

            JPanel topFooter = new JPanel();
            topFooter.setBorder(new EmptyBorder(4, 0, 4, 0));
            topFooter.setLayout(new BorderLayout());

            JPanel buttons = new JPanel();
            saveToFileButton = ToyBoxButtons.create("Save to file", this::onSaveToFileClick);
            saveToFileButton.setEnabled(false);
            buttons.add(saveToFileButton);
            copyToClipButton = ToyBoxButtons.create("Copy to clipboard", this::onCopyToClipboardClick);
            copyToClipButton.setEnabled(false);
            buttons.add(copyToClipButton);
            autoCheckbox.setSelected(true);
            buttons.add(autoCheckbox);
            applyButton = ToyBoxButtons.createPrimary("Apply", this::onApplyButtonClick);
            buttons.add(applyButton);
            topFooter.add(buttons, BorderLayout.LINE_END);

            hash = ToyBoxTextComponents.createTextField(Hints.NOT_EDITABLE_TEXT);
            hash.setBorder(new EmptyBorder(0, 10, 0, 10));
            hash.setOpaque(false);
            hash.setToolTipText("MD5 hash of resulting text");
            topFooter.add(hash, BorderLayout.CENTER);
            top.add(topFooter, BorderLayout.PAGE_END);
            super.add(top, BorderLayout.PAGE_START);

            if (dataObject != null) {
                setDataObject(dataObject);
            }
        }

        private void onCopyToClipboardClick(@NonNull ActionEvent e) {
            environment.clipboardPut(output.getViewText());
        }

        private void onSaveToFileClick(@NonNull ActionEvent e) {
            environment.chooseFileToSave(new Environment.FileCallback() {
                @Override
                public void onFileChosen(@NonNull File file) throws IOException {
                    if (log.isDebugEnabled(Slf4jUtil.TOYBOX_MARKER)) {
                        log.debug(Slf4jUtil.TOYBOX_MARKER, "Saving to file {}", file);
                    }
                    try {
                        Files.write(file.toPath(), output.getViewText().getBytes(StandardCharsets.UTF_8));
                        if (log.isErrorEnabled(Slf4jUtil.TOYBOX_MARKER)) {
                            log.info(Slf4jUtil.TOYBOX_MARKER, "Data saved to file {}", file);
                        }
                    } catch (IOException err) {
                        if (log.isErrorEnabled(Slf4jUtil.TOYBOX_MARKER)) {
                            log.error(Slf4jUtil.TOYBOX_MARKER, "Error saving data to file {}", file, err);
                        }
                        throw err;
                    }
                }

                @Override
                public void onNoFileChosen() {
                    log.info("File save cancelled");
                }
            }, null);
        }

        private void onApplyButtonClick(@NonNull ActionEvent e) {
            doApply();
        }

        private void doAutoApply() {
            if (autoCheckbox.isSelected()) {
                doApply();
            }
        }

        private void doApply() {
            final StringProducer producer = object;
            if (producer == null) {
                return;
            }
            applyButton.setEnabled(false);
            autoCheckbox.setEnabled(false);
            saveToFileButton.setEnabled(false);
            copyToClipButton.setEnabled(false);
            for (Binding b : bindings) {
                b.setEnabled(false);
            }

            Instant before = Instant.now();

            environment.execute(ui -> {
                StringBuilder sb = new StringBuilder();
                try {
                    for (Binding b : bindings) {
                        try {
                            b.applyCurrentValue();
                        } catch (Throwable err) {
                            throw new BindingValueApplyException(b, err);
                        }
                    }
                    producer.produce(sb);
                    String text = sb.toString();
                    hash.setText(md5Hex(text));
                    output.setViewText(text);
                    saveToFileButton.setEnabled(!text.isEmpty());
                    copyToClipButton.setEnabled(!text.isEmpty());
                    applyButton.setEnabled(true);
                    if (log.isDebugEnabled(Slf4jUtil.TOYBOX_MARKER)) {
                        log.debug(Slf4jUtil.TOYBOX_MARKER, "Template evaluated in {}", Duration.between(before, Instant.now()));
                    }
                } catch (Throwable err) {
                    output.setViewException(err);
                    if (log.isErrorEnabled(Slf4jUtil.TOYBOX_MARKER)) {
                        log.error(Slf4jUtil.TOYBOX_MARKER, "Error applying template", err);
                    }
                } finally {
                    ui.accept(() -> {
                        applyButton.setEnabled(true);
                        autoCheckbox.setEnabled(true);
                        for (Binding b : bindings) {
                            b.setEnabled(true);
                        }
                    });
                }
            });
        }

        /**
         * Sets the data object providing template bindings.
         *
         * @param object the string producer to bind
         */
        public void setDataObject(@NonNull StringProducer object) {
            this.object = Objects.requireNonNull(object, "object");
            inputs.removeAll();
            updateInputs(object);
        }

        private void updateInputs(@NonNull StringProducer object) {
            bindings = new BindingResolver().getBindings(environment, object);
            LinkedHashSet<String> groups = new LinkedHashSet<>();
            bindings.forEach($ -> groups.add($.getGroup().orElse("")));
            if (!groups.isEmpty()) {
                if (groups.size() == 1) {
                    HorizontalComponentsPanel inner = new HorizontalComponentsPanel();
                    for (Binding binding : bindings) {
                        Component component = binding.getComponent();
                        binding.setSubmitListener(this::doAutoApply);
                        inner.add(component);
                    }
                    inputs.add(inner);
                } else {
                    VerticalRowsPanel inner = new VerticalRowsPanel(BorderFactory.createEmptyBorder());
                    for (String group : groups) {
                        HorizontalComponentsPanel inner2 = new HorizontalComponentsPanel();
                        for (Binding binding : bindings) {
                            String bindingGroup = binding.getGroup().orElse("");
                            if (bindingGroup.equals(group)) {
                                Component component = binding.getComponent();
                                binding.setSubmitListener(this::doAutoApply);
                                inner2.add(component);
                            }
                        }
                        inner.add(
                                group.isEmpty()
                                        ? ToyBoxPanels.titledBordered("Parameters", inner2)
                                        : ToyBoxPanels.titledBordered(group, inner2)
                        );
                    }
                    inputs.add(inner);
                }
            }
            inputs.updateUI();
            Components.setInheritedPopupRecursively(inputs);
        }
    }

    private static @NonNull String md5Hex(@NonNull String source) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(source.getBytes(StandardCharsets.UTF_8));
            BigInteger bigInteger = new BigInteger(1, bytes);
            return String.format("%0" + (bytes.length << 1) + "x", bigInteger);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Exception wrapper for binding value application failures.
     */
    private static class BindingValueApplyException extends RuntimeException {
        private BindingValueApplyException(@NonNull Binding b, @NonNull Throwable cause) {
            super("Exception applying value for " + b, cause);
        }
    }
}

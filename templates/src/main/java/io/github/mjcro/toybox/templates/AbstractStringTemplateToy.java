package io.github.mjcro.toybox.templates;

import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Environment;
import io.github.mjcro.toybox.api.Toy;
import io.github.mjcro.toybox.swing.Components;
import io.github.mjcro.toybox.swing.Styles;
import io.github.mjcro.toybox.swing.factories.ButtonsFactory;
import io.github.mjcro.toybox.swing.layouts.InlineBlockLayout;
import io.github.mjcro.toybox.swing.widgets.ExceptionDetailsJPanel;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.ArrayList;

@Slf4j
public abstract class AbstractStringTemplateToy implements Toy {
    protected abstract StringProducer getDataObject(Context context);

    @Override
    public JPanel build(Context context) {
        return new Panel(context.getEnvironment(), getDataObject(context));
    }

    public static class Panel extends JPanel {
        private final Environment environment;
        private final JPanel inputs;
        private final JPanel outputOrException = new JPanel();
        private final JTextArea output = new JTextArea();
        private final ExceptionDetailsJPanel exceptionDetailsJPanel = new ExceptionDetailsJPanel();
        private final JButton applyButton;
        private final JButton copyToClipButton;
        private final JButton saveToFileButton;
        private final JCheckBox autoCheckbox = new JCheckBox("auto");

        private java.util.List<Binding> bindings = new ArrayList<>();
        private StringProducer object = null;

        private final JTextField hash;

        public Panel(Environment environment, StringProducer dataObject) {
            super.setLayout(new BorderLayout());
            Styles.PADDING_NORMAL.apply(this);
            this.environment = environment;

            // Outputs block
            CardLayout outputCardLayout = new CardLayout();
            outputOrException.setLayout(outputCardLayout);
            output.setEditable(false);
            if (dataObject != null) {
                dataObject.getInitialString().ifPresent(output::setText);
            }
            Styles.TEXT_MONOSPACED.apply(output);
            outputOrException.add(new JScrollPane(output), "result");
            outputOrException.add(exceptionDetailsJPanel, "exception");
            super.add(outputOrException, BorderLayout.CENTER);

            // Inputs
            JPanel top = new JPanel();
            top.setLayout(new BorderLayout());
            inputs = new JPanel();
            inputs.setLayout(new InlineBlockLayout());
            top.add(inputs, BorderLayout.CENTER);

            JPanel topFooter = new JPanel();
            topFooter.setBorder(new EmptyBorder(4, 0, 4, 0));
            topFooter.setLayout(new BorderLayout());

            JPanel buttons = new JPanel();
            saveToFileButton = ButtonsFactory.create("Save to file", this::onSaveToFileClick);
            saveToFileButton.setEnabled(false);
            buttons.add(saveToFileButton);
            copyToClipButton = ButtonsFactory.create("Copy to clipboard", this::onCopyToClipboardClick);
            copyToClipButton.setEnabled(false);
            buttons.add(copyToClipButton);
            autoCheckbox.setSelected(true);
            buttons.add(autoCheckbox);
            applyButton = ButtonsFactory.create("Apply", this::onApplyButtonClick, Styles.BUTTON_PRIMARY);
            buttons.add(applyButton);
            topFooter.add(buttons, BorderLayout.LINE_END);

            hash = new JTextField();
            hash.setEditable(false);
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

        private void onCopyToClipboardClick(ActionEvent e) {
            environment.clipboardPut(output.getText());
        }

        private void onSaveToFileClick(ActionEvent e) {
            environment.chooseFileToSave(new Environment.FileCallback() {
                @Override
                public void onFileChosen(File file) throws IOException {
                    log.debug("Saving to file {}", file);
                    try {
                        Files.write(file.toPath(), output.getText().getBytes(StandardCharsets.UTF_8));
                        log.info("Data saved to file {}", file);
                    } catch (IOException err) {
                        log.error("Error saving data to file", err);
                        throw err;
                    }
                }

                @Override
                public void onNoFileChosen() {
                    log.info("File save cancelled");
                }
            }, null);
        }

        private void onApplyButtonClick(ActionEvent e) {
            doApply();
        }

        private void doAutoApply() {
            if (autoCheckbox.isSelected()) {
                doApply();
            }
        }

        private void doApply() {
            if (object == null) {
                return;
            }
            applyButton.setEnabled(false);
            autoCheckbox.setEnabled(false);
            saveToFileButton.setEnabled(false);
            copyToClipButton.setEnabled(false);
            for (Binding b : bindings) {
                b.setEnabled(false);
            }

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
                    object.produce(sb);
                    String text = sb.toString();
                    hash.setText(md5Hex(text));
                    output.setText(text);
                    saveToFileButton.setEnabled(!text.isEmpty());
                    copyToClipButton.setEnabled(!text.isEmpty());
                    applyButton.setEnabled(true);
                    ((CardLayout) outputOrException.getLayout()).show(outputOrException, "result");
                } catch (Throwable err) {
                    exceptionDetailsJPanel.setException(err);
                    ((CardLayout) outputOrException.getLayout()).show(outputOrException, "exception");
                    log.error("Error applying template", err);
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

        public void setDataObject(@NonNull StringProducer object) {
            this.object = object;
            inputs.removeAll();
            updateInputs(object);
        }

        private void updateInputs(StringProducer object) {
            bindings = new BindingResolver().getBindings(environment, object);
            for (Binding binding : bindings) {
                Component component = binding.getComponent();
                binding.setSubmitListener(this::doAutoApply);
                inputs.add(component);
            }
            inputs.updateUI();
            Components.setInheritedPopupRecursively(inputs);
        }
    }

    private static String md5Hex(String source) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(source.getBytes(StandardCharsets.UTF_8));
            BigInteger bigInteger = new BigInteger(1, bytes);
            return String.format("%0" + (bytes.length << 1) + "x", bigInteger);
        } catch (Exception e) {
            return "";
        }
    }

    private static class BindingValueApplyException extends RuntimeException {
        private BindingValueApplyException(Binding b, Throwable cause) {
            super("Exception applying value for " + b, cause);
        }
    }
}

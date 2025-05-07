package io.github.mjcro.toybox.toys.crypt;

import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Label;
import io.github.mjcro.toybox.api.Menu;
import io.github.mjcro.toybox.api.Toy;
import io.github.mjcro.toybox.swing.BytesRepresentation;
import io.github.mjcro.toybox.swing.Components;
import io.github.mjcro.toybox.swing.hint.Hints;
import io.github.mjcro.toybox.swing.prefab.ToyBoxButtons;
import io.github.mjcro.toybox.swing.prefab.ToyBoxLaF;
import io.github.mjcro.toybox.swing.prefab.ToyBoxLabels;
import io.github.mjcro.toybox.swing.prefab.ToyBoxPanels;
import io.github.mjcro.toybox.swing.prefab.ToyBoxTextComponents;
import io.github.mjcro.toybox.swing.widgets.MultiViewTextAreaOrExceptionPanel;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;
import org.bouncycastle.util.encoders.Hex;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.concurrent.Executor;

@Slf4j
public class EncryptionToy implements Toy {
    @Override
    public List<Menu> getPath() {
        return List.of(Menu.TOYBOX_BASIC_TOOLS_MENU, Menu.TOYBOX_BASIC_TOOLS_CRYPTO_SUBMENU);
    }

    @Override
    public Label getLabel() {
        return Label.ofIconAndName("fam://key", "Encrypt/Decrypt");
    }

    @Override
    public JPanel build(Context context) {
        return new Panel(context.getEnvironment());
    }

    private static class Panel extends JPanel {
        private final Executor executor;
        private final JComboBox<Algo> algorithms = new JComboBox<>(new Algo[]{
                new AesGcm(),
                new GeneralAesAlgo("AES/ECB/NoPadding"),
                new GeneralAesAlgo("AES/ECB/PKCS5Padding"),
                new GeneralAesAlgo("AES/CBC/NoPadding"),
                new GeneralAesAlgo("AES/CBC/PKCS5Padding"),
        });
        private final JComboBox<BytesRepresentation>
                representationSecret = new JComboBox<>(BytesRepresentation.values()),
                representationIV = new JComboBox<>(BytesRepresentation.values()),
                representationInput = new JComboBox<>(BytesRepresentation.values()),
                representationOutput = new JComboBox<>(BytesRepresentation.values());
        private final JComboBox<KeyTransformation> keyTransformations = new JComboBox<>(KeyTransformation.values());
        private final JTextArea
                inputTextArea = ToyBoxTextComponents.createJTextAreaMonospaced();
        private final JTextField
                inputFieldSecret = ToyBoxTextComponents.createJTextField(),
                inputFieldIV = ToyBoxTextComponents.createJTextField(),
                outputFieldIV = ToyBoxTextComponents.createJTextField(Hints.NOT_EDITABLE_TEXT);
        private final JButton
                buttonEncrypt = ToyBoxButtons.createPrimary("Encrypt", this::onEncryptClick),
                buttonDecrypt = ToyBoxButtons.createPrimary("Decrypt", this::onDecryptClick);
        private final MultiViewTextAreaOrExceptionPanel outputArea = new MultiViewTextAreaOrExceptionPanel("");

        Panel(Executor executor) {
            super(new BorderLayout());

            this.executor = executor == null ? Runnable::run : executor;


            add(buildHeader(), BorderLayout.PAGE_START);
            add(buildInputOutputs(), BorderLayout.CENTER);
        }

        @Override
        public void setEnabled(boolean enabled) {
            algorithms.setEnabled(enabled);
            representationSecret.setEnabled(enabled);
            representationIV.setEnabled(enabled);
            representationInput.setEnabled(enabled);
            representationOutput.setEnabled(enabled);
            inputTextArea.setEnabled(enabled);
            inputFieldSecret.setEnabled(enabled);
            inputFieldIV.setEnabled(enabled);
            buttonEncrypt.setEnabled(enabled);
            buttonDecrypt.setEnabled(enabled);
            keyTransformations.setEnabled(enabled);
        }

        private void setResult(byte[] iv, byte[] data) {
            // Converting to string
            try {
                outputFieldIV.setText(Hex.toHexString(iv));
                BytesRepresentation output = (BytesRepresentation) representationOutput.getSelectedItem();
                String s = output.fromBytes(data);
                SwingUtilities.invokeLater(() -> outputArea.setViewText(s));
            } catch (Throwable t) {
                setResult(t);
            }
        }

        private void setResult(Throwable t) {
            SwingUtilities.invokeLater(() -> outputArea.setViewException(t));
        }

        private JComponent buildHeader() {
            JPanel panel = new JPanel(new MigLayout());

            panel.add(ToyBoxLabels.create("Algorithm"));
            panel.add(algorithms, "span 3, w 100%, wrap");

            panel.add(ToyBoxLabels.create("Key/Secret"));
            panel.add(keyTransformations);
            panel.add(inputFieldSecret, "w 100%");
            panel.add(representationSecret, "wrap");

            panel.add(ToyBoxLabels.create("IV", "Initialization vector"));
            panel.add(inputFieldIV, "span2, w 100%");
            panel.add(representationIV, "wrap");

            JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            buttons.add(buttonEncrypt);
            buttons.add(buttonDecrypt);
            panel.add(buttons, "span 4, w 100%");

            return ToyBoxPanels.titledBordered("Settings", panel);
        }

        private JComponent buildInputOutputs() {
            JPanel inputPanel = new JPanel(new BorderLayout());
            JPanel inputRepresentationPanel = new JPanel(new MigLayout());
            inputRepresentationPanel.add(ToyBoxLabels.create("Format"));
            inputRepresentationPanel.add(representationInput);
            inputPanel.add(inputRepresentationPanel, BorderLayout.PAGE_START);
            inputPanel.add(new JScrollPane(inputTextArea));

            JPanel outputPanel = new JPanel(new BorderLayout());
            JPanel outputRepresentationPanel = new JPanel(new MigLayout());
            outputRepresentationPanel.add(ToyBoxLabels.create("Effective IV"));
            outputRepresentationPanel.add(outputFieldIV, "w 100%, wrap");
            outputRepresentationPanel.add(ToyBoxLabels.create("Format"));
            outputRepresentationPanel.add(representationOutput, "wrap");
            outputPanel.add(outputRepresentationPanel, BorderLayout.PAGE_START);
            outputPanel.add(outputArea);

            JSplitPane pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            pane.add(ToyBoxPanels.titledBordered("Input", inputPanel));
            pane.add(ToyBoxPanels.titledBordered("Output", outputPanel));
            Hints.TEXT_MONOSPACED.apply(inputTextArea);
            Hints.NOT_EDITABLE_TEXT.apply(outputArea.getTextArea());
            Hints.TEXT_MONOSPACED.apply(outputArea.getTextArea());

            pane.setResizeWeight(0.5);
            return pane;
        }

        private void onEncryptClick(ActionEvent e) {
            prepareCrypto(true);
        }

        private void onDecryptClick(ActionEvent e) {
            prepareCrypto(false);
        }

        private void prepareCrypto(boolean encrypt) {
            setEnabled(false);
            try {
                // Reading algo
                Algo algo = (Algo) algorithms.getSelectedItem();
                if (algo == null) {
                    throw new IllegalArgumentException("Algorithm not selected");
                }

                byte[] secret, iv, data;

                // Reading secret
                try {
                    secret = ((KeyTransformation) keyTransformations.getSelectedItem()).transform(
                            ((BytesRepresentation) representationSecret.getSelectedItem()).asBytes(inputFieldSecret)
                    );
                } catch (Exception e) {
                    throw new IllegalArgumentException("Unable to read secret", e);
                }
                log.info(
                        "Applying {} using {} and key size {} bit",
                        encrypt ? "encryption" : "decryption",
                        algo.toString(),
                        secret.length * 8
                );

                // Reading iv
                try {
                    if (inputFieldIV.getText().isEmpty()) {
                        iv = null;
                    } else {
                        iv = ((BytesRepresentation) representationIV.getSelectedItem()).asBytes(inputFieldIV);
                    }
                } catch (Exception e) {
                    throw new IllegalArgumentException("Unable to read IV", e);
                }

                // Reading data
                try {
                    data = ((BytesRepresentation) representationInput.getSelectedItem()).asBytes(inputTextArea);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Unable to read input data", e);
                }

                executor.execute(() -> {
                    try {
                        if (encrypt) {
                            IVData encrypted = algo.encrypt(secret, iv, data);
                            setResult(encrypted.getIv(), encrypted.getData());
                        } else {
                            byte[] plaintext = algo.decrypt(secret, new IVData(iv, data));
                            setResult(iv, plaintext);
                        }
                    } catch (Exception e) {
                        setResult(e);
                    } finally {
                        setEnabled(true);
                    }
                });
            } catch (Exception e) {
                setResult(e);
                setEnabled(true);
            }
        }
    }

    public static void main(String[] args) {
        ToyBoxLaF.initialize(false);
        Components.show(new Panel(null));
    }
}

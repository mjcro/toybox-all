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
import io.github.mjcro.toybox.swing.util.Slf4jUtil;
import io.github.mjcro.toybox.swing.widgets.MultiViewTextAreaOrExceptionPanel;
import net.miginfocom.swing.MigLayout;
import org.bouncycastle.util.encoders.Hex;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Toy providing symmetric encryption and decryption using AES algorithms
 * (GCM, ECB, CBC) with configurable key transformations and byte representations.
 */
public class EncryptionToy implements Toy {
    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull List<@NonNull Menu> getPath() {
        return List.of(Menu.TOYBOX_BASIC_TOOLS_MENU, Menu.TOYBOX_BASIC_TOOLS_CRYPTO_SUBMENU);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull Label getLabel() {
        return Label.ofIconAndName("fam://key", "Encrypt/Decrypt");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull JPanel build(@NonNull Context context) {
        return new Panel(context.getEnvironment());
    }

    /**
     * Inner panel providing the encryption/decryption UI with algorithm selection,
     * key/IV fields, byte-representation choosers and input/output areas.
     */
    private static class Panel extends JPanel {
        private static final @NonNull Logger log = LoggerFactory.getLogger(Panel.class);

        private final @NonNull Executor executor;
        private final @NonNull JComboBox<@NonNull Algo> algorithms = new JComboBox<>(new Algo[]{
                new AesGcm(),
                new GeneralAesAlgo("AES/ECB/NoPadding"),
                new GeneralAesAlgo("AES/ECB/PKCS5Padding"),
                new GeneralAesAlgo("AES/CBC/NoPadding"),
                new GeneralAesAlgo("AES/CBC/PKCS5Padding"),
        });
        private final @NonNull JComboBox<@NonNull BytesRepresentation>
                representationSecret = new JComboBox<>(BytesRepresentation.values()),
                representationIV = new JComboBox<>(BytesRepresentation.values()),
                representationInput = new JComboBox<>(BytesRepresentation.values()),
                representationOutput = new JComboBox<>(BytesRepresentation.values());
        private final @NonNull JComboBox<@NonNull KeyTransformation> keyTransformations = new JComboBox<>(KeyTransformation.values());
        private final @NonNull JTextArea
                inputTextArea = ToyBoxTextComponents.createJTextAreaMonospaced();
        private final @NonNull JTextField
                inputFieldSecret = ToyBoxTextComponents.createJTextField(),
                inputFieldIV = ToyBoxTextComponents.createJTextField(),
                outputFieldIV = ToyBoxTextComponents.createJTextField(Hints.NOT_EDITABLE_TEXT);
        private final @NonNull JButton
                buttonEncrypt = ToyBoxButtons.createPrimary("Encrypt", this::onEncryptClick),
                buttonDecrypt = ToyBoxButtons.createPrimary("Decrypt", this::onDecryptClick);
        private final @NonNull MultiViewTextAreaOrExceptionPanel outputArea = new MultiViewTextAreaOrExceptionPanel("");

        /**
         * Constructs the encryption panel.
         *
         * @param executor the executor for running crypto operations off the EDT, may be {@code null}
         */
        Panel(@Nullable Executor executor) {
            super(new BorderLayout());

            this.executor = executor == null ? Runnable::run : executor;


            add(buildHeader(), BorderLayout.PAGE_START);
            add(buildInputOutputs(), BorderLayout.CENTER);
        }

        /**
         * Enables or disables all interactive components in the panel.
         *
         * @param enabled whether to enable the components
         */
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

        /**
         * Displays the encryption/decryption result (IV and data) in the output area.
         *
         * @param iv   the initialization vector bytes
         * @param data the result data bytes
         */
        private void setResult(byte @NonNull [] iv, byte @NonNull [] data) {
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

        /**
         * Displays an exception in the output area.
         *
         * @param t the throwable to display
         */
        private void setResult(@NonNull Throwable t) {
            SwingUtilities.invokeLater(() -> outputArea.setViewException(t));
        }

        /**
         * Builds the header panel containing algorithm, key/IV, and button controls.
         *
         * @return the header component
         */
        private @NonNull JComponent buildHeader() {
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

        /**
         * Builds the split pane containing the input and output areas.
         *
         * @return the input/output component
         */
        private @NonNull JComponent buildInputOutputs() {
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

        /**
         * Handles the encrypt button click.
         *
         * @param e the action event
         */
        private void onEncryptClick(@NonNull ActionEvent e) {
            prepareCrypto(true);
        }

        /**
         * Handles the decrypt button click.
         *
         * @param e the action event
         */
        private void onDecryptClick(@NonNull ActionEvent e) {
            prepareCrypto(false);
        }

        /**
         * Prepares and executes the encryption or decryption operation.
         *
         * @param encrypt {@code true} to encrypt, {@code false} to decrypt
         */
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
                        Slf4jUtil.TOYBOX_MARKER,
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

                final byte[] ivFinal = iv != null ? iv : new byte[0];
                executor.execute(() -> {
                    try {
                        if (encrypt) {
                            IVData encrypted = algo.encrypt(secret, ivFinal, data);
                            setResult(encrypted.getIv(), encrypted.getData());
                        } else {
                            byte[] plaintext = algo.decrypt(secret, new IVData(ivFinal, data));
                            setResult(ivFinal, plaintext);
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

    /**
     * Standalone entry point for testing the encryption panel.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(@NonNull String[] args) {
        ToyBoxLaF.initialize(false);
        Components.show(new Panel(null));
    }
}

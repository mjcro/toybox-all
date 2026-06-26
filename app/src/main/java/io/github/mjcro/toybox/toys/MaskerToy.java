package io.github.mjcro.toybox.toys;

import io.github.mjcro.masker.Masker;
import io.github.mjcro.masker.auto.AutoBodyMasker;
import io.github.mjcro.masker.rules.Rulebook;
import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Label;
import io.github.mjcro.toybox.api.Menu;
import io.github.mjcro.toybox.api.Toy;
import io.github.mjcro.toybox.swing.Components;
import io.github.mjcro.toybox.swing.hint.Hints;
import io.github.mjcro.toybox.swing.prefab.ToyBoxButtons;
import io.github.mjcro.toybox.swing.prefab.ToyBoxPanels;
import io.github.mjcro.toybox.swing.prefab.ToyBoxTextComponents;
import io.github.mjcro.toybox.swing.util.Slf4jUtil;
import io.github.mjcro.toybox.swing.widgets.MultiViewTextAreaOrExceptionPanel;
import org.fife.ui.rtextarea.RTextArea;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Toy that masks sensitive data (PII, card PANs, IBANs, credentials, ...) in the
 * supplied text using the {@code io.github.mjcro.masker} library.
 *
 * <p>The input is fed to an {@link AutoBodyMasker} which auto-detects whether the
 * payload is JSON, XML or form-data and dispatches to the matching document masker.
 * The full set of masking rules (card data, identity, contacts, credentials and
 * banking) is always applied.
 */
public class MaskerToy implements Toy {
    /**
     * Masker assembled from the full rulebook, applied to every input.
     */
    private static final @NonNull Masker<String, String> MASKER = AutoBodyMasker.usingRulebook(
            Rulebook.builder()
                    .withMaskedCardData()
                    .withMaskedIdentity()
                    .withMaskedContacts()
                    .withMaskedCredentials()
                    .withMaskedIban()
                    .build()
    );

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
        return Label.ofIconAndName("fam://shield", "Masker");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull JPanel build(@NonNull Context context) {
        return new Panel();
    }

    /**
     * Inner panel providing the masking UI with an editable input area and a
     * read-only output area.
     */
    private static class Panel extends JPanel {
        private static final @NonNull Logger log = LoggerFactory.getLogger(Panel.class);

        private final @NonNull RTextArea input = ToyBoxTextComponents.createBigTextArea();
        private final @NonNull MultiViewTextAreaOrExceptionPanel output = new MultiViewTextAreaOrExceptionPanel("");
        private final @NonNull JSplitPane pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        /**
         * Constructs the masker panel.
         */
        public Panel() {
            super(new BorderLayout());

            add(buildHeader(), BorderLayout.PAGE_START);
            add(buildInputOutput(), BorderLayout.CENTER);
        }

        /**
         * Builds the header component containing the apply button.
         *
         * @return the header component
         */
        private @NonNull Component buildHeader() {
            JButton apply = ToyBoxButtons.createPrimary("Mask", this::onApply);

            JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttons.add(apply);

            JPanel header = new JPanel(new BorderLayout());
            header.add(buttons, BorderLayout.PAGE_END);

            return ToyBoxPanels.titledBordered("Settings", header);
        }

        /**
         * Builds the split pane containing the input and read-only output areas.
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
         * Masks the input text using the full rulebook and displays the result.
         *
         * @param e the triggering action event
         */
        private void onApply(@NonNull ActionEvent e) {
            Components.setEnabled(false, input);

            try {
                output.setViewText(MASKER.applyMasking(input.getText()));
            } catch (Throwable ex) {
                log.error(Slf4jUtil.TOYBOX_MARKER, "Error masking input", ex);
                output.setViewException(ex);
            } finally {
                Components.setEnabled(true, input);
            }
        }
    }
}

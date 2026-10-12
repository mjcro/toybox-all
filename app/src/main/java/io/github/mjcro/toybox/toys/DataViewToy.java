package io.github.mjcro.toybox.toys;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.BaseEncoding;
import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Label;
import io.github.mjcro.toybox.api.Menu;
import io.github.mjcro.toybox.api.Toy;
import io.github.mjcro.toybox.swing.Components;
import io.github.mjcro.toybox.swing.hint.Hints;
import io.github.mjcro.toybox.swing.prefab.SilkIcons;
import io.github.mjcro.toybox.swing.prefab.ToyBoxButtons;
import io.github.mjcro.toybox.swing.prefab.ToyBoxLaF;
import io.github.mjcro.toybox.swing.prefab.ToyBoxPanels;
import io.github.mjcro.toybox.swing.prefab.ToyBoxTextComponents;
import io.github.mjcro.toybox.swing.util.Slf4jUtil;
import io.github.mjcro.toybox.swing.widgets.ExceptionDetailsJPanel;
import io.github.mjcro.toybox.swing.widgets.JsonJTree;
import io.github.mjcro.toybox.swing.widgets.XmlJTree;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Toy that renders structured data (JSON, XML) as a tree or formatted text.
 */
public class DataViewToy implements Toy {
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
        return Label.ofIconAndName(SilkIcons.TAG, "Data view");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull JPanel build(@NonNull Context context) {
        return new Panel();
    }

    /**
     * Inner panel that provides the data-view UI with an input area,
     * mode chooser, and a split pane for the rendered result.
     */
    private static class Panel extends JPanel {
        private static final @NonNull Logger log = LoggerFactory.getLogger(Panel.class);

        private final @NonNull JTextArea input = ToyBoxTextComponents.createBigTextArea(Hints.TEXT_MONOSPACED);
        private final @NonNull JComboBox<@NonNull Mode> modeChooser = new JComboBox<>(new Mode[]{
                Mode.JSON_TREE,
                Mode.JSON_PRETTY,
                Mode.XML_TREE,
                Mode.JWT_TREE
        });
        private final @NonNull JPanel result = new JPanel(new BorderLayout());

        /**
         * Constructs the data view panel.
         */
        Panel() {
            super(new BorderLayout());

            this.add(ToyBoxPanels.verticalSplit(
                    "Input", new JScrollPane(input),
                    null, buildHeaderPanel(),
                    null, result
            ));
        }

        /**
         * Builds the header panel containing the mode selector and apply button.
         *
         * @return the header panel
         */
        private @NonNull JPanel buildHeaderPanel() {
            JPanel panel = new JPanel();
            JButton apply = ToyBoxButtons.createPrimary("Apply", e -> apply());
            panel.add(modeChooser);
            panel.add(apply);
            return panel;
        }

        /**
         * Replaces the result area content with the given component.
         *
         * @param component the component to display as the result
         */
        private void setResult(@NonNull Component component) {
            result.removeAll();
            result.add(component, BorderLayout.CENTER);
            Components.setInheritedPopupRecursively(result);
            result.revalidate();
            result.repaint();
        }

        /**
         * Replaces the result area content with a read-only text area
         * showing the given string.
         *
         * @param string the text to display
         */
        private void setResult(@NonNull String string) {
            JTextArea textArea = ToyBoxTextComponents.createBigTextArea(
                    string,
                    Hints.NOT_EDITABLE_TEXT,
                    Hints.TEXT_MONOSPACED
            );
            setResult(new JScrollPane(textArea));
        }

        /**
         * Parses the input text according to the selected mode and displays the result.
         */
        private void apply() {
            try {
                switch ((Mode) modeChooser.getSelectedItem()) {
                    case JSON_TREE: {
                        ObjectMapper mapper = new ObjectMapper();
                        Object data = mapper.readValue(input.getText(), Object.class);
                        setResult(new JScrollPane(new JsonJTree(data)));
                    }
                    break;
                    case JSON_PRETTY: {
                        ObjectMapper mapper = new ObjectMapper();
                        Object data = mapper.readValue(input.getText(), Object.class);
                        setResult(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data));
                    }
                    break;
                    case XML_TREE:
                        setResult(new JScrollPane(new XmlJTree(input.getText())));
                        break;
                    case JWT_TREE:
                        setResult(new JScrollPane(new JsonJTree(decodeJwt(input.getText()))));
                        break;
                }
            } catch (Exception e) {
                log.error(Slf4jUtil.TOYBOX_MARKER, "Error parsing data", e);
                setResult(new ExceptionDetailsJPanel(e));
            }
        }
    }

    /**
     * Decodes JWS compact serialization without verifying its signature.
     *
     * @param token token, optionally prefixed with {@code Bearer}
     * @return map with parsed {@code header} and {@code payload} and raw {@code signature}
     * @throws IllegalArgumentException if token is malformed or encrypted (JWE)
     */
    static @NonNull Map<@NonNull String, @Nullable Object> decodeJwt(@NonNull String token) {
        String compact = token.strip();
        if (compact.regionMatches(true, 0, "Bearer ", 0, 7)) {
            compact = compact.substring(7).strip();
        }
        final String[] parts = compact.split("\\.", -1);
        if (parts.length == 5) {
            throw new IllegalArgumentException("Encrypted JWT (JWE) is not supported");
        }
        if (parts.length != 3) {
            throw new IllegalArgumentException("Expected 3 dot-separated parts, got " + parts.length);
        }

        final Map<String, Object> result = new LinkedHashMap<>();
        result.put("header", decodeJwtPart(parts[0]));
        result.put("payload", decodeJwtPart(parts[1]));
        result.put("signature", parts[2]);
        return result;
    }

    /**
     * Decodes Base64URL part as JSON, falling back to plain string for non-JSON payloads.
     */
    private static @Nullable Object decodeJwtPart(@NonNull String part) {
        final String json = new String(BaseEncoding.base64Url().decode(part), StandardCharsets.UTF_8);
        try {
            return new ObjectMapper().readValue(json, Object.class);
        } catch (JsonProcessingException e) {
            return json;
        }
    }

    /**
     * Enumeration of supported data-view modes.
     */
    private enum Mode {
        /** Renders JSON as an expandable tree. */
        JSON_TREE("JSON tree"),
        /** Formats JSON with indentation. */
        JSON_PRETTY("Formatted JSON"),
        /** Renders XML as an expandable tree. */
        XML_TREE("XML tree"),
        /** Renders decoded JWT header and payload as an expandable tree. */
        JWT_TREE("JWT tree");

        private final @NonNull String displayName;

        /**
         * Constructs a mode with the given display name.
         *
         * @param displayName the human-readable name
         */
        Mode(@NonNull String displayName) {
            this.displayName = displayName;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public @NonNull String toString() {
            return displayName;
        }
    }

    /**
     * Standalone entry point for testing the data view panel.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(@NonNull String[] args) {
        ToyBoxLaF.initialize(false);

        Components.show(new Panel());
    }
}

package io.github.mjcro.toybox.toys;

import com.google.common.io.BaseEncoding;
import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Label;
import io.github.mjcro.toybox.api.Menu;
import io.github.mjcro.toybox.api.Toy;
import io.github.mjcro.toybox.api.util.Util;
import io.github.mjcro.toybox.swing.Components;
import io.github.mjcro.toybox.swing.hint.Hints;
import io.github.mjcro.toybox.swing.prefab.ToyBoxButtons;
import io.github.mjcro.toybox.swing.prefab.ToyBoxTextComponents;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Vector;
import java.util.function.Function;

/**
 * Toy for converting strings between different encodings and representations.
 */
public class StringConverterToy implements Toy {

    private static final @NonNull List<@NonNull SourceToBytes> toBytesConverters = List.of(
            new SourceToBytes("String", s -> s.getBytes(StandardCharsets.UTF_8)),
            new SourceToBytes("Hexadecimal", s -> BaseEncoding.base16().decode(s.toUpperCase(Locale.ROOT))),
            new SourceToBytes("Base 32", s -> BaseEncoding.base32().decode(s.toUpperCase(Locale.ROOT))),
            new SourceToBytes("Base 64", s -> BaseEncoding.base64().decode(s))
    );
    private static final @NonNull List<@NonNull BytesToResult> fromBytesConverters = List.of(
            new BytesToResult("String", b -> new String(b, StandardCharsets.UTF_8)),
            new BytesToResult("Lowercase", b -> new String(b, StandardCharsets.UTF_8).toLowerCase(Locale.ROOT)),
            new BytesToResult("Uppercase", b -> new String(b, StandardCharsets.UTF_8).toUpperCase(Locale.ROOT)),
            new BytesToResult("Hexadecimal", b -> BaseEncoding.base16().lowerCase().encode(b)),
            new BytesToResult("Base 32", b -> BaseEncoding.base32().encode(b)),
            new BytesToResult("Base 64", b -> BaseEncoding.base64().encode(b)),
            new BytesToResult("IP address", bytes -> {
                if (bytes == null || bytes.length != 4) {
                    throw new IllegalArgumentException("Expected 4 bytes exactly");
                }
                return Byte.toUnsignedInt(bytes[0]) + "." + Byte.toUnsignedInt(bytes[1]) + "." + Byte.toUnsignedInt(bytes[2]) + "." + Byte.toUnsignedInt(bytes[3]);
            }),
            new BytesToResult("URL Encode", bytes -> Util.isEmpty(bytes)
                    ? ""
                    : URLEncoder.encode(new String(bytes, StandardCharsets.UTF_8), StandardCharsets.UTF_8)),
            new BytesToResult("URL Decode", bytes -> Util.isEmpty(bytes)
                    ? ""
                    : URLDecoder.decode(new String(bytes, StandardCharsets.UTF_8), StandardCharsets.UTF_8)),
            new BytesToResult("Hex table", StringConverterToy::hexTable),
            new BytesToResult("Strip AI markers", bytes -> Util.isEmpty(bytes)
                    ? ""
                    : stripAiMarkers(new String(bytes, StandardCharsets.UTF_8)))
    );

    @Override
    public @NonNull List<@NonNull Menu> getPath() {
        return List.of(Menu.TOYBOX_BASIC_TOOLS_MENU);
    }

    @Override
    public @NonNull Label getLabel() {
        return Label.ofIconAndName("fam://text_replace", "String Convert");
    }

    @Override
    public @NonNull Optional<@NonNull String> getVersion() {
        return Optional.of("v0.3");
    }

    @Override
    public @NonNull JPanel build(@NonNull Context context) {
        Panel panel = new Panel();
        context.getInitialData()
                .filter($ -> $ instanceof CharSequence)
                .map(Object::toString)
                .ifPresent(panel::setSourceText);

        return panel;
    }

    private static @NonNull String hexTable(byte @Nullable [] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        int size = 16;
        int x = 0;
        for (int i = 0; i < bytes.length; i++) {
            sb.append(String.format("%02X", bytes[i]));
            if (x == size - 1 || i == bytes.length - 1) {
                for (int j = 0; j < size - x - 1; j++) {
                    sb.append("   ");
                }
                sb.append(" | ");
                for (int j = i - x; j <= i; j++) {
                    String s = new String(new byte[]{bytes[j]}, StandardCharsets.UTF_8);
                    char c = s.charAt(0);
                    if (!Character.isLetter(c) && !Character.isDigit(c)) {
                        s = ".";
                    }
                    sb.append(s);
                }
                sb.append("\n");
                x = 0;
            } else {
                sb.append(" ");
                x++;
            }
        }
        return sb.toString();
    }

    /**
     * Normalizes typographic characters frequently emitted by LLMs (em/en dashes, smart quotes,
     * ellipsis, non-breaking and Unicode spaces) and strips invisible/steganographic codepoints
     * such as zero-width characters, bidi controls, variation selectors and the Tag block
     * ({@code U+E0000..U+E007F}) which is commonly used to embed hidden fingerprints.
     *
     * @param input source string
     * @return string containing only characters humans typically type
     */
    private static @NonNull String stripAiMarkers(@NonNull String input) {
        if (input.isEmpty()) {
            return input;
        }

        final StringBuilder sb = new StringBuilder(input.length());
        final int length = input.length();
        int index = 0;
        while (index < length) {
            final int cp = input.codePointAt(index);
            index += Character.charCount(cp);

            // Tag block — steganographic fingerprints
            if (cp >= 0xE0000 && cp <= 0xE007F) {
                continue;
            }
            // Variation selectors
            if (cp >= 0xFE00 && cp <= 0xFE0F) {
                continue;
            }
            if (cp >= 0xE0100 && cp <= 0xE01EF) {
                continue;
            }

            switch (cp) {
                // Hyphen, non-breaking hyphen, figure/en/em dash, horizontal bar,
                // minus sign, small/fullwidth hyphen variants
                case 0x2010:
                case 0x2011:
                case 0x2012:
                case 0x2013:
                case 0x2014:
                case 0x2015:
                case 0x2212:
                case 0xFE58:
                case 0xFE63:
                case 0xFF0D:
                    sb.append('-');
                    break;
                // Smart single quotes / low-9 quote
                case 0x2018:
                case 0x2019:
                case 0x201A:
                case 0x201B:
                    sb.append('\'');
                    break;
                // Smart double quotes / low-9 / reversed
                case 0x201C:
                case 0x201D:
                case 0x201E:
                case 0x201F:
                    sb.append('"');
                    break;
                // Horizontal ellipsis
                case 0x2026:
                    sb.append("...");
                    break;
                // Non-breaking space, en/em/figure/punctuation spaces,
                // thin/hair/six-/four-/three-per-em, narrow/medium-math, ideographic
                case 0x00A0:
                case 0x2000:
                case 0x2001:
                case 0x2002:
                case 0x2003:
                case 0x2004:
                case 0x2005:
                case 0x2006:
                case 0x2007:
                case 0x2008:
                case 0x2009:
                case 0x200A:
                case 0x202F:
                case 0x205F:
                case 0x3000:
                    sb.append(' ');
                    break;
                // Soft hyphen, zero-width space/non-joiner/joiner, LRM/RLM,
                // line/paragraph separator, bidi embedding/override controls,
                // word joiner, invisible math operators, isolate controls, BOM
                case 0x00AD:
                case 0x200B:
                case 0x200C:
                case 0x200D:
                case 0x200E:
                case 0x200F:
                case 0x2028:
                case 0x2029:
                case 0x202A:
                case 0x202B:
                case 0x202C:
                case 0x202D:
                case 0x202E:
                case 0x2060:
                case 0x2061:
                case 0x2062:
                case 0x2063:
                case 0x2064:
                case 0x2066:
                case 0x2067:
                case 0x2068:
                case 0x2069:
                case 0xFEFF:
                    break;
                default:
                    sb.appendCodePoint(cp);
                    break;
            }
        }
        return sb.toString();
    }

    /**
     * Main panel containing source/result text areas and conversion selectors.
     */
    private static final class Panel extends JPanel {

        private final @NonNull JTextArea sourceText;
        private final @NonNull JTextArea resultText;
        private final @NonNull JComboBox<@NonNull SourceToBytes> sourceTypeSelector;
        private final @NonNull JComboBox<@NonNull BytesToResult> bytesToResultSelector;
        private final @NonNull JButton convertButton;

        Panel() {
            this.sourceText = ToyBoxTextComponents.createBigTextArea(Hints.TEXT_MONOSPACED);
            this.resultText = ToyBoxTextComponents.createBigTextArea(Hints.TEXT_MONOSPACED, Hints.NOT_EDITABLE_TEXT);
            this.convertButton = ToyBoxButtons.createPrimary("Convert To", e -> doConvert());
            this.sourceTypeSelector = new JComboBox<>(new Vector<>(toBytesConverters));
            this.bytesToResultSelector = new JComboBox<>(new Vector<>(fromBytesConverters));

            this.sourceTypeSelector.setEditable(false);
            this.bytesToResultSelector.setEditable(false);

            this.sourceTypeSelector.addActionListener(e -> doConvert());
            this.bytesToResultSelector.addActionListener(e -> doConvert());

            JSplitPane textAreasPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            textAreasPanel.setResizeWeight(.5d);
            textAreasPanel.add(new JScrollPane(sourceText));
            textAreasPanel.add(new JScrollPane(resultText));

            JPanel selectorsPanel = new JPanel();
            selectorsPanel.setLayout(new GridLayout(1, 3));
            selectorsPanel.add(Components.with(new JPanel(), p -> {
                p.setLayout(new BorderLayout());
                Hints.PADDING_NORMAL.apply(p);
                p.add(sourceTypeSelector, BorderLayout.CENTER);
            }));
            selectorsPanel.add(Components.with(new JPanel(), p -> {
                p.setLayout(new BorderLayout());
                Hints.PADDING_NORMAL.apply(p);
                p.add(convertButton, BorderLayout.CENTER);
            }));
            selectorsPanel.add(Components.with(new JPanel(), p -> {
                p.setLayout(new BorderLayout());
                Hints.PADDING_NORMAL.apply(p);
                p.add(bytesToResultSelector, BorderLayout.CENTER);
            }));

            Hints.PADDING_NORMAL.apply(this);
            this.setLayout(new BorderLayout());
            this.add(selectorsPanel, BorderLayout.PAGE_START);
            this.add(textAreasPanel, BorderLayout.CENTER);
        }

        void setSourceText(@NonNull String s) {
            sourceText.setText(s);
        }

        void doConvert() {
            SourceToBytes from = (SourceToBytes) this.sourceTypeSelector.getSelectedItem();
            BytesToResult to = (BytesToResult) this.bytesToResultSelector.getSelectedItem();

            this.sourceTypeSelector.setEnabled(false);
            this.bytesToResultSelector.setEnabled(false);
            this.convertButton.setEnabled(false);
            this.sourceText.setEnabled(false);

            try {
                byte[] bytes = from.converter.apply(this.sourceText.getText());
                this.resultText.setText(to.converter.apply(bytes));
                this.resultText.setEnabled(true);
            } catch (Throwable e) {
                this.resultText.setText(String.format("%s\n%s", e.getClass(), e.getMessage()));
                this.resultText.setEnabled(false);
            } finally {
                this.sourceTypeSelector.setEnabled(true);
                this.bytesToResultSelector.setEnabled(true);
                this.convertButton.setEnabled(true);
                this.sourceText.setEnabled(true);
            }
        }
    }

    /**
     * Converter from source string to byte array.
     */
    private static final class SourceToBytes {

        private final @NonNull String name;
        private final @NonNull Function<@NonNull String, byte @NonNull []> converter;

        SourceToBytes(@NonNull String name, @NonNull Function<@NonNull String, byte @NonNull []> converter) {
            this.name = name;
            this.converter = converter;
        }

        @NonNull String getName() {
            return name;
        }

        @NonNull Function<@NonNull String, byte @NonNull []> getConverter() {
            return converter;
        }

        @Override
        public boolean equals(@Nullable Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            SourceToBytes that = (SourceToBytes) o;
            return Objects.equals(name, that.name)
                    && Objects.equals(converter, that.converter);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, converter);
        }

        @Override
        public @NonNull String toString() {
            return name;
        }
    }

    /**
     * Converter from byte array to result string.
     */
    private static final class BytesToResult {

        private final @NonNull String name;
        private final @NonNull Function<byte @Nullable [], @NonNull String> converter;

        BytesToResult(@NonNull String name, @NonNull Function<byte @Nullable [], @NonNull String> converter) {
            this.name = name;
            this.converter = converter;
        }

        @NonNull String getName() {
            return name;
        }

        @NonNull Function<byte @Nullable [], @NonNull String> getConverter() {
            return converter;
        }

        @Override
        public boolean equals(@Nullable Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            BytesToResult that = (BytesToResult) o;
            return Objects.equals(name, that.name)
                    && Objects.equals(converter, that.converter);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, converter);
        }

        @Override
        public @NonNull String toString() {
            return name;
        }
    }

    /**
     * Main method for standalone testing.
     *
     * @param args command-line arguments
     */
    public static void main(@NonNull String @NonNull [] args) {
        var panel = new Panel();
        JPopupMenu menu = new JPopupMenu();
        menu.add(new javax.swing.JMenuItem("Hello"));

        panel.setComponentPopupMenu(menu);
        Components.show(panel);
    }
}

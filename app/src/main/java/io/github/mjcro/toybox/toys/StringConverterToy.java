package io.github.mjcro.toybox.toys;

import com.google.common.base.CharMatcher;
import com.google.common.io.BaseEncoding;
import com.google.common.net.InetAddresses;
import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Label;
import io.github.mjcro.toybox.api.Menu;
import io.github.mjcro.toybox.api.Toy;
import io.github.mjcro.toybox.api.util.Util;
import io.github.mjcro.toybox.swing.Components;
import io.github.mjcro.toybox.swing.hint.Hints;
import io.github.mjcro.toybox.swing.prefab.SilkIcons;
import io.github.mjcro.toybox.swing.prefab.ToyBoxButtons;
import io.github.mjcro.toybox.swing.prefab.ToyBoxPanels;
import io.github.mjcro.toybox.swing.prefab.ToyBoxTextComponents;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Vector;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Toy for converting strings between different encodings and representations.
 */
public class StringConverterToy implements Toy {

    private static final @NonNull List<@NonNull SourceToBytes> toBytesConverters = List.of(
            new SourceToBytes("String", s -> s.getBytes(StandardCharsets.UTF_8)),
            new SourceToBytes("Hexadecimal", StringConverterToy::decodeHex),
            new SourceToBytes("Base 32", StringConverterToy::decodeBase32),
            new SourceToBytes("Base 64", StringConverterToy::decodeBase64),
            new SourceToBytes("Base 64 URL", StringConverterToy::decodeBase64Url),
            new SourceToBytes("Escaped string", s -> unescape(s).getBytes(StandardCharsets.UTF_8))
    );
    private static final @NonNull List<@NonNull BytesToResult> fromBytesConverters = List.of(
            new BytesToResult("String", b -> new String(b, StandardCharsets.UTF_8)),
            new BytesToResult("Lowercase", b -> new String(b, StandardCharsets.UTF_8).toLowerCase(Locale.ROOT)),
            new BytesToResult("Uppercase", b -> new String(b, StandardCharsets.UTF_8).toUpperCase(Locale.ROOT)),
            new BytesToResult("Hexadecimal", b -> BaseEncoding.base16().lowerCase().encode(b)),
            new BytesToResult("Base 32", b -> BaseEncoding.base32().encode(b)),
            new BytesToResult("Base 64", b -> BaseEncoding.base64().encode(b)),
            new BytesToResult("IP address", StringConverterToy::ipAddress),
            new BytesToResult("URL Encode", bytes -> Util.isEmpty(bytes)
                    ? ""
                    : URLEncoder.encode(new String(bytes, StandardCharsets.UTF_8), StandardCharsets.UTF_8)),
            new BytesToResult("URL Decode", bytes -> Util.isEmpty(bytes)
                    ? ""
                    : URLDecoder.decode(new String(bytes, StandardCharsets.UTF_8), StandardCharsets.UTF_8)),
            new BytesToResult("Hex table", StringConverterToy::hexTable),
            new BytesToResult("Strip AI markers", bytes -> Util.isEmpty(bytes)
                    ? ""
                    : AiMarkers.strip(new String(bytes, StandardCharsets.UTF_8)))
    );

    /**
     * Separators and prefixes tolerated in hexadecimal input, e.g. {@code 0xDE, AD:BE \xEF}.
     */
    private static final @NonNull Pattern HEX_NOISE = Pattern.compile("(?i)0x|\\\\x|[\\s:,_-]");

    @Override
    public @NonNull List<@NonNull Menu> getPath() {
        return List.of(Menu.TOYBOX_BASIC_TOOLS_MENU);
    }

    @Override
    public @NonNull Label getLabel() {
        return Label.ofIconAndName(SilkIcons.TEXT_REPLACE, "String Convert");
    }

    @Override
    public @NonNull Optional<@NonNull String> getVersion() {
        return Optional.of("v0.4");
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

    /**
     * Decodes hexadecimal ignoring case, whitespace, {@code 0x}/{@code \x} prefixes and
     * {@code :,_-} separators.
     */
    static byte @NonNull [] decodeHex(@NonNull String s) {
        return BaseEncoding.base16().decode(HEX_NOISE.matcher(s).replaceAll("").toUpperCase(Locale.ROOT));
    }

    /**
     * Decodes Base 32 ignoring case, whitespace and missing padding.
     */
    static byte @NonNull [] decodeBase32(@NonNull String s) {
        return BaseEncoding.base32().decode(CharMatcher.whitespace().removeFrom(s).toUpperCase(Locale.ROOT));
    }

    /**
     * Decodes Base 64 in either standard or URL-safe alphabet, ignoring whitespace and missing padding.
     */
    static byte @NonNull [] decodeBase64(@NonNull String s) {
        return BaseEncoding.base64().decode(CharMatcher.whitespace().removeFrom(s).replace('-', '+').replace('_', '/'));
    }

    /**
     * Decodes Base 64 in URL-safe alphabet, ignoring whitespace and missing padding.
     */
    static byte @NonNull [] decodeBase64Url(@NonNull String s) {
        return BaseEncoding.base64Url().decode(CharMatcher.whitespace().removeFrom(s));
    }

    /**
     * Unescapes JSON/Java string literal, surrounding double quotes are optional.
     *
     * @param s escaped string
     * @return unescaped string
     * @throws IllegalArgumentException on unknown or truncated escape sequence
     */
    static @NonNull String unescape(@NonNull String s) {
        final String body = s.length() >= 2 && s.startsWith("\"") && s.endsWith("\"")
                ? s.substring(1, s.length() - 1)
                : s;
        final StringBuilder sb = new StringBuilder(body.length());
        int i = 0;
        while (i < body.length()) {
            final char c = body.charAt(i++);
            if (c != '\\') {
                sb.append(c);
                continue;
            }
            if (i >= body.length()) {
                throw new IllegalArgumentException("Trailing backslash");
            }
            final char e = body.charAt(i++);
            switch (e) {
                case '"':
                case '\'':
                case '\\':
                case '/':
                    sb.append(e);
                    break;
                case 'b':
                    sb.append('\b');
                    break;
                case 'f':
                    sb.append('\f');
                    break;
                case 'n':
                    sb.append('\n');
                    break;
                case 'r':
                    sb.append('\r');
                    break;
                case 't':
                    sb.append('\t');
                    break;
                case 'u':
                    if (i + 4 > body.length()) {
                        throw new IllegalArgumentException("Truncated \\u escape at " + (i - 2));
                    }
                    try {
                        sb.append((char) Integer.parseInt(body.substring(i, i + 4), 16));
                    } catch (NumberFormatException ex) {
                        throw new IllegalArgumentException("Invalid \\u escape at " + (i - 2), ex);
                    }
                    i += 4;
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported escape \\" + e + " at " + (i - 2));
            }
        }
        return sb.toString();
    }

    /**
     * Formats 4 or 16 bytes as IPv4 or compressed IPv6 address.
     *
     * @throws IllegalArgumentException on other lengths
     */
    static @NonNull String ipAddress(byte @Nullable [] bytes) {
        try {
            if (bytes != null && bytes.length == 4) {
                return InetAddresses.toAddrString(InetAddress.getByAddress(bytes));
            }
            if (bytes != null && bytes.length == 16) {
                // Inet6Address keeps IPv4-mapped addresses in IPv6 form, unlike InetAddress
                return InetAddresses.toAddrString(Inet6Address.getByAddress(null, bytes, -1));
            }
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException(e);
        }
        throw new IllegalArgumentException("Expected 4 or 16 bytes exactly");
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
            this.add(ToyBoxPanels.verticalSplit(
                    null, new JScrollPane(sourceText),
                    null, selectorsPanel,
                    null, new JScrollPane(resultText)
            ), BorderLayout.CENTER);
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

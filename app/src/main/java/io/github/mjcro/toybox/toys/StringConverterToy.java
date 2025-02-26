package io.github.mjcro.toybox.toys;

import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Label;
import io.github.mjcro.toybox.api.Menu;
import io.github.mjcro.toybox.api.Toy;
import io.github.mjcro.toybox.api.util.Util;
import io.github.mjcro.toybox.swing.Components;
import io.github.mjcro.toybox.swing.hint.Hints;
import io.github.mjcro.toybox.swing.prefab.ToyBoxButtons;
import lombok.Data;

import javax.swing.*;
import java.awt.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Vector;
import java.util.function.Function;

public class StringConverterToy implements Toy {
    private static final List<SourceToBytes> toBytesConverters = List.of(
            new SourceToBytes("String", s -> s.getBytes(StandardCharsets.UTF_8)),
            new SourceToBytes("Hexadecimal", s -> BaseEncoding.base16().decode(s.toUpperCase(Locale.ROOT))),
            new SourceToBytes("Base 32", s -> BaseEncoding.base32().decode(s.toUpperCase(Locale.ROOT))),
            new SourceToBytes("Base 64", s -> BaseEncoding.base64().decode(s))
    );
    private static final List<BytesToResult> fromBytesConverters = List.of(
            new BytesToResult("String", b -> new String(b, StandardCharsets.UTF_8)),
            new BytesToResult("Hexadecimal", b -> BaseEncoding.base16().lowerCase().encode(b)),
            new BytesToResult("Base 32", b -> BaseEncoding.base32().encode(b)),
            new BytesToResult("Base 64", b -> BaseEncoding.base64().encode(b)),
            new BytesToResult("MD5", b -> Hashing.md5().hashBytes(b).toString()),
            new BytesToResult("SHA256", b -> Hashing.sha256().hashBytes(b).toString()),
            new BytesToResult("SHA384", b -> Hashing.sha384().hashBytes(b).toString()),
            new BytesToResult("SHA512", b -> Hashing.sha512().hashBytes(b).toString()),
            new BytesToResult("CRC32", b -> Hashing.crc32().hashBytes(b).toString()),
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
            new BytesToResult("Hex table", StringConverterToy::hexTable)
    );

    @Override
    public List<Menu> getPath() {
        return List.of(Menu.TOYBOX_BASIC_TOOLS_MENU);
    }

    @Override
    public Label getLabel() {
        return Label.ofIconAndName("fam://text_replace", "String Convert");
    }

    @Override
    public Optional<String> getVersion() {
        return Optional.of("v0.2");
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

    private static String hexTable(byte[] bytes) {
        if (Util.isEmpty(bytes)) {
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

    private static final class Panel extends JPanel {
        private final JTextArea sourceText, resultText;
        private final JComboBox<SourceToBytes> sourceTypeSelector;
        private final JComboBox<BytesToResult> bytesToResultSelector;
        private final JButton convertButton;

        public Panel() {
            this.sourceText = new JTextArea();
            this.resultText = new JTextArea();
            this.convertButton = ToyBoxButtons.createPrimary("Convert To", e -> doConvert());
            this.sourceTypeSelector = new JComboBox<>(new Vector<>(toBytesConverters));
            this.bytesToResultSelector = new JComboBox<>(new Vector<>(fromBytesConverters));

            this.resultText.setEditable(false);
            this.sourceTypeSelector.setEditable(false);
            this.bytesToResultSelector.setEditable(false);

            Hints.TEXT_MONOSPACED.apply(this.sourceText);
            Hints.TEXT_MONOSPACED.apply(this.resultText);

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

        public void setSourceText(String s) {
            sourceText.setText(s);
        }

        public void doConvert() {
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

    @Data
    private static final class SourceToBytes {
        private final String name;
        private final Function<String, byte[]> converter;

        @Override
        public String toString() {
            return name;
        }
    }

    @Data
    private static final class BytesToResult {
        private final String name;
        private final Function<byte[], String> converter;

        @Override
        public String toString() {
            return name;
        }
    }

    public static void main(String[] args) {
        var panel = new Panel();
        JPopupMenu menu = new JPopupMenu();
        menu.add(new JMenuItem("Hello"));

        panel.setComponentPopupMenu(menu);
        Components.show(panel);
    }
}

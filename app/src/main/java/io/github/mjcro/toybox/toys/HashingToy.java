package io.github.mjcro.toybox.toys;

import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Label;
import io.github.mjcro.toybox.api.Menu;
import io.github.mjcro.toybox.api.Toy;
import io.github.mjcro.toybox.swing.hint.Hints;
import io.github.mjcro.toybox.swing.prefab.ToyBoxButtons;
import io.github.mjcro.toybox.swing.prefab.ToyBoxLabels;
import io.github.mjcro.toybox.swing.prefab.ToyBoxPanels;
import io.github.mjcro.toybox.swing.prefab.ToyBoxTextComponents;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.crypto.digests.RIPEMD128Digest;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.digests.RIPEMD256Digest;
import org.bouncycastle.crypto.digests.RIPEMD320Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.digests.SHA224Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA384Digest;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.generators.OpenBSDBCrypt;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.encoders.Hex;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.AbstractMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Vector;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

@Slf4j
public class HashingToy implements Toy {
    @Override
    public List<Menu> getPath() {
        return List.of(Menu.TOYBOX_BASIC_TOOLS_MENU, Menu.TOYBOX_BASIC_TOOLS_CRYPTO_SUBMENU);
    }

    @Override
    public Label getLabel() {
        return Label.ofIconAndName("fam://asterisk_orange", "Hashing");
    }

    @Override
    public JPanel build(final Context context) {
        return new Panel(context.getEnvironment());
    }

    private static class Panel extends JPanel {
        private final List<Hash> hashes = List.of(
                new CRC32Hash(),
                new BCDigestHash(MD5Digest::new),
                TransformOutput.uppercase(new BCDigestHash(MD5Digest::new)),
                new BCDigestHash(SHA1Digest::new),
                TransformOutput.uppercase(new BCDigestHash(SHA1Digest::new)),
                new BCDigestHash(SHA224Digest::new),
                TransformOutput.uppercase(new BCDigestHash(SHA224Digest::new)),
                new BCDigestHash(SHA256Digest::new),
                TransformOutput.uppercase(new BCDigestHash(SHA256Digest::new)),
                new BCDigestHash(SHA384Digest::new),
                TransformOutput.uppercase(new BCDigestHash(SHA384Digest::new)),
                new BCDigestHash(SHA512Digest::new),
                TransformOutput.uppercase(new BCDigestHash(SHA512Digest::new)),
                new BCDigestHash(SHA3Digest::new),
                TransformOutput.uppercase(new BCDigestHash(SHA3Digest::new)),
                new BCDigestHash(RIPEMD128Digest::new),
                TransformOutput.uppercase(new BCDigestHash(RIPEMD128Digest::new)),
                new BCDigestHash(RIPEMD160Digest::new),
                TransformOutput.uppercase(new BCDigestHash(RIPEMD160Digest::new)),
                new BCDigestHash(RIPEMD256Digest::new),
                TransformOutput.uppercase(new BCDigestHash(RIPEMD256Digest::new)),
                new BCDigestHash(RIPEMD320Digest::new),
                TransformOutput.uppercase(new BCDigestHash(RIPEMD320Digest::new)),
                new BCHMac(MD5Digest::new),
                TransformOutput.uppercase(new BCHMac(MD5Digest::new)),
                new BCHMac(SHA1Digest::new),
                TransformOutput.uppercase(new BCHMac(SHA1Digest::new)),
                new BCHMac(SHA256Digest::new),
                TransformOutput.uppercase(new BCHMac(SHA256Digest::new)),
                new BCryptHash(),
                new JavaHash()
        );

        private final Executor executor;

        private final JTextArea input = ToyBoxTextComponents.createJTextArea();
        private final JTextField password = ToyBoxTextComponents.createJTextField();
        private final JTextField complexity = ToyBoxTextComponents.createJTextField();
        private final JTextField output = ToyBoxTextComponents.createJTextField(Hints.NOT_EDITABLE_TEXT);
        private final JCheckBox trimInput = new JCheckBox("Trim input");
        private final JButton hashButton = ToyBoxButtons.createPrimary("Hash", this::onHash);
        private final JComboBox<Hash> hashSelector = new JComboBox<>(new Vector<>(hashes));


        public Panel(Executor executor) {
            super(new BorderLayout());
            this.executor = executor;

            add(buildHeaderPanel(), BorderLayout.PAGE_START);
            add(buildInputPanel());
        }

        private JPanel buildHeaderPanel() {
            JPanel additional = new JPanel(new FlowLayout(FlowLayout.LEFT));
            additional.add(trimInput);

            JPanel settingsPanel = ToyBoxPanels.twoColumnsRight(
                    new AbstractMap.SimpleEntry<>(
                            ToyBoxLabels.create("Algorithm"),
                            hashSelector
                    ),
                    new AbstractMap.SimpleEntry<>(
                            ToyBoxLabels.create("Password"),
                            password
                    ),
                    new AbstractMap.SimpleEntry<>(
                            ToyBoxLabels.create("Complexity"),
                            complexity
                    ),
                    new AbstractMap.SimpleEntry<>(
                            ToyBoxLabels.create("Additional"),
                            additional
                    )
            );

            complexity.setEnabled(false);
            password.setEnabled(false);
            hashSelector.setEditable(false);

            // Realtime changes
            hashSelector.addActionListener(e -> doTryRealtime());
            trimInput.addActionListener(e -> doTryRealtime());

            return ToyBoxPanels.verticalRows(
                    ToyBoxPanels.titledBordered("Settings", settingsPanel),
                    hashButton,
                    ToyBoxPanels.titledBordered("Hash result", output)
            );
        }

        private JPanel buildInputPanel() {
            // Realtime changes
            input.getDocument().addUndoableEditListener(e -> doTryRealtime());

            return ToyBoxPanels.titledBordered(
                    "Input string",
                    new JScrollPane(input)
            );
        }

        @Override
        public void setEnabled(boolean enabled) {
            Hash h = (Hash) hashSelector.getSelectedItem();

            hashButton.setEnabled(enabled);
            hashSelector.setEnabled(enabled);
            input.setEnabled(enabled);
            trimInput.setEnabled(enabled);
            complexity.setEnabled(enabled && h != null && h.isComplexitySupported());
            password.setEnabled(enabled && h != null && h.isPasswordSupported());
        }

        private void doTryRealtime() {
            setEnabled(true);
            Hash h = (Hash) hashSelector.getSelectedItem();
            if (h.isFast()) {
                onHash(null);
            } else {
                output.setText(null);
            }
        }

        private void onHash(ActionEvent e) {
            Hash h = (Hash) hashSelector.getSelectedItem();
            boolean trim = trimInput.isSelected();
            if (h.isFast()) {
                doHash(h, trim);
            } else {
                setEnabled(false);
                executor.execute(() -> doHash(h, trim));
            }
        }

        private void doHash(Hash h, boolean trim) {
            Instant before = Instant.now();
            try {
                String in = input.getText();
                String cmp = complexity.getText();
                String pw = password.getText();
                if (trim) {
                    in = in.strip();
                }
                String out = h.apply(in, cmp, pw);
                SwingUtilities.invokeLater(() -> output.setText(out));
                log.info("Hashing using {} completed in {}", h, Duration.between(before, Instant.now()));
            } catch (Throwable e1) {
                log.error("Error applying hash", e1);
            } finally {
                SwingUtilities.invokeLater(() -> setEnabled(true));
            }
        }
    }

    private interface Hash {
        String apply(String input, String complexity, String password) throws Exception;

        /**
         * @return True if hashing so fast that it could be done in UI thread.
         */
        boolean isFast();

        /**
         * @return True if complexity parameter is supported.
         */
        default boolean isComplexitySupported() {
            return false;
        }

        /**
         * @return True if password parameter is supported.
         */
        default boolean isPasswordSupported() {
            return false;
        }
    }

    private static class JavaHash implements Hash {
        @Override
        public String apply(String input, String complexity, String password) {
            return String.valueOf(input.hashCode());
        }

        @Override
        public boolean isFast() {
            return true;
        }

        @Override
        public String toString() {
            return "Java String hash";
        }
    }

    private static class CRC32Hash implements Hash {
        @Override
        public boolean isFast() {
            return true;
        }

        @Override
        public String apply(String input, String complexity, String password) {
            Checksum checksum = new CRC32();
            byte[] source = input.getBytes(StandardCharsets.UTF_8);
            checksum.update(source, 0, source.length);
            return String.valueOf(checksum.getValue());
        }

        @Override
        public String toString() {
            return "CRC32";
        }
    }

    private static class TransformOutput implements Hash {
        private final Hash real;
        private final String suffix;
        private final UnaryOperator<String> transformation;

        public static TransformOutput uppercase(Hash h) {
            return new TransformOutput(h, "uppercase", $ -> $.toUpperCase(Locale.ROOT));
        }

        private TransformOutput(Hash real, String suffix, UnaryOperator<String> transformation) {
            this.real = real;
            this.suffix = suffix;
            this.transformation = transformation;
        }

        @Override
        public String apply(String input, String complexity, String password) throws Exception {
            return transformation.apply(real.apply(input, complexity, password));
        }

        @Override
        public boolean isFast() {
            return real.isFast();
        }

        @Override
        public String toString() {
            return real.toString() + " " + suffix;
        }

        @Override
        public boolean isComplexitySupported() {
            return real.isComplexitySupported();
        }

        @Override
        public boolean isPasswordSupported() {
            return real.isPasswordSupported();
        }
    }

    private static class BCDigestHash implements Hash {
        final Supplier<Digest> supplier;
        private final String name;

        private BCDigestHash(Supplier<Digest> supplier) {
            this.supplier = supplier;
            this.name = supplier.get().getAlgorithmName();
        }

        @Override
        public boolean isFast() {
            return true;
        }

        @Override
        public String apply(String input, String complexity, String password) {
            Digest d = supplier.get();
            byte[] b = input.getBytes(StandardCharsets.UTF_8);
            d.update(b, 0, b.length);
            byte[] hash = new byte[d.getDigestSize()];
            d.doFinal(hash, 0);
            return Hex.toHexString(hash);
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private static class BCHMac extends BCDigestHash {
        private BCHMac(Supplier<Digest> supplier) {
            super(supplier);
        }

        @Override
        public String apply(String input, String complexity, String password) {
            Digest digest = super.supplier.get();

            HMac hMac = new HMac(digest);
            hMac.init(new KeyParameter(password.getBytes()));

            byte[] hmacIn = input.getBytes();
            hMac.update(hmacIn, 0, hmacIn.length);
            byte[] hmacOut = new byte[hMac.getMacSize()];

            hMac.doFinal(hmacOut, 0);
            return Hex.toHexString(hmacOut);
        }

        @Override
        public boolean isFast() {
            return false;
        }

        @Override
        public boolean isPasswordSupported() {
            return true;
        }

        @Override
        public String toString() {
            return "HMAC " + super.toString();
        }
    }

    private static class BCryptHash implements Hash {
        @Override
        public String apply(String input, String complexity, String password) {
            SecureRandom r = new SecureRandom();
            byte[] salt = new byte[16];
            r.nextBytes(salt);

            if (complexity.isBlank()) {
                complexity = "10";
            }

            int cost = Optional.ofNullable(complexity)
                    .filter($ -> !$.isBlank())
                    .map(String::strip)
                    .map(Integer::parseInt)
                    .orElse(10);

            return OpenBSDBCrypt.generate(input.getBytes(StandardCharsets.UTF_8), salt, cost);
        }

        @Override
        public boolean isFast() {
            return false;
        }

        @Override
        public boolean isComplexitySupported() {
            return true;
        }

        @Override
        public String toString() {
            return "BCrypt (OpenBSD)";
        }
    }
}

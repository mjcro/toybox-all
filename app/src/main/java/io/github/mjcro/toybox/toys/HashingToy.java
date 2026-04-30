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
import io.github.mjcro.toybox.swing.util.Slf4jUtil;
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
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
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

/**
 * Toy providing various hashing algorithms including CRC32, MD5, SHA family,
 * RIPEMD family, HMAC variants, BCrypt and Java's built-in hash.
 */
public class HashingToy implements Toy {
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
        return Label.ofIconAndName("fam://asterisk_orange", "Hashing");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull JPanel build(@NonNull Context context) {
        return new Panel(context.getEnvironment());
    }

    /**
     * Inner panel providing the hashing UI with algorithm selection,
     * input area, password/complexity fields, and hash output.
     */
    private static class Panel extends JPanel {
        private static final @NonNull Logger log = LoggerFactory.getLogger(Panel.class);

        private final @NonNull List<@NonNull Hash> hashes = List.of(
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

        private final @NonNull Executor executor;

        private final @NonNull JTextArea input = ToyBoxTextComponents.createTextArea();
        private final @NonNull JTextField password = ToyBoxTextComponents.createTextField();
        private final @NonNull JTextField complexity = ToyBoxTextComponents.createTextField();
        private final @NonNull JTextField output = ToyBoxTextComponents.createTextField(Hints.NOT_EDITABLE_TEXT);
        private final @NonNull JCheckBox trimInput = new JCheckBox("Trim input");
        private final @NonNull JButton hashButton = ToyBoxButtons.createPrimary("Hash", this::onHash);
        private final @NonNull JComboBox<@NonNull Hash> hashSelector = new JComboBox<>(new Vector<>(hashes));

        /**
         * Constructs the hashing panel.
         *
         * @param executor the executor for running slow hash operations off the EDT
         */
        public Panel(@NonNull Executor executor) {
            super(new BorderLayout());
            this.executor = executor;

            add(buildHeaderPanel(), BorderLayout.PAGE_START);
            add(buildInputPanel());
        }

        /**
         * Builds the header containing algorithm selector, password/complexity fields,
         * the hash button and the output field.
         *
         * @return the header panel
         */
        private @NonNull JPanel buildHeaderPanel() {
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

        /**
         * Builds the input panel containing the text area for data to hash.
         *
         * @return the input panel
         */
        private @NonNull JPanel buildInputPanel() {
            // Realtime changes
            input.getDocument().addUndoableEditListener(e -> doTryRealtime());

            return ToyBoxPanels.titledBordered(
                    "Input string",
                    new JScrollPane(input)
            );
        }

        /**
         * Enables or disables the panel's interactive components.
         *
         * @param enabled whether to enable the components
         */
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

        /**
         * Attempts real-time hashing if the selected algorithm is fast,
         * otherwise clears the output.
         */
        private void doTryRealtime() {
            setEnabled(true);
            Hash h = (Hash) hashSelector.getSelectedItem();
            if (h.isFast()) {
                onHash(null);
            } else {
                output.setText(null);
            }
        }

        /**
         * Handles the hash button click or realtime trigger.
         *
         * @param e the action event, may be {@code null} for realtime triggers
         */
        private void onHash(@Nullable ActionEvent e) {
            Hash h = (Hash) hashSelector.getSelectedItem();
            boolean trim = trimInput.isSelected();
            if (h.isFast()) {
                doHash(h, trim);
            } else {
                setEnabled(false);
                executor.execute(() -> doHash(h, trim));
            }
        }

        /**
         * Performs the actual hashing operation.
         *
         * @param h    the hash algorithm to apply
         * @param trim whether to trim the input before hashing
         */
        private void doHash(@NonNull Hash h, boolean trim) {
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
                log.info(Slf4jUtil.TOYBOX_MARKER, "Hashing using {} completed in {}", h, Duration.between(before, Instant.now()));
            } catch (Throwable e1) {
                log.error(Slf4jUtil.TOYBOX_MARKER, "Error applying hash", e1);
            } finally {
                SwingUtilities.invokeLater(() -> setEnabled(true));
            }
        }
    }

    /**
     * Contract for hash algorithm implementations used by the hashing panel.
     */
    private interface Hash {
        /**
         * Applies the hash to the given input.
         *
         * @param input      the text to hash
         * @param complexity the complexity parameter (algorithm-specific)
         * @param password   the password/key parameter (algorithm-specific)
         * @return the hash result as a string
         * @throws Exception if hashing fails
         */
        @NonNull String apply(@NonNull String input, @NonNull String complexity, @NonNull String password) throws Exception;

        /**
         * @return {@code true} if hashing is fast enough to run on the EDT
         */
        boolean isFast();

        /**
         * @return {@code true} if the complexity parameter is supported
         */
        default boolean isComplexitySupported() {
            return false;
        }

        /**
         * @return {@code true} if the password parameter is supported
         */
        default boolean isPasswordSupported() {
            return false;
        }
    }

    /**
     * Hash implementation using Java's built-in {@link String#hashCode()}.
     */
    private static class JavaHash implements Hash {
        /**
         * {@inheritDoc}
         */
        @Override
        public @NonNull String apply(@NonNull String input, @NonNull String complexity, @NonNull String password) {
            return String.valueOf(input.hashCode());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isFast() {
            return true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public @NonNull String toString() {
            return "Java String hash";
        }
    }

    /**
     * Hash implementation using Java's {@link CRC32} checksum.
     */
    private static class CRC32Hash implements Hash {
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isFast() {
            return true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public @NonNull String apply(@NonNull String input, @NonNull String complexity, @NonNull String password) {
            Checksum checksum = new CRC32();
            byte[] source = input.getBytes(StandardCharsets.UTF_8);
            checksum.update(source, 0, source.length);
            return String.valueOf(checksum.getValue());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public @NonNull String toString() {
            return "CRC32";
        }
    }

    /**
     * Decorator that transforms the output of another {@link Hash} (e.g. to uppercase).
     */
    private static class TransformOutput implements Hash {
        private final @NonNull Hash real;
        private final @NonNull String suffix;
        private final @NonNull UnaryOperator<@NonNull String> transformation;

        /**
         * Creates a transform that converts the delegate's output to uppercase.
         *
         * @param h the delegate hash
         * @return a new transform-output hash
         */
        public static @NonNull TransformOutput uppercase(@NonNull Hash h) {
            return new TransformOutput(h, "uppercase", $ -> $.toUpperCase(Locale.ROOT));
        }

        /**
         * Constructs a transform-output wrapper.
         *
         * @param real           the delegate hash
         * @param suffix         display-name suffix
         * @param transformation the string transformation to apply
         */
        private TransformOutput(@NonNull Hash real, @NonNull String suffix, @NonNull UnaryOperator<@NonNull String> transformation) {
            this.real = real;
            this.suffix = suffix;
            this.transformation = transformation;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public @NonNull String apply(@NonNull String input, @NonNull String complexity, @NonNull String password) throws Exception {
            return transformation.apply(real.apply(input, complexity, password));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isFast() {
            return real.isFast();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public @NonNull String toString() {
            return real.toString() + " " + suffix;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isComplexitySupported() {
            return real.isComplexitySupported();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isPasswordSupported() {
            return real.isPasswordSupported();
        }
    }

    /**
     * Hash implementation backed by a BouncyCastle {@link Digest}.
     */
    private static class BCDigestHash implements Hash {
        final @NonNull Supplier<@NonNull Digest> supplier;
        private final @NonNull String name;

        /**
         * Constructs a digest hash from the given supplier.
         *
         * @param supplier supplier that creates fresh digest instances
         */
        private BCDigestHash(@NonNull Supplier<@NonNull Digest> supplier) {
            this.supplier = supplier;
            this.name = supplier.get().getAlgorithmName();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isFast() {
            return true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public @NonNull String apply(@NonNull String input, @NonNull String complexity, @NonNull String password) {
            Digest d = supplier.get();
            byte[] b = input.getBytes(StandardCharsets.UTF_8);
            d.update(b, 0, b.length);
            byte[] hash = new byte[d.getDigestSize()];
            d.doFinal(hash, 0);
            return Hex.toHexString(hash);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public @NonNull String toString() {
            return name;
        }
    }

    /**
     * HMAC implementation backed by a BouncyCastle {@link Digest}.
     */
    private static class BCHMac extends BCDigestHash {
        /**
         * Constructs an HMAC hash from the given digest supplier.
         *
         * @param supplier supplier that creates fresh digest instances
         */
        private BCHMac(@NonNull Supplier<@NonNull Digest> supplier) {
            super(supplier);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public @NonNull String apply(@NonNull String input, @NonNull String complexity, @NonNull String password) {
            Digest digest = super.supplier.get();

            HMac hMac = new HMac(digest);
            hMac.init(new KeyParameter(password.getBytes()));

            byte[] hmacIn = input.getBytes();
            hMac.update(hmacIn, 0, hmacIn.length);
            byte[] hmacOut = new byte[hMac.getMacSize()];

            hMac.doFinal(hmacOut, 0);
            return Hex.toHexString(hmacOut);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isFast() {
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isPasswordSupported() {
            return true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public @NonNull String toString() {
            return "HMAC " + super.toString();
        }
    }

    /**
     * BCrypt (OpenBSD) hash implementation.
     */
    private static class BCryptHash implements Hash {
        /**
         * {@inheritDoc}
         */
        @Override
        public @NonNull String apply(@NonNull String input, @NonNull String complexity, @NonNull String password) {
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

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isFast() {
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isComplexitySupported() {
            return true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public @NonNull String toString() {
            return "BCrypt (OpenBSD)";
        }
    }
}

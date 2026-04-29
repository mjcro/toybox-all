package io.github.mjcro.toybox.swing.prefab;

import io.github.mjcro.toybox.swing.util.Slf4jUtil;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Registers ToyBox-bundled TrueType fonts with the local
 * {@link GraphicsEnvironment} so they can be referenced by family name
 * from FlatLaf theme properties and from application code.
 *
 * <p>Bundled families:
 * <ul>
 *     <li>{@link #FAMILY_REGULAR} - {@code Noto Sans} (Regular, Bold, Italic, BoldItalic)</li>
 *     <li>{@link #FAMILY_MONOSPACED} - {@code Noto Sans Mono} (Regular, Bold)</li>
 *     <li>{@link #FAMILY_EMOJI} - {@code Noto Emoji} (monochrome variable font)</li>
 * </ul>
 *
 * <p>{@link #register()} is idempotent and must be invoked before any
 * Look-and-Feel installs its defaults, otherwise FlatLaf's
 * {@code @fontFamily} resolution will not find the embedded families.
 *
 * <p><b>Emoji caveat.</b> Java Swing's text pipeline does not support
 * color emoji tables (COLR/CPAL, CBDT, SVG-in-OT). {@code Noto Emoji}
 * is monochrome and renders correctly when explicitly requested via
 * {@code new Font(EmbeddedFonts.FAMILY_EMOJI, ...)}, but Swing does
 * not auto-fall-back from a physical primary font to the emoji family
 * for missing glyphs. A future enhancement may introduce a per-run
 * renderer that switches to {@link #FAMILY_EMOJI} for codepoints that
 * the primary font cannot display.
 */
public final class EmbeddedFonts {
    private static final @NonNull Logger log = LoggerFactory.getLogger(EmbeddedFonts.class);

    /** Regular sans-serif family name (broad Latin/Cyrillic/Greek/IPA/symbol coverage). */
    public static final @NonNull String FAMILY_REGULAR = "Noto Sans";

    /** Monospaced family name (matches {@link #FAMILY_REGULAR} metrics). */
    public static final @NonNull String FAMILY_MONOSPACED = "Noto Sans Mono";

    /** Monochrome emoji family name. Must be requested explicitly; Swing does not auto-fall-back to it. */
    public static final @NonNull String FAMILY_EMOJI = "Noto Emoji";

    private static final @NonNull String RESOURCE_FOLDER = "/fonts/";

    private static final @NonNull List<@NonNull String> RESOURCES = List.of(
            "NotoSans-Regular.ttf",
            "NotoSans-Bold.ttf",
            "NotoSans-Italic.ttf",
            "NotoSans-BoldItalic.ttf",
            "NotoSansMono-Regular.ttf",
            "NotoSansMono-Bold.ttf",
            "NotoEmoji-Regular.ttf"
    );

    private static volatile boolean registered = false;

    /**
     * Registers all bundled fonts with the local {@link GraphicsEnvironment}.
     * Safe to call multiple times; subsequent invocations are no-ops.
     *
     * @throws IllegalStateException if a bundled font resource is missing or unreadable
     */
    public static void register() {
        if (registered) {
            return;
        }
        synchronized (EmbeddedFonts.class) {
            if (registered) {
                return;
            }
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            for (String resource : RESOURCES) {
                registerSingle(ge, RESOURCE_FOLDER + resource);
            }
            log.info(Slf4jUtil.TOYBOX_MARKER, "Embedded fonts registered: {}, {}, {}",
                    FAMILY_REGULAR, FAMILY_MONOSPACED, FAMILY_EMOJI);
            registered = true;
        }
    }

    private static void registerSingle(@NonNull GraphicsEnvironment ge, @NonNull String resourcePath) {
        try (InputStream in = EmbeddedFonts.class.getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new IllegalStateException("Bundled font resource not found on classpath: " + resourcePath);
            }
            Font font = Font.createFont(Font.TRUETYPE_FONT, in);
            ge.registerFont(font);
        } catch (IOException | FontFormatException e) {
            throw new IllegalStateException("Failed to load bundled font: " + resourcePath, e);
        }
    }

    private EmbeddedFonts() {
    }
}

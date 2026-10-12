package io.github.mjcro.toybox.eve;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.swing.plaf.FontUIResource;

/**
 * Bundled fonts of the Eve Look and Feel.
 *
 * <p>Bundled families (SIL Open Font License, Latin + Cyrillic + Greek coverage):
 * <ul>
 *     <li>{@link #FAMILY_SANS} - {@code Play} (Regular, Bold), a squarish techno sans used for all UI text.</li>
 *     <li>{@link #FAMILY_MONO} - {@code JetBrains Mono} (Regular, Bold), used for code and numbers.</li>
 * </ul>
 *
 * <p>{@link #register()} must run before fonts are requested by family name; all
 * factory methods of this class call it implicitly. After registration
 * {@code new Font("Play", Font.BOLD, 12)} and {@code font.deriveFont(Font.BOLD)}
 * resolve to the real bold face instead of a synthetic one.
 */
public final class EveFonts {
    /** Family name of the main UI font. */
    public static final String FAMILY_SANS = "Play";
    /** Family name of the monospaced font. */
    public static final String FAMILY_MONO = "JetBrains Mono";
    /** Base UI font size in points. */
    public static final int SIZE_BASE = 12;
    /** Small UI font size in points: status bars, captions, table headers. */
    public static final int SIZE_SMALL = 11;
    /** Letter spacing applied to uppercase headers, as {@link TextAttribute#TRACKING} value. */
    public static final float HEADER_TRACKING = 0.08f;

    private static final String RESOURCE_FOLDER = "/io/github/mjcro/toybox/eve/fonts/";
    private static final List<String> RESOURCES = List.of(
            "Play-Regular.ttf",
            "Play-Bold.ttf",
            "JetBrainsMono-Regular.ttf",
            "JetBrainsMono-Bold.ttf"
    );
    private static volatile boolean registered = false;

    private EveFonts() {
    }

    /**
     * Registers all bundled fonts with the local {@link GraphicsEnvironment}.
     * Thread-safe and idempotent; subsequent invocations are no-ops.
     *
     * @throws IllegalStateException if a bundled font resource is missing or unreadable.
     */
    public static void register() {
        if (registered) {
            return;
        }
        synchronized (EveFonts.class) {
            if (registered) {
                return;
            }
            final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            for (String name : RESOURCES) {
                ge.registerFont(load(name));
            }
            registered = true;
        }
    }

    /**
     * Returns the regular UI font.
     *
     * @return non-null {@code Play} plain font of {@link #SIZE_BASE} size.
     */
    public static FontUIResource regular() {
        return sans(Font.PLAIN, SIZE_BASE);
    }

    /**
     * Returns the bold UI font.
     *
     * @return non-null {@code Play} bold font of {@link #SIZE_BASE} size.
     */
    public static FontUIResource bold() {
        return sans(Font.BOLD, SIZE_BASE);
    }

    /**
     * Returns the small UI font.
     *
     * @return non-null {@code Play} plain font of {@link #SIZE_SMALL} size.
     */
    public static FontUIResource small() {
        return sans(Font.PLAIN, SIZE_SMALL);
    }

    /**
     * Returns the header font: small, bold and letter-spaced.
     * Intended for uppercase text in tab titles, table headers, titled borders and title bars.
     *
     * @return non-null {@code Play} bold font of {@link #SIZE_SMALL} size with {@link #HEADER_TRACKING}.
     */
    public static FontUIResource header() {
        return new FontUIResource(tracked(sans(Font.BOLD, SIZE_SMALL), HEADER_TRACKING));
    }

    /**
     * Returns the monospaced font.
     *
     * @return non-null {@code JetBrains Mono} plain font of {@link #SIZE_BASE} size.
     */
    public static FontUIResource mono() {
        register();
        return new FontUIResource(FAMILY_MONO, Font.PLAIN, SIZE_BASE);
    }

    /**
     * Returns a {@code Play} font of the given style and size.
     *
     * @param style font style, one of {@link Font#PLAIN}, {@link Font#BOLD}, {@link Font#ITALIC} or a combination.
     * @param size  font size in points.
     * @return non-null font.
     */
    public static FontUIResource sans(int style, int size) {
        register();
        return new FontUIResource(FAMILY_SANS, style, size);
    }

    /**
     * Returns a copy of the font with letter spacing applied.
     *
     * @param font     source font, not null.
     * @param tracking tracking value, see {@link TextAttribute#TRACKING}; {@code 0} means none.
     * @return non-null derived font.
     */
    public static Font tracked(Font font, float tracking) {
        return font.deriveFont(Map.of(TextAttribute.TRACKING, tracking));
    }

    private static Font load(String name) {
        try (InputStream is = EveFonts.class.getResourceAsStream(RESOURCE_FOLDER + name)) {
            if (is == null) {
                throw new IllegalStateException("Bundled font resource missing: " + name);
            }
            return Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (IOException | FontFormatException e) {
            throw new IllegalStateException("Unable to load bundled font " + name, e);
        }
    }
}

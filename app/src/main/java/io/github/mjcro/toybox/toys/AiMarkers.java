package io.github.mjcro.toybox.toys;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Detects and strips fragments typical for LLM output: typographic punctuation, exotic spaces,
 * invisible or steganographic codepoints and chat citation leftovers.
 */
final class AiMarkers {

    /**
     * Chat citation leftovers and tracking parameters, removed as a whole.
     */
    private static final @NonNull Pattern FRAGMENTS = Pattern.compile(
            // ChatGPT private-use citation span, e.g. U+E200 cite U+E202 turn0search0 U+E201
            "[^]*"
                    + "|:contentReference\\[oaicite:\\d+\\]\\{index=\\d+\\}"
                    + "|\u3010[^\u3011\\n]*\u2020[^\u3011\\n]*\u3011"
                    // utm_source=chatgpt.com as the only, the first or a subsequent URL parameter
                    + "|\\?utm_source=chatgpt\\.com(?![\\w%&-]|\\.\\w)"
                    + "|(?<=\\?)utm_source=chatgpt\\.com&"
                    + "|&utm_source=chatgpt\\.com(?![\\w%-]|\\.\\w)"
    );

    private AiMarkers() {
    }

    /**
     * Normalizes typographic characters and removes invisible codepoints and citation leftovers.
     *
     * @param input source string
     * @return cleaned string
     */
    static @NonNull String strip(@NonNull String input) {
        return scan(input).text;
    }

    /**
     * Finds everything {@link #strip(String)} would remove or replace.
     *
     * @param input source string
     * @return markers ordered by offset
     */
    static @NonNull List<@NonNull Marker> find(@NonNull String input) {
        return scan(input).markers;
    }

    private static @NonNull Result scan(@NonNull String input) {
        final StringBuilder out = new StringBuilder(input.length());
        final List<Marker> markers = new ArrayList<>();
        final Matcher fragments = FRAGMENTS.matcher(input);
        int nextFragment = fragments.find() ? fragments.start() : -1;
        int index = 0;
        while (index < input.length()) {
            if (index == nextFragment) {
                markers.add(new Marker(index, fragments.group(), ""));
                index = fragments.end();
                nextFragment = fragments.find() ? fragments.start() : -1;
                continue;
            }

            final int cp = input.codePointAt(index);
            final int size = Character.charCount(cp);
            final int prev = out.length() == 0 ? -1 : out.codePointBefore(out.length());
            final int next = index + size < input.length() ? input.codePointAt(index + size) : -1;
            final String replacement = replacement(cp, prev, next);
            if (replacement == null) {
                out.appendCodePoint(cp);
            } else {
                out.append(replacement);
                markers.add(new Marker(index, input.substring(index, index + size), replacement));
            }
            index += size;
        }
        return new Result(out.toString(), List.copyOf(markers));
    }

    /**
     * Resolves replacement for a single codepoint.
     *
     * @param cp   codepoint to check
     * @param prev last codepoint already written to output, -1 if none
     * @param next following codepoint in input, -1 if none
     * @return replacement, empty to remove, null to keep as is
     */
    private static @Nullable String replacement(int cp, int prev, int next) {
        // Tag block, used to embed hidden fingerprints
        if (cp >= 0xE0000 && cp <= 0xE007F) {
            return "";
        }
        // Variation selectors smuggle data in long runs; keep only one presentation selector after emoji
        if ((cp >= 0xFE00 && cp <= 0xFE0F) || (cp >= 0xE0100 && cp <= 0xE01EF)) {
            final boolean presentation = cp == 0xFE0E || cp == 0xFE0F;
            return presentation && (isEmoji(prev) || next == 0x20E3) ? null : "";
        }
        // Zero-width joiner glues emoji sequences such as families and flags
        if (cp == 0x200D) {
            return (isEmoji(prev) || prev == 0xFE0F) && isEmoji(next) ? null : "";
        }

        switch (cp) {
            // Hyphen, non-breaking hyphen, figure/en dash, minus sign, small/fullwidth hyphens
            case 0x2010:
            case 0x2011:
            case 0x2012:
            case 0x2013:
            case 0x2212:
            case 0xFE58:
            case 0xFE63:
            case 0xFF0D:
                return "-";
            // Em dash, horizontal bar, two-/three-em dash
            case 0x2014:
            case 0x2015:
            case 0x2E3A:
            case 0x2E3B:
                return (prev == -1 || isSpace(prev) ? "" : " ") + "-" + (next == -1 || isSpace(next) ? "" : " ");
            // Smart single quotes, low-9 quote, primes, fullwidth apostrophe
            case 0x2018:
            case 0x2019:
            case 0x201A:
            case 0x201B:
            case 0x2032:
            case 0x2035:
            case 0xFF07:
                return "'";
            // Smart double quotes, low-9, reversed, double primes, fullwidth quote
            case 0x201C:
            case 0x201D:
            case 0x201E:
            case 0x201F:
            case 0x2033:
            case 0x2036:
            case 0xFF02:
                return "\"";
            // Bullet, triangular/hyphen/white bullets
            case 0x2022:
            case 0x2023:
            case 0x2043:
            case 0x25E6:
                return "-";
            case 0x2026:
                return "...";
            // Line/paragraph separators and next line
            case 0x0085:
            case 0x2028:
            case 0x2029:
                return "\n";
            // Braille pattern blank renders as space
            case 0x2800:
                return " ";
            // Combining grapheme joiner, Mongolian variation selectors, Hangul fillers,
            // object replacement, private-use citation delimiters
            case 0x034F:
            case 0x115F:
            case 0x1160:
            case 0x180B:
            case 0x180C:
            case 0x180D:
            case 0x180F:
            case 0x3164:
            case 0xFFA0:
            case 0xFFFC:
            case 0xE200:
            case 0xE201:
            case 0xE202:
                return "";
            case ' ':
            case '\t':
            case '\n':
            case '\r':
                return null;
            default:
                break;
        }

        switch (Character.getType(cp)) {
            case Character.SPACE_SEPARATOR:
                return " ";
            case Character.CONTROL:
                return "";
            case Character.FORMAT:
                return isVisibleFormat(cp) ? null : "";
            default:
                return null;
        }
    }

    /**
     * Format characters that render a glyph or structure a script: prepended concatenation marks
     * and Egyptian hieroglyph format controls.
     */
    private static boolean isVisibleFormat(int cp) {
        return (cp >= 0x0600 && cp <= 0x0605)
                || cp == 0x06DD
                || cp == 0x070F
                || cp == 0x0890
                || cp == 0x0891
                || cp == 0x08E2
                || cp == 0x110BD
                || cp == 0x110CD
                || (cp >= 0x13430 && cp <= 0x1345F);
    }

    private static boolean isSpace(int cp) {
        return Character.isWhitespace(cp) || Character.isSpaceChar(cp);
    }

    /**
     * Approximates Extended_Pictographic, which {@link Character} exposes only since Java 21.
     */
    private static boolean isEmoji(int cp) {
        return cp == 0x00A9
                || cp == 0x00AE
                || cp == 0x203C
                || cp == 0x2049
                || cp == 0x2122
                || cp == 0x2139
                || (cp >= 0x2190 && cp <= 0x21FF)
                || (cp >= 0x2300 && cp <= 0x23FF)
                || cp == 0x24C2
                || (cp >= 0x25A0 && cp <= 0x27BF)
                || (cp >= 0x2900 && cp <= 0x297F)
                || (cp >= 0x2B00 && cp <= 0x2BFF)
                || cp == 0x3030
                || cp == 0x303D
                || cp == 0x3297
                || cp == 0x3299
                || (cp >= 0x1F000 && cp <= 0x1FAFF);
    }

    /**
     * Fragment of the source string that is removed or replaced.
     *
     * @param offset      UTF-16 offset in source string
     * @param original    original fragment
     * @param replacement replacement, empty when removed
     */
    record Marker(int offset, @NonNull String original, @NonNull String replacement) {
    }

    private static final class Result {

        private final @NonNull String text;
        private final @NonNull List<@NonNull Marker> markers;

        Result(@NonNull String text, @NonNull List<@NonNull Marker> markers) {
            this.text = text;
            this.markers = markers;
        }
    }
}

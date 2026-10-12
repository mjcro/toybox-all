package io.github.mjcro.toybox.toys;

import org.jspecify.annotations.NonNull;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.function.IntPredicate;
import java.util.function.ToIntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;

/**
 * Immutable snapshot of string metrics, character traits, hash and AI markers,
 * computed off the EDT for {@link StringAnalyzerToy}.
 */
final class StringAnalysis {
    static final int MAX_MARKERS = 1000;
    private static final @NonNull Pattern WORDS = Pattern.compile("[\\p{L}\\p{N}]+(?:['\\-][\\p{L}\\p{N}]+)*");
    private static final char[] HEX = "0123456789abcdef".toCharArray();
    private final @NonNull String source;
    private final int length;
    private final int codePoints;
    private final int bytes;
    private final long lines;
    private final long words;
    private final int @NonNull [] traits;
    private final @NonNull String hash;
    private final @NonNull List<@NonNull MarkerRow> markers;
    private final int markersTotal;

    private StringAnalysis(@NonNull String source, @NonNull HashAlgorithm algorithm) {
        final byte[] utf8 = source.getBytes(StandardCharsets.UTF_8);
        final List<AiMarkers.Marker> found = AiMarkers.find(source);
        final int[] counts = new int[Trait.values().length];
        for (final Trait trait : Trait.values()) {
            counts[trait.ordinal()] = trait.counter.applyAsInt(source);
        }
        this.source = source;
        this.length = source.length();
        this.codePoints = source.codePointCount(0, source.length());
        this.bytes = utf8.length;
        this.lines = source.lines().count();
        this.words = WORDS.matcher(source).results().count();
        this.traits = counts;
        this.hash = algorithm.digest(utf8);
        this.markers = MarkerRow.of(source, found);
        this.markersTotal = found.size();
    }

    /**
     * Analyzes given string.
     *
     * @param source    string to analyze
     * @param algorithm hash algorithm to apply to UTF-8 bytes
     * @return analysis result
     */
    static @NonNull StringAnalysis of(@NonNull String source, @NonNull HashAlgorithm algorithm) {
        return new StringAnalysis(source, algorithm);
    }

    @NonNull String source() {
        return source;
    }

    int length() {
        return length;
    }

    int codePoints() {
        return codePoints;
    }

    int bytes() {
        return bytes;
    }

    long lines() {
        return lines;
    }

    long words() {
        return words;
    }

    int count(@NonNull Trait trait) {
        return traits[trait.ordinal()];
    }

    @NonNull String hash() {
        return hash;
    }

    /**
     * Returns at most {@link #MAX_MARKERS} markers ordered by offset.
     */
    @NonNull List<@NonNull MarkerRow> markers() {
        return markers;
    }

    int markersTotal() {
        return markersTotal;
    }

    private static int countCodePoints(@NonNull String s, @NonNull IntPredicate predicate) {
        return (int) s.codePoints().filter(predicate).count();
    }

    private static boolean isScript(int cp, Character.@NonNull UnicodeScript script) {
        return Character.isLetter(cp) && Character.UnicodeScript.of(cp) == script;
    }

    private static boolean isOtherScript(int cp) {
        if (!Character.isLetter(cp)) {
            return false;
        }
        final Character.UnicodeScript script = Character.UnicodeScript.of(cp);
        return script != Character.UnicodeScript.LATIN
                && script != Character.UnicodeScript.CYRILLIC
                && script != Character.UnicodeScript.COMMON
                && script != Character.UnicodeScript.INHERITED;
    }

    private static boolean isEmoji(int cp) {
        return Character.getType(cp) == Character.OTHER_SYMBOL
                && (cp >= 0x1F000 || (cp >= 0x2600 && cp <= 0x27BF));
    }

    private static boolean isPunctuation(int cp) {
        switch (Character.getType(cp)) {
            case Character.CONNECTOR_PUNCTUATION:
            case Character.DASH_PUNCTUATION:
            case Character.START_PUNCTUATION:
            case Character.END_PUNCTUATION:
            case Character.INITIAL_QUOTE_PUNCTUATION:
            case Character.FINAL_QUOTE_PUNCTUATION:
            case Character.OTHER_PUNCTUATION:
                return true;
            default:
                return false;
        }
    }

    private static boolean isSymbol(int cp) {
        switch (Character.getType(cp)) {
            case Character.MATH_SYMBOL:
            case Character.CURRENCY_SYMBOL:
            case Character.MODIFIER_SYMBOL:
                return true;
            case Character.OTHER_SYMBOL:
                return !isEmoji(cp);
            default:
                return false;
        }
    }

    private static boolean isControl(int cp) {
        return Character.getType(cp) == Character.CONTROL && cp != '\t' && cp != '\n' && cp != '\r';
    }

    private static int countLeadingWhitespace(@NonNull String s) {
        int i = 0;
        while (i < s.length() && Character.isWhitespace(s.charAt(i))) {
            i++;
        }
        return i;
    }

    private static int countTrailingWhitespace(@NonNull String s) {
        // Fully blank string counts as leading only
        if (countLeadingWhitespace(s) == s.length()) {
            return 0;
        }
        int i = s.length();
        while (i > 0 && Character.isWhitespace(s.charAt(i - 1))) {
            i--;
        }
        return s.length() - i;
    }

    private static int countMixedWords(@NonNull String s) {
        int count = 0;
        final Matcher m = WORDS.matcher(s);
        while (m.find()) {
            final String word = m.group();
            if (word.codePoints().anyMatch($ -> isScript($, Character.UnicodeScript.LATIN))
                    && word.codePoints().anyMatch($ -> isScript($, Character.UnicodeScript.CYRILLIC))) {
                count++;
            }
        }
        return count;
    }

    private static @NonNull String hex(byte @NonNull [] bytes) {
        final char[] out = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            out[i * 2] = HEX[(bytes[i] >> 4) & 0xF];
            out[i * 2 + 1] = HEX[bytes[i] & 0xF];
        }
        return new String(out);
    }

    /**
     * Group of related {@link Trait}s, displayed as one row.
     */
    enum TraitGroup {
        SCRIPTS("Scripts"),
        CONTENT("Content"),
        WHITESPACE("Whitespace");

        final @NonNull String text;

        TraitGroup(@NonNull String text) {
            this.text = text;
        }
    }

    /**
     * Countable character trait; warning traits usually indicate a problem.
     */
    enum Trait {
        LATIN(TraitGroup.SCRIPTS, "Latin", "Latin letters", false,
                s -> countCodePoints(s, cp -> isScript(cp, Character.UnicodeScript.LATIN))),
        CYRILLIC(TraitGroup.SCRIPTS, "Cyrillic", "Cyrillic letters", false,
                s -> countCodePoints(s, cp -> isScript(cp, Character.UnicodeScript.CYRILLIC))),
        OTHER_SCRIPTS(TraitGroup.SCRIPTS, "Other", "Letters of scripts other than Latin and Cyrillic", false,
                s -> countCodePoints(s, StringAnalysis::isOtherScript)),
        EMOJI(TraitGroup.SCRIPTS, "Emoji", "Emoji and pictographs", false,
                s -> countCodePoints(s, StringAnalysis::isEmoji)),
        MIXED_WORDS(TraitGroup.SCRIPTS, "Mixed words", "Words containing both Latin and Cyrillic letters (possible homoglyphs)", true,
                StringAnalysis::countMixedWords),
        LETTERS(TraitGroup.CONTENT, "Letters", "Letters of any script", false,
                s -> countCodePoints(s, Character::isLetter)),
        DIGITS(TraitGroup.CONTENT, "Digits", "Decimal digits of any script", false,
                s -> countCodePoints(s, Character::isDigit)),
        PUNCTUATION(TraitGroup.CONTENT, "Punctuation", "Punctuation marks, dashes, brackets and quotes", false,
                s -> countCodePoints(s, StringAnalysis::isPunctuation)),
        SYMBOLS(TraitGroup.CONTENT, "Symbols", "Math, currency and other symbols", false,
                s -> countCodePoints(s, StringAnalysis::isSymbol)),
        LEADING(TraitGroup.WHITESPACE, "Leading", "Whitespace at the beginning of the text", true,
                StringAnalysis::countLeadingWhitespace),
        TRAILING(TraitGroup.WHITESPACE, "Trailing", "Whitespace at the end of the text", true,
                StringAnalysis::countTrailingWhitespace),
        TABS(TraitGroup.WHITESPACE, "Tabs", "Tab characters", false,
                s -> countCodePoints(s, cp -> cp == '\t')),
        CR(TraitGroup.WHITESPACE, "CR", "Carriage returns (Windows or classic Mac line endings)", false,
                s -> countCodePoints(s, cp -> cp == '\r')),
        CONTROL(TraitGroup.WHITESPACE, "Control", "Control characters other than tab, LF and CR", true,
                s -> countCodePoints(s, StringAnalysis::isControl));

        final @NonNull TraitGroup group;
        final @NonNull String text;
        final @NonNull String description;
        final boolean warning;
        private final @NonNull ToIntFunction<@NonNull String> counter;

        Trait(
                @NonNull TraitGroup group,
                @NonNull String text,
                @NonNull String description,
                boolean warning,
                @NonNull ToIntFunction<@NonNull String> counter
        ) {
            this.group = group;
            this.text = text;
            this.description = description;
            this.warning = warning;
            this.counter = counter;
        }
    }

    /**
     * Hash algorithm applied to UTF-8 bytes.
     */
    enum HashAlgorithm {
        CRC32("CRC32"),
        MD5("MD5"),
        SHA1("SHA-1"),
        SHA256("SHA-256"),
        SHA512("SHA-512");

        private final @NonNull String text;

        HashAlgorithm(@NonNull String text) {
            this.text = text;
        }

        @NonNull String digest(byte @NonNull [] bytes) {
            if (this == CRC32) {
                final CRC32 crc = new CRC32();
                crc.update(bytes);
                return String.format("%08x", crc.getValue());
            }
            try {
                return hex(MessageDigest.getInstance(text).digest(bytes));
            } catch (NoSuchAlgorithmException e) {
                // Every listed algorithm is mandatory for Java platform implementations
                throw new IllegalStateException(e);
            }
        }

        @Override
        public @NonNull String toString() {
            return text;
        }
    }

    /**
     * What {@link AiMarkers#strip(String)} does with the marker.
     */
    enum MarkerAction {
        REMOVED,
        REPLACED
    }

    /**
     * Display-ready AI marker with its location in the source string.
     *
     * @param offset      UTF-16 offset in the source
     * @param length      UTF-16 length in the source
     * @param position    {@code line:column}, both 1-based, column in code points
     * @param code        {@code U+XXXX} for a single code point, empty for fragments
     * @param name        Unicode name or escaped fragment
     * @param action      what stripping does
     * @param replacement escaped replacement, empty when removed
     */
    record MarkerRow(
            int offset,
            int length,
            @NonNull String position,
            @NonNull String code,
            @NonNull String name,
            @NonNull MarkerAction action,
            @NonNull String replacement
    ) {
        private static @NonNull List<@NonNull MarkerRow> of(
                @NonNull String s,
                @NonNull List<AiMarkers.@NonNull Marker> markers
        ) {
            final int size = Math.min(markers.size(), MAX_MARKERS);
            final MarkerRow[] rows = new MarkerRow[size];
            int line = 1;
            int lineStart = 0;
            int scanned = 0;
            for (int i = 0; i < size; i++) {
                final AiMarkers.Marker marker = markers.get(i);
                for (; scanned < marker.offset(); scanned++) {
                    if (s.charAt(scanned) == '\n') {
                        line++;
                        lineStart = scanned + 1;
                    }
                }
                final int column = s.codePointCount(lineStart, marker.offset()) + 1;
                final String original = marker.original();
                final boolean single = original.codePointCount(0, original.length()) == 1;
                final String code;
                final String name;
                if (single) {
                    final int cp = original.codePointAt(0);
                    final String unicodeName = Character.getName(cp);
                    code = String.format("U+%04X", cp);
                    name = unicodeName == null ? "UNASSIGNED" : unicodeName;
                } else {
                    code = "";
                    name = "\"" + escape(original) + "\"";
                }
                final boolean removed = marker.replacement().isEmpty();
                rows[i] = new MarkerRow(
                        marker.offset(),
                        original.length(),
                        line + ":" + column,
                        code,
                        name,
                        removed ? MarkerAction.REMOVED : MarkerAction.REPLACED,
                        removed ? "" : "\"" + escape(marker.replacement()) + "\""
                );
            }
            return List.of(rows);
        }

        /**
         * Replaces invisible and line-breaking code points with {@code \\u} escapes.
         */
        private static @NonNull String escape(@NonNull String s) {
            final StringBuilder sb = new StringBuilder(s.length());
            s.codePoints().forEach(cp -> {
                final int type = Character.getType(cp);
                if (cp == ' ' || (type != Character.CONTROL
                        && type != Character.FORMAT
                        && type != Character.PRIVATE_USE
                        && type != Character.SPACE_SEPARATOR
                        && type != Character.LINE_SEPARATOR
                        && type != Character.PARAGRAPH_SEPARATOR)) {
                    sb.appendCodePoint(cp);
                } else {
                    for (final char c : Character.toChars(cp)) {
                        sb.append(String.format("\\u%04X", (int) c));
                    }
                }
            });
            return sb.toString();
        }
    }
}

package io.github.mjcro.toybox.toys;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

/**
 * Unit tests for {@link AiMarkers}.
 */
class AiMarkersTest {
    /**
     * Provides test cases of expected output and input pairs.
     *
     * @return stream of test arguments
     */
    static Stream<Arguments> stripDataProvider() {
        return Stream.of(
                Arguments.of("", ""),
                Arguments.of("plain ASCII text, 1-5 \"ok\"", "plain ASCII text, 1-5 \"ok\""),
                Arguments.of("a\tb\r\nc", "a\tb\r\nc"),
                // Zero-width, bidi and other format characters
                Arguments.of("ab", "a​b"),
                Arguments.of("abcde", "a؜b⁪c￹d𝅳e"),
                Arguments.of("ab", "a­b﻿"),
                // Prepended concatenation mark is a visible glyph
                Arguments.of("؀", "؀"),
                // Line separators become line feeds
                Arguments.of("one\ntwo\nthree\nfour", "one two three\u0085four"),
                // Exotic spaces
                Arguments.of("a b c d", "a b c　d"),
                // Control characters
                Arguments.of("xyz", "x\u0007y\u009Bz"),
                // Blank-looking characters
                Arguments.of("abc d", "a͏bㅤc⠀d"),
                // Tag block
                Arguments.of("ab", "a󠁁󠁿b"),
                // Emoji sequences stay intact
                Arguments.of("👨‍👩‍👧", "👨‍👩‍👧"),
                Arguments.of("❤️‍🔥", "❤️‍🔥"),
                Arguments.of("1️⃣", "1️⃣"),
                // Joiners and selectors outside of emoji
                Arguments.of("ab", "a‍b"),
                Arguments.of("ab", "a️b"),
                Arguments.of("❤️", "❤️️"),
                // Variation selector smuggling
                Arguments.of("😀", "😀︁︂󠄀󠇯"),
                // Dashes
                Arguments.of("1-5", "1–5"),
                Arguments.of("word - word", "word—word"),
                Arguments.of("word - word", "word — word"),
                Arguments.of("word -\n- start", "word—\n—start"),
                // Typography
                Arguments.of(
                        "\"hi\" 'x' 5' 6\" - item ... \"q\"",
                        "“hi” ‘x’ 5′ 6″ • item … ＂q＂"
                ),
                // Citation leftovers
                Arguments.of("Fact.", "Factciteturn0search0."),
                Arguments.of("Fact.", "Fact:contentReference[oaicite:0]{index=0}."),
                Arguments.of("Fact.", "Fact【4:0†source】."),
                Arguments.of("ab", "ab"),
                // Tracking parameter
                Arguments.of("https://x.io/a", "https://x.io/a?utm_source=chatgpt.com"),
                Arguments.of("(https://x.io/a).", "(https://x.io/a?utm_source=chatgpt.com)."),
                Arguments.of("https://x.io/a#top", "https://x.io/a?utm_source=chatgpt.com#top"),
                Arguments.of("https://x.io/a?b=1", "https://x.io/a?utm_source=chatgpt.com&b=1"),
                Arguments.of("https://x.io/a?b=1&c=2", "https://x.io/a?b=1&utm_source=chatgpt.com&c=2"),
                Arguments.of("https://x.io/a?utm_source=chatgpt.community", "https://x.io/a?utm_source=chatgpt.community")
        );
    }

    /**
     * Verifies that strip produces the expected output.
     *
     * @param expected expected output
     * @param input    input string
     */
    @ParameterizedTest
    @MethodSource("stripDataProvider")
    void testStrip(String expected, String input) {
        Assertions.assertEquals(expected, AiMarkers.strip(input));
    }

    /**
     * Verifies that find reports offsets in the source string.
     */
    @Test
    void testFind() {
        Assertions.assertEquals(
                List.of(
                        new AiMarkers.Marker(1, "​", ""),
                        new AiMarkers.Marker(3, ":contentReference[oaicite:0]{index=0}", ""),
                        new AiMarkers.Marker(41, "—", " - ")
                ),
                AiMarkers.find("a​b:contentReference[oaicite:0]{index=0}c—d")
        );
        Assertions.assertEquals(List.of(), AiMarkers.find("clean"));
    }
}

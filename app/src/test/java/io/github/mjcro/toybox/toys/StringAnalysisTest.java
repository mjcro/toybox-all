package io.github.mjcro.toybox.toys;

import io.github.mjcro.toybox.toys.StringAnalysis.HashAlgorithm;
import io.github.mjcro.toybox.toys.StringAnalysis.MarkerAction;
import io.github.mjcro.toybox.toys.StringAnalysis.MarkerRow;
import io.github.mjcro.toybox.toys.StringAnalysis.Trait;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

/**
 * Unit tests for {@link StringAnalysis}.
 */
class StringAnalysisTest {
    /**
     * Provides trait, input and expected count.
     *
     * @return stream of test arguments
     */
    static Stream<Arguments> traitDataProvider() {
        return Stream.of(
                Arguments.of(Trait.LATIN, "abc где", 3),
                Arguments.of(Trait.CYRILLIC, "abc где", 3),
                Arguments.of(Trait.OTHER_SCRIPTS, "aβγ中", 3),
                Arguments.of(Trait.EMOJI, "hi 😀 ☀ ©", 2),
                Arguments.of(Trait.MIXED_WORDS, "pаypal ok сок", 1),
                Arguments.of(Trait.DIGITS, "a1b22", 3),
                Arguments.of(Trait.PUNCTUATION, "a, b — «c»!", 5),
                Arguments.of(Trait.SYMBOLS, "1+1=2 $ ©", 4),
                Arguments.of(Trait.LEADING, "  \tx ", 3),
                Arguments.of(Trait.TRAILING, " x\n\n", 2),
                Arguments.of(Trait.TRAILING, "   ", 0),
                Arguments.of(Trait.TABS, "a\tb\t", 2),
                Arguments.of(Trait.CR, "a\r\nb\r\n", 2),
                Arguments.of(Trait.CONTROL, "a\tb\nc\r\u0007\u0000", 2)
        );
    }

    @ParameterizedTest
    @MethodSource("traitDataProvider")
    void testTraitCount(Trait trait, String input, int expected) {
        Assertions.assertEquals(expected, StringAnalysis.of(input, HashAlgorithm.CRC32).count(trait));
    }

    @Test
    void testSizes() {
        final StringAnalysis a = StringAnalysis.of("Привет 😀\nworld", HashAlgorithm.CRC32);
        Assertions.assertEquals(15, a.length());
        Assertions.assertEquals(14, a.codePoints());
        Assertions.assertEquals(23, a.bytes());
        Assertions.assertEquals(2, a.lines());
        Assertions.assertEquals(2, a.words());
    }

    @Test
    void testHash() {
        Assertions.assertEquals("cbf43926", StringAnalysis.of("123456789", HashAlgorithm.CRC32).hash());
        Assertions.assertEquals("900150983cd24fb0d6963f7d28e17f72", StringAnalysis.of("abc", HashAlgorithm.MD5).hash());
        Assertions.assertEquals(
                "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad",
                StringAnalysis.of("abc", HashAlgorithm.SHA256).hash()
        );
    }

    @Test
    void testMarkers() {
        final StringAnalysis a = StringAnalysis.of("ab\nc​d—e", HashAlgorithm.CRC32);
        Assertions.assertEquals(2, a.markersTotal());

        final MarkerRow zwsp = a.markers().get(0);
        Assertions.assertEquals(4, zwsp.offset());
        Assertions.assertEquals(1, zwsp.length());
        Assertions.assertEquals("2:2", zwsp.position());
        Assertions.assertEquals("U+200B", zwsp.code());
        Assertions.assertEquals("ZERO WIDTH SPACE", zwsp.name());
        Assertions.assertEquals(MarkerAction.REMOVED, zwsp.action());
        Assertions.assertEquals("", zwsp.replacement());

        final MarkerRow dash = a.markers().get(1);
        Assertions.assertEquals("2:4", dash.position());
        Assertions.assertEquals(MarkerAction.REPLACED, dash.action());
        Assertions.assertFalse(dash.replacement().isEmpty());
    }

    @Test
    void testMarkersCapped() {
        final StringAnalysis a = StringAnalysis.of("​".repeat(StringAnalysis.MAX_MARKERS + 5), HashAlgorithm.CRC32);
        Assertions.assertEquals(StringAnalysis.MAX_MARKERS + 5, a.markersTotal());
        Assertions.assertEquals(StringAnalysis.MAX_MARKERS, a.markers().size());
    }
}

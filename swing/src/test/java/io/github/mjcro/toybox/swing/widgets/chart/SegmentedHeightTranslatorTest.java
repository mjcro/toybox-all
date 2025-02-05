package io.github.mjcro.toybox.swing.widgets.chart;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class SegmentedHeightTranslatorTest {
    static Stream<Arguments> translateDataProvider() {
        return Stream.of(
                Arguments.of(0, -100f),
                Arguments.of(0, 0f),
                Arguments.of(33, 1.f),
                Arguments.of(166, 5.f),
                Arguments.of(333, 10.f),
                Arguments.of(337, 11.f),
                Arguments.of(370, 20.f),
                Arguments.of(481, 50.f),
                Arguments.of(666, 100.f),
                Arguments.of(667, 100.1f),
                Arguments.of(812, 500.f),
                Arguments.of(962, 900.f),
                Arguments.of(999, 1000.f),
                Arguments.of(999, 10000.f)
        );
    }

    @ParameterizedTest
    @MethodSource("translateDataProvider")
    public void testTranslate(int expected, float value) {
        Assertions.assertEquals(expected, new SegmentedHeightTranslator(1000, 10, 100, 1000).translate(value));
    }
}
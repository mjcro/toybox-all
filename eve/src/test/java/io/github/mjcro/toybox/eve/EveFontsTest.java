package io.github.mjcro.toybox.eve;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EveFontsTest {
    private static final String SAMPLE = "Sample Пример текста ЁёЇїІі ÄÖÜ 0123456789";

    @Test
    void registersBundledFamilies() {
        EveFonts.register();
        final List<String> families = List.of(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        Assertions.assertTrue(families.contains(EveFonts.FAMILY_SANS), "Play is registered");
        Assertions.assertTrue(families.contains(EveFonts.FAMILY_MONO), "JetBrains Mono is registered");
    }

    @Test
    void fontsDisplayLatinAndCyrillic() {
        for (Font font : List.of(EveFonts.regular(), EveFonts.bold(), EveFonts.small(), EveFonts.header(), EveFonts.mono())) {
            Assertions.assertEquals(-1, font.canDisplayUpTo(SAMPLE), font.getFontName() + " displays the sample");
        }
    }

    @Test
    void boldResolvesToRealBoldFace() {
        Assertions.assertEquals("Play Bold", EveFonts.bold().getFontName());
        Assertions.assertEquals("Play Bold", EveFonts.regular().deriveFont(Font.BOLD).getFontName());
        Assertions.assertEquals("Play", EveFonts.regular().getFamily());
    }
}

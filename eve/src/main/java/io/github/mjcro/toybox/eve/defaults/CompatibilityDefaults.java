package io.github.mjcro.toybox.eve.defaults;

import javax.swing.UIDefaults;

import io.github.mjcro.toybox.eve.EveFonts;
import io.github.mjcro.toybox.eve.EvePalette;
import io.github.mjcro.toybox.eve.plaf.WindowsIcons;

/**
 * Commonly used non-standard UI keys (popularized by FlatLaf) mapped to Eve values.
 *
 * <p>Lets applications written against such keys run under Eve without code changes.
 * Keys already defined by the Basic defaults or by Eve component groups are never overridden.
 */
public final class CompatibilityDefaults {
    private static final int SMALL_GLYPH = 12;

    private CompatibilityDefaults() {
    }

    /**
     * Puts the compatibility keys missing from the table.
     * Invoked by {@link io.github.mjcro.toybox.eve.EveLookAndFeel} after all component groups.
     *
     * @param table defaults table being initialized, not null.
     */
    public static void contribute(UIDefaults table) {
        final Object[] pairs = {
                // Window title pane glyphs, also used by applications for tab close buttons
                "TitlePane.small.closeIcon", WindowsIcons.close(SMALL_GLYPH, EvePalette.TEXT),
                // Semantic action colors
                "Actions.Red", EvePalette.DANGER,
                "Actions.Yellow", EvePalette.WARNING,
                "Actions.Green", EvePalette.SUCCESS,
                "Actions.Blue", EvePalette.ACCENT,
                "Actions.Grey", EvePalette.TEXT_DIM,
                "Actions.GreyInline", EvePalette.TEXT_DIM,
                "Component.linkColor", EvePalette.ACCENT,
                "monospaced.font", EveFonts.mono(),
                "Table.selectionInactiveForeground", EvePalette.TEXT,
                "Table.selectionInactiveBackground", EvePalette.SELECTION_INACTIVE,
        };
        for (int i = 0; i < pairs.length; i += 2) {
            if (table.get(pairs[i]) == null) {
                table.put(pairs[i], pairs[i + 1]);
            }
        }
    }
}

package io.github.mjcro.toybox.eve.defaults;

import javax.swing.SwingConstants;
import javax.swing.UIDefaults;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.InsetsUIResource;

import io.github.mjcro.toybox.eve.EvePalette;
import io.github.mjcro.toybox.eve.plaf.TextFrameBorder;

/**
 * UI defaults of the text component group (text components, combo boxes, spinners).
 */
public final class TextDefaults {
    private static final String[] TEXT_PREFIXES = {
            "TextField", "FormattedTextField", "PasswordField", "TextArea", "TextPane", "EditorPane",
    };

    private TextDefaults() {
    }

    /**
     * Puts the group-specific defaults into the table.
     * Invoked by {@link io.github.mjcro.toybox.eve.EveLookAndFeel} after the shared defaults,
     * so values put here override them.
     *
     * @param table defaults table being initialized, not null.
     */
    public static void contribute(UIDefaults table) {
        for (String prefix : TEXT_PREFIXES) {
            final boolean field = prefix.endsWith("Field");
            // Read-only rich text is usually a display surface, so only fields dim when not editable.
            final ColorUIResource inactive = field ? EvePalette.PANEL : EvePalette.SURFACE;
            table.putDefaults(new Object[]{
                    prefix + ".background", EvePalette.SURFACE,
                    prefix + ".foreground", EvePalette.TEXT,
                    prefix + ".inactiveForeground", EvePalette.TEXT_DIM,
                    prefix + ".inactiveBackground", inactive,
                    prefix + ".disabledBackground", EvePalette.PANEL,
                    prefix + ".selectionBackground", EvePalette.SELECTION,
                    prefix + ".selectionForeground", EvePalette.TEXT_BRIGHT,
                    prefix + ".caretForeground", EvePalette.ACCENT,
                    prefix + ".margin", new InsetsUIResource(0, 0, 0, 0),
            });
        }
        table.putDefaults(new Object[]{
                "PasswordField.echoChar", '•',

                "ComboBox.background", EvePalette.SURFACE,
                "ComboBox.foreground", EvePalette.TEXT,
                "ComboBox.disabledBackground", EvePalette.PANEL,
                "ComboBox.disabledForeground", EvePalette.TEXT_DIM,
                "ComboBox.selectionBackground", EvePalette.SELECTION,
                "ComboBox.selectionForeground", EvePalette.TEXT_BRIGHT,
                "ComboBox.buttonBackground", EvePalette.SURFACE,
                "ComboBox.border", new TextFrameBorder(1, 1, 1, 1),
                "ComboBox.padding", new InsetsUIResource(2, 6, 2, 6),
                "ComboBox.squareButton", Boolean.FALSE,

                "Spinner.background", EvePalette.SURFACE,
                "Spinner.foreground", EvePalette.TEXT,
                "Spinner.border", new TextFrameBorder(2, 6, 3, 1),
                "Spinner.arrowButtonInsets", new InsetsUIResource(1, 1, 1, 1),
                "Spinner.editorBorderPainted", Boolean.FALSE,
                "Spinner.editorAlignment", SwingConstants.LEADING,
        });
    }
}

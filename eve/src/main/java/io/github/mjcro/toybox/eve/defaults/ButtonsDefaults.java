package io.github.mjcro.toybox.eve.defaults;

import javax.swing.UIDefaults;
import javax.swing.plaf.InsetsUIResource;

import io.github.mjcro.toybox.eve.EveFonts;
import io.github.mjcro.toybox.eve.EvePalette;
import io.github.mjcro.toybox.eve.plaf.ButtonsBorders;
import io.github.mjcro.toybox.eve.plaf.ButtonsIcons;

/**
 * UI defaults of the buttons component group: buttons, toggles, check boxes, radio buttons,
 * labels, panels, separators, tooltips and titled borders.
 */
public final class ButtonsDefaults {
    private ButtonsDefaults() {
    }

    /**
     * Puts the group-specific defaults into the table.
     * Invoked by {@link io.github.mjcro.toybox.eve.EveLookAndFeel} after the shared defaults,
     * so values put here override them.
     *
     * @param table defaults table being initialized, not null.
     */
    public static void contribute(UIDefaults table) {
        table.putDefaults(new Object[]{
                "Button.border", ButtonsBorders.button(),
                "Button.margin", new InsetsUIResource(0, 0, 0, 0),
                "Button.textShiftOffset", 0,
                "Button.disabledText", EvePalette.TEXT_DIM,
                "ToggleButton.border", ButtonsBorders.button(),
                "ToggleButton.margin", new InsetsUIResource(0, 0, 0, 0),
                "ToggleButton.textShiftOffset", 0,
                "ToggleButton.disabledText", EvePalette.TEXT_DIM,

                "CheckBox.icon", ButtonsIcons.checkBox(),
                "CheckBox.margin", new InsetsUIResource(2, 2, 2, 2),
                "CheckBox.textIconGap", 5,
                "CheckBox.disabledText", EvePalette.TEXT_DIM,
                "RadioButton.icon", ButtonsIcons.radioButton(),
                "RadioButton.margin", new InsetsUIResource(2, 2, 2, 2),
                "RadioButton.textIconGap", 5,
                "RadioButton.disabledText", EvePalette.TEXT_DIM,

                "Label.disabledForeground", EvePalette.TEXT_DIM,
                "Separator.foreground", EvePalette.BORDER,
                "Separator.highlight", EvePalette.BORDER,
                "Separator.shadow", EvePalette.BORDER,

                "ToolTip.background", EvePalette.RAISED,
                "ToolTip.foreground", EvePalette.TEXT_BRIGHT,
                "ToolTip.backgroundInactive", EvePalette.PANEL,
                "ToolTip.foregroundInactive", EvePalette.TEXT_DIM,
                "ToolTip.border", ButtonsBorders.toolTip(),
                "ToolTip.borderInactive", ButtonsBorders.toolTip(),
                "ToolTip.font", EveFonts.small(),

                "TitledBorder.border", ButtonsBorders.titledFrame(),
                "TitledBorder.font", EveFonts.header(),
                "TitledBorder.titleColor", EvePalette.ACCENT,
                "TitledBorder.position", "TOP",
        });
    }
}

package io.github.mjcro.toybox.eve.defaults;

import javax.swing.UIDefaults;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.plaf.InsetsUIResource;

import io.github.mjcro.toybox.eve.EveBorders;
import io.github.mjcro.toybox.eve.EveFonts;
import io.github.mjcro.toybox.eve.EvePalette;

/**
 * UI defaults of the ranges (sliders, progress bars, scroll bars, scroll panes, viewports, split panes) component group.
 */
public final class RangesDefaults {
    private RangesDefaults() {
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
                // Scroll bars: slim, no arrows, see EveScrollBarUI.
                "ScrollBar.width", 8,
                // Track matches content surfaces, so bars blend into scroll panes yet stay visible on panels.
                "ScrollBar.background", EvePalette.SURFACE,
                "ScrollBar.foreground", EvePalette.BORDER,
                "ScrollBar.track", EvePalette.SURFACE,
                "ScrollBar.thumb", EvePalette.BORDER,
                "ScrollBar.thumbHover", EvePalette.BORDER_BRIGHT,
                "ScrollBar.thumbPressed", EvePalette.ACCENT,
                "ScrollBar.minimumThumbSize", new DimensionUIResource(24, 24),
                "ScrollBar.border", EveBorders.empty(0, 0, 0, 0),

                // Scroll panes and viewports.
                "ScrollPane.background", EvePalette.SURFACE,
                "ScrollPane.foreground", EvePalette.TEXT,
                "ScrollPane.border", EveBorders.frame(),
                "ScrollPane.viewportBorder", EveBorders.empty(0, 0, 0, 0),
                "Viewport.background", EvePalette.SURFACE,

                // Sliders: labels use the slider font and foreground.
                "Slider.background", EvePalette.PANEL,
                "Slider.foreground", EvePalette.TEXT_DIM,
                "Slider.font", EveFonts.small(),
                "Slider.tickColor", EvePalette.TEXT_DIM,
                "Slider.focus", EvePalette.PANEL,
                "Slider.focusInsets", new InsetsUIResource(2, 2, 2, 2),
                "Slider.horizontalSize", new DimensionUIResource(160, 19),
                "Slider.verticalSize", new DimensionUIResource(19, 160),
                "Slider.minimumHorizontalSize", new DimensionUIResource(36, 19),
                "Slider.minimumVerticalSize", new DimensionUIResource(19, 36),

                // Progress bars.
                "ProgressBar.background", EvePalette.SURFACE,
                "ProgressBar.foreground", EvePalette.ACCENT,
                "ProgressBar.selectionForeground", EvePalette.BACKGROUND,
                "ProgressBar.selectionBackground", EvePalette.TEXT_BRIGHT,
                "ProgressBar.border", EveBorders.frame(),
                "ProgressBar.font", EveFonts.small(),
                "ProgressBar.horizontalSize", new DimensionUIResource(146, 10),
                "ProgressBar.verticalSize", new DimensionUIResource(10, 146),
                "ProgressBar.repaintInterval", 33,
                "ProgressBar.cycleTime", 1600,

                // Split panes: frameless, thin flat divider.
                "SplitPane.background", EvePalette.PANEL,
                "SplitPane.border", EveBorders.empty(0, 0, 0, 0),
                "SplitPaneDivider.border", EveBorders.empty(0, 0, 0, 0),
                "SplitPane.dividerSize", 5,
                "SplitPane.oneTouchButtonSize", 5,
                "SplitPane.oneTouchButtonOffset", 4,
                "SplitPane.centerOneTouchButtons", Boolean.FALSE,
                "SplitPaneDivider.draggingColor", EvePalette.ACCENT_DARK,
        });
    }
}

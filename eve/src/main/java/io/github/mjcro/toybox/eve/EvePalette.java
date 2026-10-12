package io.github.mjcro.toybox.eve;

import java.awt.Color;

import javax.swing.plaf.ColorUIResource;

/**
 * Photon color palette of the Eve Look and Feel.
 *
 * <p>All constants are non-null {@link ColorUIResource} instances, so they can be
 * placed into {@link javax.swing.UIDefaults} directly and get replaced on Look and
 * Feel switch. Surfaces go from darkest ({@link #BACKGROUND}) to lightest
 * ({@link #RAISED}); text goes from {@link #TEXT_DIM} to {@link #TEXT_BRIGHT}.
 */
public final class EvePalette {
    /** Deepest backdrop: desktop panes, viewports behind content, window frames. */
    public static final ColorUIResource BACKGROUND = new ColorUIResource(0x0B0E12);
    /** Default panel and window content background. */
    public static final ColorUIResource PANEL = new ColorUIResource(0x12171D);
    /** Input fields, lists, tables, trees and other content surfaces. */
    public static final ColorUIResource SURFACE = new ColorUIResource(0x1A2129);
    /** Buttons, headers, title bars and other raised elements. */
    public static final ColorUIResource RAISED = new ColorUIResource(0x222B35);
    /** Raised element under the mouse pointer. */
    public static final ColorUIResource RAISED_HOVER = new ColorUIResource(0x2A3541);
    /** Regular 1px frame color. */
    public static final ColorUIResource BORDER = new ColorUIResource(0x2E3A46);
    /** Highlighted frame color (hover, active window). */
    public static final ColorUIResource BORDER_BRIGHT = new ColorUIResource(0x4A5B6B);
    /** Thin separators and grid lines; dimmer than {@link #BORDER}. */
    public static final ColorUIResource GRID = new ColorUIResource(0x222A33);
    /** Regular text. */
    public static final ColorUIResource TEXT = new ColorUIResource(0xC8D2DA);
    /** Secondary or disabled text. */
    public static final ColorUIResource TEXT_DIM = new ColorUIResource(0x7D8B97);
    /** Emphasized text: titles, selected items, headers. */
    public static final ColorUIResource TEXT_BRIGHT = new ColorUIResource(0xE8F0F5);
    /** Main accent: focus, selection markers, progress, active controls. */
    public static final ColorUIResource ACCENT = new ColorUIResource(0x4FC3D9);
    /** Darker accent used for pressed states and accent fills behind text. */
    public static final ColorUIResource ACCENT_DARK = new ColorUIResource(0x2B7F92);
    /** Selection background for lists, tables, trees and text. */
    public static final ColorUIResource SELECTION = new ColorUIResource(0x1F4A57);
    /** Selection background when the owning component is not focused. */
    public static final ColorUIResource SELECTION_INACTIVE = new ColorUIResource(0x26323D);
    /** Warning color (amber). */
    public static final ColorUIResource WARNING = new ColorUIResource(0xE0A040);
    /** Error / danger color (red). */
    public static final ColorUIResource DANGER = new ColorUIResource(0xD9534F);
    /** Success color (green). */
    public static final ColorUIResource SUCCESS = new ColorUIResource(0x5CB85C);

    private EvePalette() {
    }

    /**
     * Returns the given color with replaced alpha channel.
     *
     * @param color source color, not null.
     * @param alpha alpha value in range 0 (transparent) to 255 (opaque).
     * @return new non-null color; not a {@link javax.swing.plaf.UIResource}.
     */
    public static Color alpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    /**
     * Linearly blends two opaque colors.
     *
     * @param from  color returned at {@code ratio == 0}, not null.
     * @param to    color returned at {@code ratio == 1}, not null.
     * @param ratio blend ratio, clamped to range 0..1.
     * @return new non-null opaque color.
     */
    public static Color mix(Color from, Color to, float ratio) {
        final float t = Math.max(0f, Math.min(1f, ratio));
        return new Color(
                Math.round(from.getRed() + (to.getRed() - from.getRed()) * t),
                Math.round(from.getGreen() + (to.getGreen() - from.getGreen()) * t),
                Math.round(from.getBlue() + (to.getBlue() - from.getBlue()) * t)
        );
    }
}

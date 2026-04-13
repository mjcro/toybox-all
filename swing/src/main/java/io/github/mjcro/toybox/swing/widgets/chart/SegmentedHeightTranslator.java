package io.github.mjcro.toybox.swing.widgets.chart;

import org.jspecify.annotations.NonNull;

/**
 * Translates a float value into a pixel height using segmented scaling.
 *
 * <p>Each segment defines a value range that maps to an equal share of
 * the total pixel height. This produces a non-linear scale that gives
 * lower value ranges more visual space relative to higher ones.
 */
public class SegmentedHeightTranslator {
    /** Actual height, in pixels. */
    private final int height;
    /** Segments definition. */
    private final float @NonNull [] segments;

    private final float segmentHeight;
    private final float maxValue;

    /**
     * Creates a new translator.
     *
     * @param height   the total pixel height to map values into
     * @param segments the segment boundaries in ascending order
     */
    public SegmentedHeightTranslator(int height, float @NonNull ... segments) {
        this.height = height;
        this.segments = segments;
        this.segmentHeight = 1.f * height / segments.length;
        this.maxValue = segments[segments.length - 1];
    }

    /**
     * Translates a value to a pixel height within the configured segments.
     *
     * @param value the value to translate
     * @return the corresponding pixel height (0 for non-positive values,
     *         at most {@code height - 1} for values at or above the maximum)
     */
    public int translate(float value) {
        if (value <= 0) {
            return 0;
        }
        if (value >= maxValue) {
            return height - 1;
        }

        float bound = 0;
        for (int i = 0; i < segments.length; i++) {
            if (value <= segments[i]) {
                return (int) ((i + (value / (segments[i] - bound))) * segmentHeight);
            }
            value -= segments[i];
            bound += segments[i];
        }
        return height - 1;
    }
}

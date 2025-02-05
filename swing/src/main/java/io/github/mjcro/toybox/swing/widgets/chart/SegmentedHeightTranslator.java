package io.github.mjcro.toybox.swing.widgets.chart;

public class SegmentedHeightTranslator {
    /**
     * Actual height, in pixels
     */
    private final int height;
    /**
     * Segments definition
     */
    private final float[] segments;

    private final float segmentHeight;
    private final float maxValue;

    public SegmentedHeightTranslator(int height, float... segments) {
        this.height = height;
        this.segments = segments;
        this.segmentHeight = 1.f * height / segments.length;
        this.maxValue = segments[segments.length - 1];
    }

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

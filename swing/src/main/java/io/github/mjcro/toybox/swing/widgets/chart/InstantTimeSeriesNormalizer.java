package io.github.mjcro.toybox.swing.widgets.chart;

import org.jspecify.annotations.NonNull;

import java.time.Duration;
import java.time.Instant;

/**
 * Normalizes a sorted time series of float values into a fixed number of
 * evenly-spaced time buckets using a configurable aggregation strategy.
 */
public class InstantTimeSeriesNormalizer {
    /** Aggregation strategy: count of values in each bucket. */
    public static final int COUNT = 1;
    /** Aggregation strategy: arithmetic mean of values in each bucket. */
    public static final int AVERAGE = 2;
    /** Aggregation strategy: minimum value in each bucket. */
    public static final int MIN = 3;
    /** Aggregation strategy: maximum value in each bucket. */
    public static final int MAX = 4;
    /** Aggregation strategy: sum of values in each bucket. */
    public static final int SUM = 5;

    private final int points;
    private final int strategy;

    /**
     * Creates a new normalizer.
     *
     * @param strategy the aggregation strategy (one of {@link #COUNT}, {@link #AVERAGE},
     *                 {@link #MIN}, {@link #MAX}, {@link #SUM})
     * @param points   the number of output buckets (must be at least 2)
     * @throws IllegalArgumentException if points is less than 2 or strategy is invalid
     */
    public InstantTimeSeriesNormalizer(int strategy, int points) {
        if (points < 2) {
            throw new IllegalArgumentException("At least 2 points expected, but requested " + points);
        }
        if (strategy < COUNT || strategy > SUM) {
            throw new IllegalArgumentException("Invalid strategy " + strategy);
        }

        this.strategy = strategy;
        this.points = points;
    }

    /**
     * Normalizes a sorted time series into the configured number of buckets.
     *
     * <p>Values and instants must be sorted by time in ascending order and
     * must have the same length. Each bucket spans an equal portion of
     * the {@code [from, to)} interval.
     *
     * @param values   the data values corresponding to each instant
     * @param instants the timestamps (sorted ascending)
     * @param from     the start of the time range (inclusive)
     * @param to       the end of the time range (exclusive)
     * @return an array of aggregated values with length equal to the configured points
     * @throws IllegalArgumentException if values and instants have different lengths
     */
    public float @NonNull [] normalizeSorted(float @NonNull [] values, @NonNull Instant @NonNull [] instants, @NonNull Instant from, @NonNull Instant to) {
        if (values.length != instants.length) {
            throw new IllegalArgumentException("Instants and values sizes differs");
        }
        final Duration duration = Duration.between(from, to);
        final Duration step = duration.dividedBy(points);

        final float[] output = new float[points];

        int x = 0;
        for (int i = 0; i < points; i++) {
            // Searching for points that fits within one step
            final Instant stepEndExclusive = from.plus(step.multipliedBy(i + 1));

            int y = -1;
            for (int j = x; j < instants.length; j++) {
                if (instants[j].compareTo(stepEndExclusive) >= 0) {
                    break;
                }
                y = j;
            }

            if (y == -1) {
                // No instants within segment
                output[i] = 0;
            } else {
                // Applying strategy
                if (y == x) {
                    // Single instant
                    if (strategy == COUNT) {
                        output[i] = 1;
                    } else {
                        output[i] = values[x];
                    }
                } else {
                    if (strategy == COUNT) {
                        output[i] = y - x + 1;
                    } else if (strategy == AVERAGE) {
                        float sum = 0;
                        for (int j = x; j <= y; j++) {
                            sum += values[j];
                        }
                        output[i] = sum / (y - x + 1);
                    } else if (strategy == MIN) {
                        float min = Float.MAX_VALUE;
                        for (int j = x; j <= y; j++) {
                            if (values[j] < min) {
                                min = values[j];
                            }
                        }
                        output[i] = min;
                    } else if (strategy == MAX) {
                        float max = Float.MIN_VALUE;
                        for (int j = x; j <= y; j++) {
                            if (values[j] > max) {
                                max = values[j];
                            }
                        }
                        output[i] = max;
                    } else if (strategy == SUM) {
                        float sum = 0;
                        for (int j = x; j <= y; j++) {
                            sum += values[j];
                        }
                        output[i] = sum;
                    }
                }
                x = y + 1;
            }
        }

        return output;
    }
}

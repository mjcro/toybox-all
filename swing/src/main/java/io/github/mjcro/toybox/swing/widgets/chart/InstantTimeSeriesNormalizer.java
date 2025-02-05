package io.github.mjcro.toybox.swing.widgets.chart;

import java.time.Duration;
import java.time.Instant;

public class InstantTimeSeriesNormalizer {
    public static final int
            COUNT = 1,
            AVERAGE = 2,
            MIN = 3,
            MAX = 4,
            SUM = 5;

    private final int points;
    private final int strategy;


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

    public float[] normalizeSorted(float[] values, Instant[] instants, Instant from, Instant to) {
        if (values.length != instants.length) {
            throw new IllegalArgumentException("Instants and values sizes differs");
        }
        Duration duration = Duration.between(from, to);
        Duration step = duration.dividedBy(points);

        float[] output = new float[points];

        int x = 0;
        for (int i = 0; i < points; i++) {
            // Searching for points that fits within one step
            Instant stepEndExclusive = from.plus(step.multipliedBy(i + 1));

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

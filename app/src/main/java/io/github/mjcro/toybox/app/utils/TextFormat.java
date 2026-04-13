package io.github.mjcro.toybox.app.utils;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.Locale;

/**
 * Utility class for formatting byte sizes and durations into human-readable strings.
 */
public class TextFormat {
    /**
     * Formats a byte count into a compact human-readable string (e.g., "128Kb", "4Mb").
     *
     * @param bytes the number of bytes
     * @return a formatted string representation
     */
    public static @NonNull String bytes(long bytes) {
        if (bytes < 1) {
            return "";
        } else if (bytes < 10_000) {
            return bytes + "b";
        } else if (bytes < 10_000_000) {
            return bytes / 1024 + "Kb";
        } else if (bytes < 10_000_000_000L) {
            return bytes / 1024 / 1024 + "Mb";
        }

        return bytes / 1024 / 1024 / 1024 + " Gb";
    }

    /**
     * Formats a duration into a compact human-readable string (e.g., "1.2ms", "3.4s").
     * Returns "0ns" for null, zero, or negative durations.
     *
     * @param duration the duration to format, or null
     * @return a formatted string representation
     */
    public static @NonNull String duration(@Nullable Duration duration) {
        if (duration == null || duration.isZero() || duration.isNegative()) {
            return "0ns";
        }

        long nanos = duration.toNanos();
        if (nanos < 800) {
            return nanos + "ns";
        } else if (nanos < 800_000) {
            return String.format(Locale.ROOT, "%.1fμs", nanos / 1e3);
        } else if (nanos < 800_000_000L) {
            return String.format(Locale.ROOT, "%.1fms", nanos / 1e6);
        }

        return String.format(Locale.ROOT, "%.1fs", nanos / 1e9);
    }

    private TextFormat() {
    }

    /**
     * Main method for standalone testing of formatting utilities.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(@NonNull String[] args) {
        long x = 2;
        while (x < 100_000_000_000L) {
            Duration d= Duration.ofNanos(x);
            System.out.println(d + " -> " + duration(d));
            x *= 2;
        }
    }
}

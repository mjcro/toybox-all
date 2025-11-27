package io.github.mjcro.toybox.app.utils;

import java.time.Duration;
import java.util.Locale;

public class TextFormat {
    public static String bytes(long bytes) {
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

    public static String duration(Duration duration) {
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

    public static void main(String[] args) {
        long x = 2;
        while (x < 100_000_000_000L) {
            Duration d= Duration.ofNanos(x);
            System.out.println(d + " -> " + duration(d));
            x *= 2;
        }
    }
}

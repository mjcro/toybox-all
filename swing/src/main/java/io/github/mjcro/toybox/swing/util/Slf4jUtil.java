package io.github.mjcro.toybox.swing.util;

import org.jspecify.annotations.NonNull;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * SLF4J utilities for ToyBox logging.
 */
public class Slf4jUtil {
    /**
     * Marker used for user-visible log messages within ToyBox.
     */
    public static final @NonNull Marker TOYBOX_MARKER = MarkerFactory.getMarker("toybox");

    private Slf4jUtil() {
    }
}

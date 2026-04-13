package io.github.mjcro.toybox.api.util;

import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * General-purpose utility methods for null/empty checks and array operations.
 *
 * <p>All methods in this class are stateless and safe for concurrent use.</p>
 */
public class Util {
    /**
     * Merges two arrays into a single array.
     *
     * <p>Returns {@code null} if both arrays are {@code null} or empty.
     * If only one is non-empty, that array is returned directly.</p>
     *
     * @param a   the first array, may be {@code null}
     * @param b   the second array, may be {@code null}
     * @param <T> the element type
     * @return the merged array, or {@code null} if both inputs are {@code null}/empty
     */
    public static <T> T @Nullable [] merge(T @Nullable [] a, T @Nullable [] b) {
        if ((a == null || a.length == 0) && (b == null || b.length == 0)) {
            return null;
        } else if (a == null || a.length == 0) {
            return b;
        } else if (b == null || b.length == 0) {
            return a;
        }

        T[] result = Arrays.copyOf(a, a.length + b.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    /**
     * Checks whether the given byte array is {@code null} or empty.
     *
     * @param b the byte array to check, may be {@code null}
     * @return {@code true} if {@code null} or zero-length
     */
    public static boolean isEmpty(byte @Nullable [] b) {
        return b == null || b.length == 0;
    }

    /**
     * Checks whether the given string is {@code null} or empty.
     *
     * @param s the string to check, may be {@code null}
     * @return {@code true} if {@code null} or empty
     */
    public static boolean isEmpty(@Nullable String s) {
        return s == null || s.isEmpty();
    }

    /**
     * Checks whether the given string is {@code null}, empty, or contains only whitespace.
     *
     * @param s the string to check, may be {@code null}
     * @return {@code true} if {@code null}, empty, or blank
     */
    public static boolean isBlank(@Nullable String s) {
        return s == null || s.isEmpty() || s.trim().isEmpty();
    }

    /**
     * Checks whether the given collection is {@code null} or empty.
     *
     * @param c the collection to check, may be {@code null}
     * @return {@code true} if {@code null} or empty
     */
    public static boolean isEmpty(@Nullable Collection<?> c) {
        return c == null || c.isEmpty();
    }

    /**
     * Checks whether the given map is {@code null} or empty.
     *
     * @param m the map to check, may be {@code null}
     * @return {@code true} if {@code null} or empty
     */
    public static boolean isEmpty(@Nullable Map<?, ?> m) {
        return m == null || m.isEmpty();
    }
}

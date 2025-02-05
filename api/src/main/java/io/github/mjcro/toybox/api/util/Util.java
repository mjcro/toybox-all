package io.github.mjcro.toybox.api.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class Util {
    public static <T> T[] merge(T[] a, T[] b) {
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

    public static boolean isEmpty(byte[] b) {
        return b == null || b.length == 0;
    }

    public static boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public static boolean isBlank(String s) {
        return isEmpty(s) || s.trim().isEmpty();
    }

    public static boolean isEmpty(Collection<?> c) {
        return c == null || c.isEmpty();
    }

    public static boolean isEmpty(Map<?, ?> m) {
        return m == null || m.isEmpty();
    }
}

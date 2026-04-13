package io.github.mjcro.toybox.app;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.PrintStream;

/**
 * Utility for converting exceptions into human-readable information.
 */
public class ExceptionConverter {
    /**
     * Returns a concise informational message for the given throwable.
     * Unwraps plain {@link RuntimeException} wrappers to find the meaningful cause.
     *
     * @param e the throwable to inspect, may be {@code null}
     * @return a non-empty message, or an empty string if {@code e} is {@code null}
     */
    public @NonNull String getInformation(@Nullable Throwable e) {
        if (e == null) {
            return "";
        }

        Throwable ex = findMeaningfulCause(e);

        return ex.getMessage() == null ? ex.getClass().getName() : ex.getMessage();
    }

    /**
     * Writes detailed exception information to the given print stream.
     * Does nothing if either argument is {@code null}.
     *
     * @param s the print stream to write to
     * @param e the throwable to describe
     */
    public void writeDetailedInformation(@Nullable PrintStream s, @Nullable Throwable e) {
        if (e == null || s == null) {
            return;
        }

        Throwable ex = findMeaningfulCause(e);

        s.println(ex.getMessage());
        s.println(ex.getClass());
        ex.printStackTrace(s);
    }

    /**
     * Recursively unwraps plain {@link RuntimeException} wrappers to find the
     * underlying meaningful cause.
     *
     * @param e the throwable to unwrap
     * @return the meaningful cause
     */
    private @NonNull Throwable findMeaningfulCause(@NonNull Throwable e) {
        if (e.getClass() == RuntimeException.class && e.getCause() != null && e.getCause() != e) {
            return findMeaningfulCause(e.getCause());
        }

        return e;
    }
}

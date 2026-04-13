package io.github.mjcro.toybox.api.events;

import io.github.mjcro.toybox.api.Event;
import io.github.mjcro.toybox.api.util.Util;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Event requesting the application window to display a hint message.
 *
 * <p>A blank or {@code null} hint value is normalized to an empty string,
 * effectively clearing any previously displayed hint.</p>
 */
public class SetWindowHintEvent implements Event {
    private final @NonNull String hint;

    /**
     * Creates a new event with the given hint text.
     *
     * @param hint the hint text to display, or {@code null}/blank to clear
     */
    public SetWindowHintEvent(@Nullable String hint) {
        this.hint = (hint == null || hint.trim().isEmpty()) ? "" : hint;
    }

    /**
     * Returns the hint text to display.
     *
     * @return the hint string, never {@code null}
     */
    public @NonNull String getHint() {
        return hint;
    }
}

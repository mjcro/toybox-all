package io.github.mjcro.toybox.api.events;

import io.github.mjcro.toybox.api.Event;
import io.github.mjcro.toybox.api.Label;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

/**
 * Event requesting the application window to update its label (title/icon).
 *
 * <p>If the label is {@code null}, the window should revert to its default label.</p>
 */
public class SetWindowLabelEvent implements Event {
    private final @Nullable Label label;

    /**
     * Creates a new event with the given label.
     *
     * @param label the label to set, or {@code null} to clear
     */
    public SetWindowLabelEvent(@Nullable Label label) {
        this.label = label;
    }

    /**
     * Returns the label to apply to the window, if present.
     *
     * @return an {@link Optional} containing the label, or empty if cleared
     */
    public @NonNull Optional<@NonNull Label> getLabel() {
        return Optional.ofNullable(label);
    }
}

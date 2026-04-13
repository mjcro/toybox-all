package io.github.mjcro.toybox.api.events;

import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Event;
import org.jspecify.annotations.NonNull;

/**
 * Listener interface for receiving ToyBox application events.
 *
 * <p>Implementations handle events dispatched through the
 * {@link io.github.mjcro.toybox.api.Environment} event system.</p>
 */
public interface EventListener {
    /**
     * Handles the given event within the provided context.
     *
     * @param context the current ToyBox context
     * @param event   the event to handle
     */
    void handleEvent(@NonNull Context context, @NonNull Event event);
}

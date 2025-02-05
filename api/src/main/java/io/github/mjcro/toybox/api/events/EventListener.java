package io.github.mjcro.toybox.api.events;

import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Event;

public interface EventListener {
    /**
     * Handles given event.
     *
     * @param context Current toybox context.
     * @param event   Event to handle.
     */
    void handleEvent(Context context, Event event);
}

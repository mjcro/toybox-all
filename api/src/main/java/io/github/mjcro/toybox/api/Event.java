package io.github.mjcro.toybox.api;

/**
 * Marker interface for events dispatched through the ToyBox event system.
 * <p>
 * Events are sent via {@link Context#sendEvent(Event)} and handled by
 * registered {@link io.github.mjcro.toybox.api.events.EventListener} instances.
 */
public interface Event {
}

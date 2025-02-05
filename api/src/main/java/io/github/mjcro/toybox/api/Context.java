package io.github.mjcro.toybox.api;

import io.github.mjcro.toybox.api.events.ShowToyEvent;

import java.util.Optional;

/**
 * Defines toy context - the holder for initial data, helpers and environment.
 * <p>
 * Every toy being displayed has own context, forked from parent context.
 */
public interface Context {
    /**
     * @return Environment associated with context.
     */
    Environment getEnvironment();

    /**
     * @return Initial data for toy.
     */
    Optional<Object> getInitialData();

    /**
     * Sends arbitrary event to context.
     * Events sent to context will be at first handled by {@link Context}
     * itself (if context can do that) and later dispatched to {@link Environment}.
     * <p>
     * In order hook custom interceptor it must be registered as listener
     * on {@link Environment} instance.
     *
     * @param event Event to send.
     */
    void sendEvent(Event event);

    /**
     * Shows given toy.
     *
     * @param toy  Toy to show.
     * @param data Initial data to pass.
     */
    default void show(AbstractToy toy, Object data) {
        sendEvent(new ShowToyEvent(toy, data));
    }
}

package io.github.mjcro.toybox.api;

import io.github.mjcro.toybox.api.events.ShowToyEvent;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

/**
 * Defines toy context - the holder for initial data, helpers and environment.
 * <p>
 * Every toy being displayed has own context, forked from parent context.
 */
public interface Context {
    /**
     * Returns the environment associated with this context.
     *
     * @return Environment associated with context.
     */
    @NonNull Environment getEnvironment();

    /**
     * Returns initial data passed to the toy, if any.
     *
     * @return Initial data for toy.
     */
    @NonNull Optional<@NonNull Object> getInitialData();

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
    void sendEvent(@NonNull Event event);

    /**
     * Shows given toy.
     *
     * @param toy  Toy to show.
     * @param data Initial data to pass, may be null.
     */
    default void show(@NonNull AbstractToy toy, @Nullable Object data) {
        sendEvent(new ShowToyEvent(toy, data));
    }

    /**
     * Searches for toy with given class and shows it.
     *
     * @param clazz          Toy class to show.
     * @param data           Initial data to pass, may be null.
     * @param throwIfMissing If set to true, {@link IllegalStateException} will be thrown if no toy
     *                       with given class is found.
     * @throws IllegalStateException If no toy with given class is found.
     */
    default void findAndShow(
            @NonNull Class<? extends @NonNull Toy> clazz,
            @Nullable Object data,
            boolean throwIfMissing
    ) {
        Optional<Toy> opt = getEnvironment().findRegisteredToy(clazz);
        if (!opt.isPresent()) {
            if (throwIfMissing) {
                throw new IllegalStateException("No toy with class " + clazz + " is registered");
            }
            return;
        }

        show(opt.get(), data);
    }

    /**
     * Searches for toy with given class name and shows it.
     *
     * @param name           Toy class name to show.
     * @param data           Initial data to pass, may be null.
     * @param throwIfMissing If set to true, {@link IllegalStateException} will be thrown if no toy
     *                       with given class is found.
     * @throws IllegalStateException If no toy with given class is found.
     */
    default void findAndShow(@NonNull String name, @Nullable Object data, boolean throwIfMissing) {
        Optional<Toy> opt = getEnvironment().findRegisteredToy(name);
        if (!opt.isPresent()) {
            if (throwIfMissing) {
                throw new IllegalStateException("No toy with name " + name + " is registered");
            }
            return;
        }

        show(opt.get(), data);
    }
}

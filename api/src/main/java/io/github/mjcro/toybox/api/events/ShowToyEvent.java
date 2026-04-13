package io.github.mjcro.toybox.api.events;

import io.github.mjcro.toybox.api.AbstractToy;
import io.github.mjcro.toybox.api.Event;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

/**
 * Event requesting the application to display a specific toy.
 *
 * <p>Optionally carries initial data that the toy can use during construction.</p>
 */
public class ShowToyEvent implements Event {
    private final @NonNull AbstractToy toy;
    private final @Nullable Object initialData;

    /**
     * Creates a new event to show the given toy with optional initial data.
     *
     * @param toy         the toy to display
     * @param initialData optional data to pass to the toy, may be {@code null}
     */
    public ShowToyEvent(@NonNull AbstractToy toy, @Nullable Object initialData) {
        this.toy = Objects.requireNonNull(toy, "toy");
        this.initialData = initialData;
    }

    /**
     * Creates a new event to show the given toy without initial data.
     *
     * @param toy the toy to display
     */
    public ShowToyEvent(@NonNull AbstractToy toy) {
        this(toy, null);
    }

    /**
     * Returns the toy to be displayed.
     *
     * @return the toy, never {@code null}
     */
    public @NonNull AbstractToy getToy() {
        return toy;
    }

    /**
     * Returns the initial data for the toy, if any.
     *
     * @return an {@link Optional} containing the initial data, or empty
     */
    public @NonNull Optional<@NonNull Object> getInitialData() {
        return Optional.ofNullable(initialData);
    }

    @Override
    public @NonNull String toString() {
        return "ShowToyEvent for " + toy.getClass().getSimpleName();
    }
}

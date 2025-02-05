package io.github.mjcro.toybox.api.events;

import io.github.mjcro.toybox.api.AbstractToy;
import io.github.mjcro.toybox.api.Event;

import java.util.Objects;
import java.util.Optional;

public class ShowToyEvent implements Event {
    private final AbstractToy toy;
    private final Object initialData;

    public ShowToyEvent(AbstractToy toy, Object initialData) {
        this.toy = Objects.requireNonNull(toy, "toy");
        this.initialData = initialData;
    }

    public ShowToyEvent(AbstractToy toy) {
        this(toy, null);
    }

    public AbstractToy getToy() {
        return toy;
    }

    public Optional<Object> getInitialData() {
        return Optional.ofNullable(initialData);
    }
}

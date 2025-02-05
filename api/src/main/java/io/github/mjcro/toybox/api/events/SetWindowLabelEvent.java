package io.github.mjcro.toybox.api.events;

import io.github.mjcro.toybox.api.Event;
import io.github.mjcro.toybox.api.Label;

import java.util.Optional;

public class SetWindowLabelEvent implements Event {
    private final Label label;

    public SetWindowLabelEvent(Label label) {
        this.label = label;
    }

    public Optional<Label> getLabel() {
        return Optional.ofNullable(label);
    }
}

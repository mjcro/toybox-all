package io.github.mjcro.toybox.api.events;

import io.github.mjcro.toybox.api.Event;
import io.github.mjcro.toybox.api.util.Util;

public class SetWindowHintEvent implements Event {
    private final String hint;

    public SetWindowHintEvent(String hint) {
        this.hint = Util.isBlank(hint) ? "" : hint;
    }

    public String getHint() {
        return hint;
    }
}

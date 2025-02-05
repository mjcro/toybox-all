package io.github.mjcro.toybox.api;

import java.util.Objects;
import java.util.Optional;

class TextMenu implements Menu {
    private final int order;
    private final String name;

    TextMenu(int order, String name) {
        this.order = order;
        this.name = Objects.requireNonNull(name);
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public Label getLabel() {
        return Label.ofName(name);
    }

    @Override
    public String toString() {
        return getName();
    }
}

package io.github.mjcro.toybox.toys;

import io.github.mjcro.interfaces.ints.WithOrder;
import io.github.mjcro.toybox.api.Label;

import java.util.Objects;
import java.util.Optional;

class OrderedLabel implements Label, WithOrder {
    private final int order;
    private final Label label;

    OrderedLabel(int order, Label label) {
        this.order = order;
        this.label = Objects.requireNonNull(label, "label");
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public Optional<String> getIconURI() {
        return label.getIconURI();
    }

    @Override
    public Optional<String> getStyle() {
        return label.getStyle();
    }

    @Override
    public String getName() {
        return label.getName();
    }
}

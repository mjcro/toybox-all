package io.github.mjcro.toybox.toys;

import io.github.mjcro.interfaces.ints.WithOrder;
import io.github.mjcro.toybox.api.Label;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.Optional;

/**
 * A label decorator that adds ordering support via {@link WithOrder}.
 */
class OrderedLabel implements Label, WithOrder {
    private final int order;
    private final @NonNull Label label;

    /**
     * Constructs an ordered label wrapping the given label with the given sort order.
     *
     * @param order the sort order value
     * @param label the label to decorate
     */
    OrderedLabel(int order, @NonNull Label label) {
        this.order = order;
        this.label = Objects.requireNonNull(label, "label");
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public @NonNull Optional<@NonNull String> getIconURI() {
        return label.getIconURI();
    }

    @Override
    public @NonNull Optional<@NonNull String> getStyle() {
        return label.getStyle();
    }

    @Override
    public @NonNull String getName() {
        return label.getName();
    }
}

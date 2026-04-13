package io.github.mjcro.toybox.api;

import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * {@link Menu} implementation that holds only a label and display order, without an action.
 */
public class LabelMenu implements Menu {
    private final int order;
    private final @NonNull Label label;

    /**
     * Creates a label menu with default order and the given name.
     *
     * @param name Display name.
     * @return Label menu instance.
     */
    static @NonNull LabelMenu ofName(@NonNull String name) {
        return new LabelMenu(0, Label.ofName(name));
    }

    /**
     * Creates a label menu with default order, the given icon and name.
     *
     * @param icon Icon URI.
     * @param name Display name.
     * @return Label menu instance.
     */
    static @NonNull LabelMenu ofIconAndName(@NonNull String icon, @NonNull String name) {
        return new LabelMenu(0, Label.ofIconAndName(icon, name));
    }

    /**
     * Constructs a new label menu.
     *
     * @param order Display order.
     * @param label Label to display.
     */
    public LabelMenu(int order, @NonNull Label label) {
        this.order = order;
        this.label = Objects.requireNonNull(label, "label");
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public @NonNull Label getLabel() {
        return label;
    }

    @Override
    public @NonNull String toString() {
        return getName();
    }
}

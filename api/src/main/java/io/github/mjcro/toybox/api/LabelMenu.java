package io.github.mjcro.toybox.api;

import java.util.Objects;

/**
 * Menu interface implementation without action.
 */
public class LabelMenu implements Menu {
    private final int order;
    private final Label label;

    static LabelMenu ofName(String name) {
        return new LabelMenu(0, Label.ofName(name));
    }

    static LabelMenu ofIconAndName(String icon, String name) {
        return new LabelMenu(0, Label.ofIconAndName(icon, name));
    }

    /**
     * Constructor.
     *
     * @param order Order.
     * @param label Label to display.
     */
    public LabelMenu(int order, Label label) {
        this.order = order;
        this.label = Objects.requireNonNull(label, "label");
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public Label getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return getName();
    }
}

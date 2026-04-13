package io.github.mjcro.toybox.api;

import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Menu item that wraps an {@link Action}, combining menu ordering with action execution.
 */
class ActionMenu implements Menu, Action {
    private final int order;
    private final @NonNull Action action;

    /**
     * Constructs a new action menu item.
     *
     * @param order  Display order of the menu item.
     * @param action Action to delegate to.
     */
    ActionMenu(int order, @NonNull Action action) {
        this.order = order;
        this.action = Objects.requireNonNull(action, "action");
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public @NonNull Label getLabel() {
        return action.getLabel();
    }

    @Override
    public void run() {
        action.run();
    }

    @Override
    public @NonNull String toString() {
        return getName();
    }
}

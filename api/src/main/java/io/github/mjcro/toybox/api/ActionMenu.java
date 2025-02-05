package io.github.mjcro.toybox.api;

import java.util.Objects;

class ActionMenu implements Menu, Action {
    private final int order;
    private final Action action;

    ActionMenu(int order, Action action) {
        this.order = order;
        this.action = Objects.requireNonNull(action, "action");
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public Label getLabel() {
        return action.getLabel();
    }

    @Override
    public void run() {
        action.run();
    }

    @Override
    public String toString() {
        return getName();
    }
}

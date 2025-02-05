package io.github.mjcro.toybox.api;

import java.util.Objects;

class ActionImpl implements Action {
    private final Label label;
    private final Runnable runnable;

    ActionImpl(Label label, Runnable runnable) {
        this.label = Objects.requireNonNull(label, "label");
        this.runnable = Objects.requireNonNull(runnable, "runnable");
    }

    @Override
    public Label getLabel() {
        return label;
    }

    @Override
    public void run() {
        runnable.run();
    }

    @Override
    public String toString() {
        return getLabel().getName();
    }
}

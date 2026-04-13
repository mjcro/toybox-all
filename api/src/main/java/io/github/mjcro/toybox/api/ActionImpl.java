package io.github.mjcro.toybox.api;

import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Default implementation of {@link Action} that delegates to a {@link Runnable}.
 */
class ActionImpl implements Action {
    private final @NonNull Label label;
    private final @NonNull Runnable runnable;

    /**
     * Constructs a new action with the given label and runnable.
     *
     * @param label    Label describing the action.
     * @param runnable Runnable to execute when the action is invoked.
     */
    ActionImpl(@NonNull Label label, @NonNull Runnable runnable) {
        this.label = Objects.requireNonNull(label, "label");
        this.runnable = Objects.requireNonNull(runnable, "runnable");
    }

    @Override
    public @NonNull Label getLabel() {
        return label;
    }

    @Override
    public void run() {
        runnable.run();
    }

    @Override
    public @NonNull String toString() {
        return getLabel().getName();
    }
}

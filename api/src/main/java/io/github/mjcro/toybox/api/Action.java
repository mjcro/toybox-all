package io.github.mjcro.toybox.api;

import org.jspecify.annotations.NonNull;

/**
 * Defines abstract action.
 * <p>
 * This action can be rendered as menu item, button or other component
 * depending on application needs and placement context.
 */
public interface Action extends Runnable, Labeled {
    /**
     * Constructs new action instance using given label and action.
     *
     * @param label  Action label.
     * @param action Action runnable.
     * @return Action.
     */
    static @NonNull Action of(@NonNull Label label, @NonNull Runnable action) {
        return new ActionImpl(label, action);
    }

    /**
     * Constructs new action instance using given name and action.
     * Constructed action will have no icon or other styling, just name.
     *
     * @param name   Action name.
     * @param action Action runnable.
     * @return Action.
     */
    static @NonNull Action ofName(@NonNull String name, @NonNull Runnable action) {
        return of(Label.ofName(name), action);
    }

    /**
     * Constructs new action instance using given name, style type and action.
     *
     * @param name   Action name.
     * @param style  Action style.
     * @param action Action runnable.
     * @return Action.
     */
    static @NonNull Action ofNameAndStyle(@NonNull String name, @NonNull String style, @NonNull Runnable action) {
        return of(Label.ofNameStyle(name, style), action);
    }
}

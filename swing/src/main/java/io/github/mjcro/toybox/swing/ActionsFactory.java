package io.github.mjcro.toybox.swing;

import io.github.mjcro.toybox.api.Action;
import io.github.mjcro.toybox.swing.hint.Hints;
import org.jspecify.annotations.NonNull;

/**
 * Factory for creating pre-styled {@link Action} instances.
 */
public class ActionsFactory {
    /**
     * Creates a primary-styled action.
     *
     * @param text display text for the action
     * @param run  runnable to execute when the action is triggered
     * @return primary-styled action
     */
    public static @NonNull Action primary(@NonNull String text, @NonNull Runnable run) {
        return Action.ofNameAndStyle(text, Hints.BUTTON_PRIMARY.getValue(), run);
    }

    /**
     * Creates a success-styled action.
     *
     * @param text display text for the action
     * @param run  runnable to execute when the action is triggered
     * @return success-styled action
     */
    public static @NonNull Action success(@NonNull String text, @NonNull Runnable run) {
        return Action.ofNameAndStyle(text, Hints.BUTTON_SUCCESS.getValue(), run);
    }
}

package io.github.mjcro.toybox.swing;

import io.github.mjcro.toybox.api.Action;

public class ActionsFactory {
    public static Action primary(String text, Runnable run) {
        return Action.ofNameAndStyle(text, Styles.BUTTON_PRIMARY.getValue(), run);
    }

    public static Action success(String text, Runnable run) {
        return Action.ofNameAndStyle(text, Styles.BUTTON_SUCCESS.getValue(), run);
    }
}

package io.github.mjcro.toybox.templates;

import java.awt.*;

/**
 * Defines components that provides binding from object field
 * to Swing component.
 */
public interface Binding {
    /**
     * @return Component to render.
     */
    Component getComponent();

    /**
     * Registers callback function to be invoked by this binding
     * when component it provides performs and action to submit
     * data (for example "Entry" key press in input field, or
     * mouse click on checkbox)
     *
     * @param callback Callback to run.
     */
    void setSubmitListener(Runnable callback);

    /**
     * Changes availability state of editable components in binding.
     *
     * @param enabled True to make enabled, false otherwise.
     */
    void setEnabled(boolean enabled);

    /**
     * Read current value from rendered component and then
     * applies it to object field this binding was originally
     * created from.
     *
     * @throws IllegalAccessException On reflection error.
     */
    void applyCurrentValue() throws IllegalAccessException;
}

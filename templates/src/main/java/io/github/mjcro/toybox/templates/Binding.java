package io.github.mjcro.toybox.templates;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.awt.Component;
import java.util.Optional;

/**
 * Defines a binding from an object field to a Swing component,
 * supporting value submission, enable/disable, and group membership.
 */
public interface Binding {
    /**
     * Returns the Swing component rendered by this binding.
     *
     * @return the component to render
     */
    @NonNull Component getComponent();

    /**
     * Returns the optional group name this binding belongs to.
     *
     * @return the binding group, or empty if ungrouped
     */
    @NonNull Optional<@NonNull String> getGroup();

    /**
     * Registers a callback to be invoked when this binding's component
     * performs a submit action (for example an Enter key press in an
     * input field or a mouse click on a checkbox).
     *
     * @param callback the callback to run, or {@code null} to clear
     */
    void setSubmitListener(@Nullable Runnable callback);

    /**
     * Changes the enabled state of editable components in this binding.
     *
     * @param enabled {@code true} to enable, {@code false} to disable
     */
    void setEnabled(boolean enabled);

    /**
     * Reads the current value from the rendered component and applies
     * it to the object field this binding was originally created from.
     *
     * @throws IllegalAccessException on reflection error
     */
    void applyCurrentValue() throws IllegalAccessException;
}

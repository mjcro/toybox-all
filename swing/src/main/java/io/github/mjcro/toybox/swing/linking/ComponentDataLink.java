package io.github.mjcro.toybox.swing.linking;

import io.github.mjcro.interfaces.Decorator;
import org.jspecify.annotations.NonNull;

import java.awt.Component;

/**
 * A {@link DataLink} bound to a specific AWT {@link Component},
 * providing enable/disable control and decorator access.
 *
 * @param <C> component type
 * @param <V> value type
 */
public interface ComponentDataLink<C extends Component, V> extends DataLink<V>, Decorator<C> {
    /**
     * Returns the bound component.
     *
     * @return the component
     */
    @NonNull C getComponent();

    /**
     * Submits the current value, typically invoking a registered callback.
     */
    void submit();

    /**
     * Sets the enabled state of the bound component.
     *
     * @param enabled whether the component should be enabled
     */
    default void setEnabled(boolean enabled) {
        getComponent().setEnabled(enabled);
    }

    @Override
    default @NonNull C getDecorated() {
        return getComponent();
    }
}

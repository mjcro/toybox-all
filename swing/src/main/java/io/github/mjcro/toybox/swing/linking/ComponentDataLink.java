package io.github.mjcro.toybox.swing.linking;

import io.github.mjcro.interfaces.Decorator;

import java.awt.*;

public interface ComponentDataLink<C extends Component, V> extends DataLink<V>, Decorator<C> {
    C getComponent();

    void submit();

    default void setEnabled(boolean enabled) {
        getComponent().setEnabled(enabled);
    }

    @Override
    default C getDecorated() {
        return getComponent();
    }
}

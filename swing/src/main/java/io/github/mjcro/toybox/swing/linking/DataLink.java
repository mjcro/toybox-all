package io.github.mjcro.toybox.swing.linking;

import java.util.Optional;

public interface DataLink<V> {
    void setValue(V value);

    Optional<V> getValue();
}

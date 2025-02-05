package io.github.mjcro.toybox.api;

import io.github.mjcro.interfaces.strings.WithName;

/**
 * Defines entities with label.
 */
public interface Labeled extends WithName {
    /**
     * @return Label.
     */
    Label getLabel();

    @Override
    default String getName() { return getLabel().getName(); }
}

package io.github.mjcro.toybox.api;

import io.github.mjcro.interfaces.strings.WithName;
import org.jspecify.annotations.NonNull;

/**
 * Defines entities that have a displayable {@link Label}.
 */
public interface Labeled extends WithName {
    /**
     * Returns the label associated with this entity.
     *
     * @return Label.
     */
    @NonNull Label getLabel();

    /**
     * Returns the display name derived from the label.
     *
     * @return Display name.
     */
    @Override
    default @NonNull String getName() {
        return getLabel().getName();
    }
}

package io.github.mjcro.toybox.api;

import io.github.mjcro.interfaces.strings.WithName;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

/**
 * Defines a display label that can be rendered as a label component, button,
 * menu item, window title, or other UI element.
 */
public interface Label extends WithName, Labeled {
    /**
     * Constructs label containing no additional information except name.
     *
     * @param name Name to display.
     * @return Label instance.
     */
    static @NonNull Label ofName(@NonNull String name) {
        return new LabelImpl(name, null, null);
    }

    /**
     * Constructs label with icon URI and name.
     *
     * @param icon Icon URI.
     * @param name Name to display.
     * @return Label instance.
     */
    static @NonNull Label ofIconAndName(@NonNull String icon, @NonNull String name) {
        return new LabelImpl(name, icon, null);
    }

    /**
     * Constructs label with name and style.
     *
     * @param name  Name to display.
     * @param style Style of label.
     * @return Label instance.
     */
    static @NonNull Label ofNameStyle(@NonNull String name, @NonNull String style) {
        return new LabelImpl(name, null, style);
    }

    /**
     * Constructs label with icon, name, and style.
     *
     * @param icon  Icon URI.
     * @param name  Name to display.
     * @param style Style of label.
     * @return Label instance.
     */
    static @NonNull Label ofIconNameStyle(@NonNull String icon, @NonNull String name, @NonNull String style) {
        return new LabelImpl(name, icon, style);
    }

    /**
     * Returns the icon URI for this label, if one has been set.
     *
     * @return Label icon URI, optional.
     */
    @NonNull Optional<@NonNull String> getIconURI();

    /**
     * Returns the style identifier for this label, if one has been set.
     *
     * @return Label style, optional.
     */
    @NonNull Optional<@NonNull String> getStyle();

    /**
     * Returns this label instance.
     *
     * @return This label.
     */
    @Override
    default @NonNull Label getLabel() {
        return this;
    }
}

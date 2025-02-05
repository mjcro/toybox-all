package io.github.mjcro.toybox.api;

import io.github.mjcro.interfaces.strings.WithName;

import java.util.Optional;

/**
 * Defines some label, that can be later rendered as:
 * - Label
 * - Button
 * - Menu
 * - Window title
 * - etc...
 */
public interface Label extends WithName, Labeled {
    /**
     * Constructs label containing no additional information except name.
     *
     * @param name Name to display.
     * @return Label instance.
     */
    static Label ofName(String name) {
        return new LabelImpl(name, null, null);
    }

    /**
     * Constructs label with icon URI and name.
     *
     * @param icon Icon URI.
     * @param name Name to display.
     * @return Label instance.
     */
    static Label ofIconAndName(String icon, String name) {
        return new LabelImpl(name, icon, null);
    }

    /**
     * Constructs label with name and style.
     *
     * @param name  Name to display.
     * @param style Style of label.
     * @return Label instance.
     */
    static Label ofNameStyle(String name, String style) {
        return new LabelImpl(name, null, style);
    }

    /**
     * Construct label with icon name and style.
     *
     * @param icon  Icon URI.
     * @param name  Name to display.
     * @param style Style of label.
     * @return Label instance.
     */
    static Label ofIconNameStyle(String icon, String name, String style) {
        return new LabelImpl(name, icon, style);
    }

    /**
     * @return Label icon URI, optional.
     */
    Optional<String> getIconURI();

    /**
     * @return Label style, optional.
     */
    Optional<String> getStyle();

    @Override
    default Label getLabel() {
        return this;
    }
}

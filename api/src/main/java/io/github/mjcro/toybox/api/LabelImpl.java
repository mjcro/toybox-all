package io.github.mjcro.toybox.api;

import java.util.Objects;
import java.util.Optional;

class LabelImpl implements Label {
    private final String name;
    private final String iconURI;
    private final String style;

    LabelImpl(String name, String iconURI, String style) {
        this.name = Objects.requireNonNull(name, "name");
        this.iconURI = iconURI;
        this.style = style;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Optional<String> getIconURI() {
        return Optional.ofNullable(iconURI);
    }

    @Override
    public Optional<String> getStyle() {
        return Optional.ofNullable(style);
    }

    @Override
    public String toString() {
        return getName();
    }
}

package io.github.mjcro.toybox.api;

import io.github.mjcro.interfaces.strings.WithUri;

import java.util.Objects;

class SystemMenu implements Menu, WithUri {
    private final int order;
    private final String uri;
    private final String name;
    private final String iconURI;

    SystemMenu(int order, String uri, String name, String iconURI) {
        this.order = order;
        this.uri = Objects.requireNonNull(uri, "uri");
        this.name = Objects.requireNonNull(name, "name");
        this.iconURI = iconURI;
    }

    @Override
    public Label getLabel() {
        return Label.ofIconAndName(iconURI, name);
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public String getURI() {
        return uri;
    }

    @Override
    public String toString() {
        return getName();
    }
}

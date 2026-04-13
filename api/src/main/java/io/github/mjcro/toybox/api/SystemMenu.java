package io.github.mjcro.toybox.api;

import io.github.mjcro.interfaces.strings.WithUri;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Package-private implementation of {@link Menu} representing a built-in system menu entry.
 *
 * <p>Instances are immutable and identified by their URI.</p>
 */
class SystemMenu implements Menu, WithUri {
    private final int order;
    private final @NonNull String uri;
    private final @NonNull String name;
    private final @Nullable String iconURI;

    /**
     * Constructs a new system menu entry.
     *
     * @param order   the display order of this menu
     * @param uri     the unique URI identifying this menu
     * @param name    the human-readable name
     * @param iconURI the optional icon URI, may be {@code null}
     */
    SystemMenu(int order, @NonNull String uri, @NonNull String name, @Nullable String iconURI) {
        this.order = order;
        this.uri = Objects.requireNonNull(uri, "uri");
        this.name = Objects.requireNonNull(name, "name");
        this.iconURI = iconURI;
    }

    @Override
    public @NonNull Label getLabel() {
        return iconURI == null ? Label.ofName(name) : Label.ofIconAndName(iconURI, name);
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public @NonNull String getURI() {
        return uri;
    }

    @Override
    public @NonNull String toString() {
        return getName();
    }
}

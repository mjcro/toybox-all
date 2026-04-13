package io.github.mjcro.toybox.api;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

/**
 * Default immutable implementation of {@link Label}.
 */
class LabelImpl implements Label {
    private final @NonNull String name;
    private final @Nullable String iconURI;
    private final @Nullable String style;

    /**
     * Constructs a new label.
     *
     * @param name    Display name, must not be null.
     * @param iconURI Icon URI, may be null.
     * @param style   Style identifier, may be null.
     */
    LabelImpl(@NonNull String name, @Nullable String iconURI, @Nullable String style) {
        this.name = Objects.requireNonNull(name, "name");
        this.iconURI = iconURI;
        this.style = style;
    }

    @Override
    public @NonNull String getName() {
        return name;
    }

    @Override
    public @NonNull Optional<@NonNull String> getIconURI() {
        return Optional.ofNullable(iconURI);
    }

    @Override
    public @NonNull Optional<@NonNull String> getStyle() {
        return Optional.ofNullable(style);
    }

    @Override
    public @NonNull String toString() {
        return getName();
    }
}

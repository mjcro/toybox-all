package io.github.mjcro.toybox.swing;

import io.github.mjcro.interfaces.Decorator;
import io.github.mjcro.interfaces.enums.WithType;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * A decorator that associates an enum type with a wrapped value.
 *
 * @param <T> enum type
 * @param <V> decorated value type
 */
public class TypedDecorator<T extends Enum<T>, V> implements Decorator<V>, WithType<T> {
    private final @NonNull T type;
    private final @NonNull V decorated;

    /**
     * Creates a new typed decorator.
     *
     * @param type      enum type, must not be {@code null}
     * @param decorated value to decorate, must not be {@code null}
     */
    public TypedDecorator(@NonNull T type, @NonNull V decorated) {
        this.type = Objects.requireNonNull(type);
        this.decorated = Objects.requireNonNull(decorated);
    }

    @Override
    public @NonNull T getType() {
        return type;
    }

    @Override
    public @NonNull V getDecorated() {
        return decorated;
    }

    @Override
    public @NonNull String toString() {
        return decorated.toString();
    }
}

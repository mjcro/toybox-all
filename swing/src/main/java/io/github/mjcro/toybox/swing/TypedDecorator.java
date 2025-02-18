package io.github.mjcro.toybox.swing;

import io.github.mjcro.interfaces.Decorator;
import io.github.mjcro.interfaces.enums.WithType;

import java.util.Objects;

public class TypedDecorator<T extends Enum<T>, V> implements Decorator<V>, WithType<T> {
    private final T type;
    private final V decorated;

    public TypedDecorator(T type, V decorated) {
        this.type = Objects.requireNonNull(type);
        this.decorated = decorated;
    }

    @Override
    public T getType() {
        return type;
    }

    @Override
    public V getDecorated() {
        return decorated;
    }

    @Override
    public String toString() {
        return decorated == null ? null : decorated.toString();
    }
}

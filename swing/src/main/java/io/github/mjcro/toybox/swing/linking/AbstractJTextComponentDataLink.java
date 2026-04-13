package io.github.mjcro.toybox.swing.linking;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.text.JTextComponent;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Base class for {@link ComponentDataLink} implementations backed by a
 * {@link JTextComponent}. Subclasses provide the conversion between
 * string and the value type.
 *
 * @param <C> text component type
 * @param <V> value type
 */
public abstract class AbstractJTextComponentDataLink<C extends JTextComponent, V> implements ComponentDataLink<C, V> {
    private final @NonNull C component;
    private final @Nullable Consumer<@NonNull Optional<@Nullable V>> onSubmit;

    /**
     * Creates a new data link for the given text component.
     *
     * @param component text component, must not be {@code null}
     * @param onSubmit  callback invoked on submit, may be {@code null}
     */
    public AbstractJTextComponentDataLink(@NonNull C component, @Nullable Consumer<@NonNull Optional<@Nullable V>> onSubmit) {
        this.component = Objects.requireNonNull(component, "component");
        this.onSubmit = onSubmit;
    }

    /**
     * Converts a value to its string representation.
     *
     * @param value value to convert, may be {@code null}
     * @return string representation, or {@code null}
     */
    protected @Nullable String valueToString(@Nullable V value) {
        return value == null ? null : value.toString();
    }

    /**
     * Converts a string to the value type.
     *
     * @param s string to convert
     * @return converted value
     */
    protected abstract @Nullable V stringToValue(@NonNull String s);

    @Override
    public final @NonNull C getComponent() {
        return component;
    }

    @Override
    public final void setValue(@Nullable V value) {
        getComponent().setText(valueToString(value));
    }

    @Override
    public final @NonNull Optional<@Nullable V> getValue() {
        return Optional.ofNullable(component.getText()).map(this::stringToValue);
    }

    @Override
    public void submit() {
        if (onSubmit != null) {
            onSubmit.accept(getValue());
        }
    }
}

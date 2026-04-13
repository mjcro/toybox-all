package io.github.mjcro.toybox.swing.linking;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.text.JTextComponent;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * Data link binding a {@link JTextComponent} to a {@link String} value,
 * with an optional transformation applied on read.
 *
 * @param <C> The text component type.
 */
public class JTextComponentStringDataLink<C extends JTextComponent> extends AbstractJTextComponentDataLink<C, String> {
    private final @Nullable UnaryOperator<@NonNull String> transform;

    /**
     * Creates a new string data link.
     *
     * @param component The text component to bind.
     * @param transform Optional transformation applied when reading the string value.
     * @param onSubmit  Optional callback invoked on submit with the current value.
     */
    public JTextComponentStringDataLink(
            @NonNull C component,
            @Nullable UnaryOperator<@NonNull String> transform,
            @Nullable Consumer<@NonNull Optional<@NonNull String>> onSubmit
    ) {
        super(component, onSubmit);
        this.transform = transform;
    }

    @Override
    protected @Nullable String stringToValue(@Nullable String s) {
        return transform != null && s != null ? transform.apply(s) : s;
    }
}

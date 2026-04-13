package io.github.mjcro.toybox.swing.linking;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.text.JTextComponent;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Data link binding a {@link JTextComponent} to a numeric value,
 * supporting various {@link Number} subtypes including primitives and boxed types.
 *
 * @param <C> The text component type.
 * @param <V> The number type.
 */
public class JTextComponentNumberDataLink<C extends JTextComponent, V extends Number> extends AbstractJTextComponentDataLink<C, V> {
    private final @NonNull Class<@NonNull V> clazz;

    /**
     * Creates a new numeric data link.
     *
     * @param component The text component to bind.
     * @param clazz     The target number class.
     * @param onSubmit  Optional callback invoked on submit with the current value.
     */
    public JTextComponentNumberDataLink(
            @NonNull C component,
            @NonNull Class<@NonNull V> clazz,
            @Nullable Consumer<@NonNull Optional<@NonNull V>> onSubmit
    ) {
        super(component, onSubmit);
        this.clazz = Objects.requireNonNull(clazz, "clazz");
    }

    @Override
    protected @Nullable V stringToValue(@Nullable String s) {
        if (s != null) {
            s = s.trim()
                    .replaceAll(",", ".")
                    .replaceAll("[ ']", "");
        }
        //noinspection unchecked
        return (V) (clazz.isPrimitive() ? stringToPrimitives(s) : stringToBoxed(s));
    }

    /**
     * Converts a string to a primitive-compatible value, defaulting to zero for null or empty input.
     *
     * @param s The string to convert.
     * @return The parsed object value.
     */
    private @NonNull Object stringToPrimitives(@Nullable String s) {
        return Objects.requireNonNull(stringToBoxed(s == null || s.isEmpty() ? "0" : s));
    }

    /**
     * Converts a string to the appropriate boxed number type.
     *
     * @param s The string to convert.
     * @return The parsed number, or null if input is null or empty.
     */
    private @Nullable Object stringToBoxed(@Nullable String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }

        // Integers
        if (clazz == long.class || clazz == Long.class) {
            return new BigInteger(s).longValue();
        } else if (clazz == int.class || clazz == Integer.class) {
            return new BigInteger(s).intValue();
        } else if (clazz == short.class || clazz == Short.class) {
            return new BigInteger(s).shortValue();
        } else if (clazz == byte.class || clazz == Byte.class) {
            return new BigInteger(s).byteValue();
        } else if (clazz == BigInteger.class) {
            return new BigInteger(s);
        }

        // Floats
        if (clazz == float.class || clazz == Float.class) {
            return new BigDecimal(s).floatValue();
        } else if (clazz == double.class || clazz == Double.class) {
            return new BigDecimal(s).doubleValue();
        }

        // Default
        return new BigDecimal(s);
    }
}

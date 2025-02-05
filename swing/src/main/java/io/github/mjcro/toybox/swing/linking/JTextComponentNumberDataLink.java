package io.github.mjcro.toybox.swing.linking;

import javax.swing.text.JTextComponent;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public class JTextComponentNumberDataLink<C extends JTextComponent, V extends Number> extends AbstractJTextComponentDataLink<C, V> {
    private final Class<V> clazz;

    public JTextComponentNumberDataLink(C component, Class<V> clazz, Consumer<Optional<V>> onSubmit) {
        super(component, onSubmit);
        this.clazz = Objects.requireNonNull(clazz, "clazz");
    }

    @Override
    protected V stringToValue(String s) {
        if (s != null) {
            s = s.trim()
                    .replaceAll(",", ".")
                    .replaceAll("[ ']", "");
        }
        //noinspection unchecked
        return (V) (clazz.isPrimitive() ? stringToPrimitives(s) : stringToBoxed(s));
    }

    private Object stringToPrimitives(String s) {
        return stringToBoxed(s == null || s.isEmpty() ? "0" : s);
    }

    private Object stringToBoxed(String s) {
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

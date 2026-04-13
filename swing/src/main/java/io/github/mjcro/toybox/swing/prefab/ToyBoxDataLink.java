package io.github.mjcro.toybox.swing.prefab;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DateTimePicker;
import io.github.mjcro.toybox.swing.hint.Hint;
import io.github.mjcro.toybox.swing.linking.ComponentDataLink;
import io.github.mjcro.toybox.swing.linking.DatePickerLocalDateDataLink;
import io.github.mjcro.toybox.swing.linking.DateTimePickerLocalDateTimeDataLink;
import io.github.mjcro.toybox.swing.linking.JCheckBoxBooleanDataLink;
import io.github.mjcro.toybox.swing.linking.JTextComponentNumberDataLink;
import io.github.mjcro.toybox.swing.linking.JTextComponentStringDataLink;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.JCheckBox;
import javax.swing.text.JTextComponent;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.UnaryOperator;

/**
 * Factory methods for creating {@link ComponentDataLink} instances
 * that bind Swing components to object fields or standalone values.
 */
public class ToyBoxDataLink {
    /**
     * Creates a data link binding a {@link JCheckBox} to a boolean field via reflection.
     *
     * @param component The check box component.
     * @param f         The target boolean field.
     * @param target    The object owning the field.
     * @param hints     Hints to apply to the component.
     * @return A new data link.
     */
    @SafeVarargs
    public static @NonNull ComponentDataLink<@NonNull JCheckBox, @NonNull Boolean> linkBooleanField(
            @NonNull JCheckBox component,
            @NonNull Field f,
            @NonNull Object target,
            @NonNull Hint<? super JCheckBox>... hints
    ) {
        f.setAccessible(true);
        final JCheckBoxBooleanDataLink link = new JCheckBoxBooleanDataLink(component, b -> {
            try {
                if (f.getType().isPrimitive()) {
                    f.setBoolean(target, b.orElse(false));
                } else {
                    f.set(target, b.orElse(null));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });

        Hint.applyAll(component, hints);
        try {
            Optional.ofNullable(f.get(target))
                    .map($ -> (Boolean) $)
                    .ifPresent(link::setValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return link;
    }

    /**
     * Creates a data link binding a text component to a string value.
     *
     * @param component The text component.
     * @param initial   Optional initial string value.
     * @param transform Optional transformation applied when reading.
     * @param hints     Hints to apply to the component.
     * @param <C>       The text component type.
     * @return A new data link.
     */
    @SafeVarargs
    public static <C extends JTextComponent> @NonNull ComponentDataLink<@NonNull C, @NonNull String> linkString(
            @NonNull C component,
            @Nullable String initial,
            @Nullable UnaryOperator<@NonNull String> transform,
            @NonNull Hint<? super C>... hints
    ) {
        final JTextComponentStringDataLink<C> link = new JTextComponentStringDataLink<>(component, transform, null);
        Hint.applyAll(component, hints);
        Optional.ofNullable(initial).ifPresent(link::setValue);
        return link;
    }

    /**
     * Creates a data link binding a {@link DatePicker} to a {@link LocalDate} field via reflection.
     *
     * @param component The date picker component.
     * @param f         The target field.
     * @param target    The object owning the field.
     * @param hints     Hints to apply to the component.
     * @return A new data link.
     */
    @SafeVarargs
    public static @NonNull ComponentDataLink<@NonNull DatePicker, @NonNull LocalDate> linkLocalDateField(
            @NonNull DatePicker component,
            @NonNull Field f,
            @NonNull Object target,
            @NonNull Hint<? super DatePicker>... hints
    ) {
        f.setAccessible(true);
        final DatePickerLocalDateDataLink link = new DatePickerLocalDateDataLink(component, ld -> {
            try {
                f.set(target, ld.orElse(null));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
        Hint.applyAll(component, hints);
        try {
            Optional.ofNullable(f.get(target)).map($ -> (LocalDate) $).ifPresent(link::setValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return link;
    }

    /**
     * Creates a data link binding a {@link DateTimePicker} to a {@link LocalDateTime} field via reflection.
     *
     * @param component The date-time picker component.
     * @param f         The target field.
     * @param target    The object owning the field.
     * @param hints     Hints to apply to the component.
     * @return A new data link.
     */
    @SafeVarargs
    public static @NonNull ComponentDataLink<@NonNull DateTimePicker, @NonNull LocalDateTime> linkLocalDateTimeField(
            @NonNull DateTimePicker component,
            @NonNull Field f,
            @NonNull Object target,
            @NonNull Hint<? super DateTimePicker>... hints
    ) {
        f.setAccessible(true);
        final DateTimePickerLocalDateTimeDataLink link = new DateTimePickerLocalDateTimeDataLink(component, ld -> {
            try {
                f.set(target, ld.orElse(null));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
        Hint.applyAll(component, hints);
        try {
            Optional.ofNullable(f.get(target)).map($ -> (LocalDateTime) $).ifPresent(link::setValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return link;
    }

    /**
     * Creates a data link binding a text component to a string field via reflection.
     *
     * @param component The text component.
     * @param f         The target string field.
     * @param target    The object owning the field.
     * @param transform Optional transformation applied when reading.
     * @param hints     Hints to apply to the component.
     * @param <C>       The text component type.
     * @return A new data link.
     */
    @SafeVarargs
    public static <C extends JTextComponent> @NonNull ComponentDataLink<@NonNull C, @NonNull String> linkStringField(
            @NonNull C component,
            @NonNull Field f,
            @NonNull Object target,
            @Nullable UnaryOperator<@NonNull String> transform,
            @NonNull Hint<? super C>... hints
    ) {
        f.setAccessible(true);
        final JTextComponentStringDataLink<C> link = new JTextComponentStringDataLink<>(component, transform, s -> {
            try {
                f.set(target, s.orElse(null));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });

        Hint.applyAll(component, hints);

        try {
            Optional.ofNullable(f.get(target)).map(Object::toString).ifPresent(link::setValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return link;
    }

    /**
     * Creates a data link binding a text component to a numeric field via reflection.
     *
     * @param component The text component.
     * @param f         The target numeric field.
     * @param target    The object owning the field.
     * @param hints     Hints to apply to the component.
     * @param <C>       The text component type.
     * @param <V>       The number type.
     * @return A new data link.
     */
    @SafeVarargs
    public static <C extends JTextComponent, V extends Number> @NonNull ComponentDataLink<@NonNull C, @NonNull V> linkNumberField(
            @NonNull C component,
            @NonNull Field f,
            @NonNull Object target,
            @NonNull Hint<? super C>... hints
    ) {
        f.setAccessible(true);
        @SuppressWarnings("unchecked") final Class<V> clazz = (Class<V>) f.getType();
        final JTextComponentNumberDataLink<C, V> link = new JTextComponentNumberDataLink<>(component, clazz, v -> {
            try {
                if (v.isEmpty() && clazz.isPrimitive()) {
                    f.set(target, 0);
                } else {
                    f.set(target, v.orElse(null));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });

        Hint.applyAll(component, hints);

        try {
            Optional.ofNullable(f.get(target)).map($ -> (V) $).ifPresent(link::setValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return link;
    }

    private ToyBoxDataLink() {
    }
}

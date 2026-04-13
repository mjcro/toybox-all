package io.github.mjcro.toybox.templates.bindings;

import io.github.mjcro.interfaces.strings.WithName;
import io.github.mjcro.interfaces.tuples.OptionalPair;
import io.github.mjcro.toybox.api.Label;
import io.github.mjcro.toybox.swing.hint.Hints;
import io.github.mjcro.toybox.swing.prefab.ToyBoxLabels;
import io.github.mjcro.toybox.templates.EnumerationValue;
import org.jspecify.annotations.NonNull;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Vector;
import java.util.function.Function;

/**
 * Binding that presents enumerated values in a combo box.
 *
 * @param <T> the value type
 */
public class EnumerationBinding<T> extends AbstractJPanelContainerBinding {

    private final @NonNull List<@NonNull OptionalPair<@NonNull T, @NonNull Label>> values;
    private @NonNull JComboBox<@NonNull OptionalPair<@NonNull T, @NonNull Label>> comboBox;

    /**
     * Creates an enumeration binding from an enum class using the default name mapper.
     *
     * @param target the object containing the field
     * @param field  the annotated field
     * @param clazz  the enum class
     * @param <T>    the enum type
     * @return a new enumeration binding
     */
    public static <T extends Enum<T>> @NonNull EnumerationBinding<@NonNull T> ofEnum(
            @NonNull Object target,
            @NonNull Field field,
            @NonNull Class<@NonNull T> clazz
    ) {
        return ofEnum(target, field, clazz, t -> t instanceof WithName ? ((WithName) t).getName() : t.name());
    }

    /**
     * Creates an enumeration binding from an enum class with a custom name mapper.
     *
     * @param target the object containing the field
     * @param field  the annotated field
     * @param clazz  the enum class
     * @param mapper function to extract display name from enum value
     * @param <T>    the enum type
     * @return a new enumeration binding
     */
    public static <T extends Enum<T>> @NonNull EnumerationBinding<@NonNull T> ofEnum(
            @NonNull Object target,
            @NonNull Field field,
            @NonNull Class<@NonNull T> clazz,
            @NonNull Function<@NonNull T, @NonNull String> mapper
    ) {
        ArrayList<OptionalPair<T, Label>> values = new ArrayList<>();
        for (T t : clazz.getEnumConstants()) {
            values.add(new EnumerationValue<>(t, mapper.apply(t)));
        }
        return new EnumerationBinding<>(target, field, values);
    }

    /**
     * Creates a new enumeration binding.
     *
     * @param target the object containing the field
     * @param field  the annotated field
     * @param values the enumerated value/label pairs
     */
    public EnumerationBinding(
            @NonNull Object target,
            @NonNull Field field,
            @NonNull Iterable<@NonNull OptionalPair<@NonNull T, @NonNull Label>> values
    ) {
        super(target, field);
        Objects.requireNonNull(values, "values");
        this.values = new ArrayList<>();
        for (OptionalPair<T, Label> value : values) {
            this.values.add(value);
        }
        initComponents();
    }

    private void initComponents() {
        JLabel label = ToyBoxLabels.create(getLabelName());
        Hints.PADDING_NORMAL.apply(label);

        comboBox = new JComboBox<>(new Vector<>(values));
        comboBox.setEditable(false);
        comboBox.addActionListener(e -> fireSubmit());

        super.add(label, java.awt.BorderLayout.LINE_START);
        super.add(comboBox, java.awt.BorderLayout.CENTER);

        try {
            Object currentValue = field.get(target);
            for (final OptionalPair<T, Label> v : values) {
                if (v.getFirst().isPresent() && v.getFirst().get().equals(currentValue)) {
                    comboBox.setSelectedItem(v);
                    break;
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void applyCurrentValue() throws IllegalAccessException {
        field.set(target, ((EnumerationValue<T>) comboBox.getSelectedItem()).getKey().orElse(null));
    }

    @Override
    public void setEnabled(boolean enabled) {
        comboBox.setEnabled(enabled);
    }
}

package io.github.mjcro.toybox.templates.bindings;

import io.github.mjcro.interfaces.strings.WithName;
import io.github.mjcro.interfaces.tuples.OptionalPair;
import io.github.mjcro.toybox.api.Label;
import io.github.mjcro.toybox.swing.hint.Hints;
import io.github.mjcro.toybox.swing.prefab.ToyBoxLabels;
import io.github.mjcro.toybox.templates.EnumerationValue;
import lombok.NonNull;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.function.Function;

public class EnumerationBinding<T> extends AbstractJPanelContainerBinding {
    private final List<OptionalPair<T, Label>> values;
    private JComboBox<OptionalPair<T, Label>> comboBox;

    public static <T extends Enum<T>> EnumerationBinding<T> ofEnum(Object target, Field field, Class<T> clazz) {
        return ofEnum(target, field, clazz, t -> t instanceof WithName ? ((WithName) t).getName() : t.name());
    }

    public static <T extends Enum<T>> EnumerationBinding<T> ofEnum(Object target, Field field, Class<T> clazz, Function<T, String> mapper) {
        ArrayList<OptionalPair<T, Label>> values = new ArrayList<>();
        for (T t : clazz.getEnumConstants()) {
            values.add(new EnumerationValue<>(t, mapper.apply(t)));
        }
        return new EnumerationBinding<>(target, field, values);
    }

    public EnumerationBinding(Object target, Field field, @NonNull Iterable<OptionalPair<T, Label>> values) {
        super(target, field);
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

        super.add(label, BorderLayout.LINE_START);
        super.add(comboBox, BorderLayout.CENTER);

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

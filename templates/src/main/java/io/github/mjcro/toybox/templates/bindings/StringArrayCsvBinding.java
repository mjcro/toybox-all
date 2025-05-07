package io.github.mjcro.toybox.templates.bindings;

import io.github.mjcro.toybox.swing.Components;
import io.github.mjcro.toybox.swing.prefab.ToyBoxTextComponents;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.Arrays;

public class StringArrayCsvBinding extends AbstractLabeledBinding {
    private JTextField textField;

    public StringArrayCsvBinding(Object target, Field field) {
        super(target, field);
    }

    @Override
    protected JComponent createEditor() {
        textField = ToyBoxTextComponents.createJTextField();
        textField.setToolTipText("Multiple string values separated by comma");
        textField.setPreferredSize(new Dimension(300, textField.getPreferredSize().height));
        Components.onPressEnter(textField, this::fireSubmit);
        try {
            Object currentValue = field.get(target);
            if (currentValue != null) {
                String[] strings = (String[]) currentValue;
                textField.setText(String.join(",", strings));
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return textField;
    }

    @Override
    public void setEnabled(boolean enabled) {
        textField.setEnabled(enabled);
    }

    @Override
    public void applyCurrentValue() throws IllegalAccessException {
        String text = textField.getText();
        String[] strings = Arrays.stream(text.split(","))
                .filter($ -> !$.isEmpty())
                .map(String::trim)
                .toArray(String[]::new);
        field.set(target, strings);
    }
}

package io.github.mjcro.toybox.templates.bindings;

import io.github.mjcro.toybox.swing.Components;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

public class LongArrayBinding extends AbstractLabeledBinding {
    private JTextField textField;

    public LongArrayBinding(Object target, Field field) {
        super(target, field);
    }

    @Override
    protected JComponent createEditor() {
        textField = new JTextField();
        textField.setToolTipText("Multiple int64 values\nSeparators are ',;' and space");
        textField.setPreferredSize(new Dimension(250, textField.getPreferredSize().height));
        Components.onPressEnter(textField, this::fireSubmit);
        try {
            Object currentValue = field.get(target);
            if (currentValue != null) {
                long[] longs = (long[]) currentValue;
                textField.setText(Arrays.stream(longs).boxed().map(Object::toString).collect(Collectors.joining(",")));
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
        if (annotation.trim()) {
            text = text.trim();
        }

        if (text.isEmpty()) {
            field.set(target, new long[0]);
            return;
        }

        long[] longs = Arrays.stream(text.split("[;, ]"))
                .filter($ -> !$.isEmpty())
                .map(String::trim)
                .mapToLong(Long::parseLong)
                .toArray();
        field.set(target, longs);
    }
}

package io.github.mjcro.toybox.templates.bindings;

import io.github.mjcro.toybox.swing.Components;
import io.github.mjcro.toybox.swing.prefab.ToyBoxTextComponents;
import org.jspecify.annotations.NonNull;

import javax.swing.JComponent;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Binding for {@code long[]} fields, rendered as a text field accepting
 * comma-, semicolon-, or space-separated integer values.
 */
public class LongArrayBinding extends AbstractLabeledBinding {
    private @NonNull JTextField textField;

    /**
     * Creates a new long-array binding for the given target and field.
     *
     * @param target the object containing the field
     * @param field  the annotated field
     */
    @SuppressWarnings("NullAway.Init") // textField is initialized via super() -> initComponents() -> createEditor()
    public LongArrayBinding(@NonNull Object target, @NonNull Field field) {
        super(target, field);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected @NonNull JComponent createEditor() {
        textField = ToyBoxTextComponents.createJTextField();
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEnabled(boolean enabled) {
        textField.setEnabled(enabled);
    }

    /**
     * {@inheritDoc}
     */
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

package io.github.mjcro.toybox.templates.bindings;

import io.github.mjcro.toybox.swing.Components;
import io.github.mjcro.toybox.swing.prefab.ToyBoxTextComponents;
import org.jspecify.annotations.NonNull;

import javax.swing.JComponent;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * Binding for {@code String[]} fields, rendered as a text field accepting
 * comma-separated values.
 */
public class StringArrayCsvBinding extends AbstractLabeledBinding {
    private @NonNull JTextField textField;

    /**
     * Creates a new string-array CSV binding for the given target and field.
     *
     * @param target the object containing the field
     * @param field  the annotated field
     */
    @SuppressWarnings("NullAway.Init") // textField is initialized via super() -> initComponents() -> createEditor()
    public StringArrayCsvBinding(@NonNull Object target, @NonNull Field field) {
        super(target, field);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected @NonNull JComponent createEditor() {
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
        String[] strings = Arrays.stream(text.split(","))
                .filter($ -> !$.isEmpty())
                .map(String::trim)
                .toArray(String[]::new);
        field.set(target, strings);
    }
}

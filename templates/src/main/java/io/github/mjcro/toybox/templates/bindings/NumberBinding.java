package io.github.mjcro.toybox.templates.bindings;

import io.github.mjcro.toybox.swing.Styles;
import io.github.mjcro.toybox.swing.factories.DataLinkFactory;
import io.github.mjcro.toybox.swing.linking.ComponentDataLink;

import javax.swing.*;
import java.lang.reflect.Field;

public class NumberBinding extends AbstractLabeledDataLinkBinding<JTextField, Long> {
    public NumberBinding(Object target, Field field) {
        super(target, field);
    }

    @Override
    protected ComponentDataLink<JTextField, Long> createLink() {
        return DataLinkFactory.linkNumberField(
                new JTextField(),
                field,
                target,
                Styles.setPreferredWidth(150),
                Styles.setToolTipText("Numeric " + (field.getType().getSimpleName()) + " input"),
                Styles.onEnterKeyPress(this::fireSubmit)
        );
    }
}

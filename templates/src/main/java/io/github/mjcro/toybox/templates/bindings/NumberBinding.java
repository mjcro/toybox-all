package io.github.mjcro.toybox.templates.bindings;

import io.github.mjcro.toybox.swing.hint.Hints;
import io.github.mjcro.toybox.swing.prefab.ToyBoxDataLink;
import io.github.mjcro.toybox.swing.linking.ComponentDataLink;

import javax.swing.*;
import java.lang.reflect.Field;

public class NumberBinding extends AbstractLabeledDataLinkBinding<JTextField, Long> {
    public NumberBinding(Object target, Field field) {
        super(target, field);
    }

    @Override
    protected ComponentDataLink<JTextField, Long> createLink() {
        return ToyBoxDataLink.linkNumberField(
                new JTextField(),
                field,
                target,
                Hints.setPreferredWidth(150),
                Hints.setToolTipText("Numeric " + (field.getType().getSimpleName()) + " input"),
                Hints.onEnterKeyPress(this::fireSubmit)
        );
    }
}

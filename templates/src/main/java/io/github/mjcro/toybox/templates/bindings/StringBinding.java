package io.github.mjcro.toybox.templates.bindings;

import io.github.mjcro.toybox.swing.hint.Hints;
import io.github.mjcro.toybox.swing.prefab.ToyBoxDataLink;
import io.github.mjcro.toybox.swing.linking.ComponentDataLink;
import io.github.mjcro.toybox.swing.prefab.ToyBoxTextComponents;

import javax.swing.*;
import java.lang.reflect.Field;

public class StringBinding extends AbstractLabeledDataLinkBinding<JTextField, String> {

    public StringBinding(Object target, Field field) {
        super(target, field);
    }

    @Override
    protected ComponentDataLink<JTextField, String> createLink() {
        return ToyBoxDataLink.linkStringField(
                ToyBoxTextComponents.createJTextField(),
                field,
                target,
                annotation.trim() ? String::trim : null,
                Hints.setPreferredWidth(150),
                Hints.setToolTipText("String input, " + (annotation.trim() ? "with" : "without") + " space trimming"),
                Hints.onEnterKeyPress(this::fireSubmit)
        );
    }
}

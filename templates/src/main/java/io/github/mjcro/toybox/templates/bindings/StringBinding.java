package io.github.mjcro.toybox.templates.bindings;

import io.github.mjcro.toybox.swing.Styles;
import io.github.mjcro.toybox.swing.factories.DataLinkFactory;
import io.github.mjcro.toybox.swing.linking.ComponentDataLink;

import javax.swing.*;
import java.lang.reflect.Field;

public class StringBinding extends AbstractLabeledDataLinkBinding<JTextField, String> {

    public StringBinding(Object target, Field field) {
        super(target, field);
    }

    @Override
    protected ComponentDataLink<JTextField, String> createLink() {
        return DataLinkFactory.linkStringField(
                new JTextField(),
                field,
                target,
                annotation.trim() ? String::trim : null,
                Styles.setPreferredWidth(150),
                Styles.setToolTipText("String input, " + (annotation.trim() ? "with" : "without") + " space trimming"),
                Styles.onEnterKeyPress(this::fireSubmit)
        );
    }
}

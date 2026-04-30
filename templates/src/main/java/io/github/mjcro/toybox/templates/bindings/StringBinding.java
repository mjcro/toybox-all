package io.github.mjcro.toybox.templates.bindings;

import io.github.mjcro.toybox.swing.hint.Hints;
import io.github.mjcro.toybox.swing.linking.ComponentDataLink;
import io.github.mjcro.toybox.swing.prefab.ToyBoxDataLink;
import io.github.mjcro.toybox.swing.prefab.ToyBoxTextComponents;
import org.jspecify.annotations.NonNull;

import javax.swing.JTextField;
import java.lang.reflect.Field;

/**
 * Binding for {@link String} fields, rendered as a text field with
 * optional whitespace trimming.
 */
public class StringBinding extends AbstractLabeledDataLinkBinding<JTextField, String> {

    /**
     * Creates a new string binding for the given target and field.
     *
     * @param target the object containing the field
     * @param field  the annotated field
     */
    public StringBinding(@NonNull Object target, @NonNull Field field) {
        super(target, field);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected @NonNull ComponentDataLink<@NonNull JTextField, @NonNull String> createLink() {
        return ToyBoxDataLink.linkStringField(
                ToyBoxTextComponents.createTextField(),
                field,
                target,
                annotation.trim() ? String::trim : null,
                Hints.setPreferredWidth(150),
                Hints.setToolTipText("String input, " + (annotation.trim() ? "with" : "without") + " space trimming"),
                Hints.onEnterKeyPress(this::fireSubmit)
        );
    }
}

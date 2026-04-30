package io.github.mjcro.toybox.templates.bindings;

import io.github.mjcro.toybox.swing.hint.Hints;
import io.github.mjcro.toybox.swing.linking.ComponentDataLink;
import io.github.mjcro.toybox.swing.prefab.ToyBoxDataLink;
import io.github.mjcro.toybox.swing.prefab.ToyBoxTextComponents;
import org.jspecify.annotations.NonNull;

import javax.swing.JTextField;
import java.lang.reflect.Field;

/**
 * Binding for numeric fields ({@code byte}, {@code short}, {@code int},
 * {@code long}, and their boxed equivalents, plus {@link java.math.BigInteger}
 * and {@link java.math.BigDecimal}), rendered as a text field.
 */
public class NumberBinding extends AbstractLabeledDataLinkBinding<JTextField, Long> {
    /**
     * Creates a new number binding for the given target and field.
     *
     * @param target the object containing the field
     * @param field  the annotated field
     */
    public NumberBinding(@NonNull Object target, @NonNull Field field) {
        super(target, field);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected @NonNull ComponentDataLink<@NonNull JTextField, @NonNull Long> createLink() {
        return ToyBoxDataLink.linkNumberField(
                ToyBoxTextComponents.createTextField(),
                field,
                target,
                Hints.setPreferredWidth(150),
                Hints.setToolTipText("Numeric " + (field.getType().getSimpleName()) + " input"),
                Hints.onEnterKeyPress(this::fireSubmit)
        );
    }
}

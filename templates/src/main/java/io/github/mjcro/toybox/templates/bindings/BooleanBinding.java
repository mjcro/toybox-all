package io.github.mjcro.toybox.templates.bindings;

import io.github.mjcro.toybox.swing.hint.Hints;
import io.github.mjcro.toybox.swing.linking.ComponentDataLink;
import io.github.mjcro.toybox.swing.prefab.ToyBoxDataLink;
import org.jspecify.annotations.NonNull;

import javax.swing.JCheckBox;
import java.lang.reflect.Field;

/**
 * Binding for boolean and {@link Boolean} fields, rendered as a checkbox.
 */
public class BooleanBinding extends AbstractJPanelContainerBinding {
    private final @NonNull ComponentDataLink<@NonNull JCheckBox, @NonNull Boolean> link;

    /**
     * Creates a new boolean binding for the given target and field.
     *
     * @param target the object containing the field
     * @param field  the annotated field
     */
    public BooleanBinding(@NonNull Object target, @NonNull Field field) {
        super(target, field);
        this.link = ToyBoxDataLink.linkBooleanField(
                new JCheckBox(getLabelName()),
                field,
                target,
                Hints.onAction(this::fireSubmit)
        );
        super.add(this.link.getComponent());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEnabled(boolean enabled) {
        link.getComponent().setEnabled(enabled);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void applyCurrentValue() {
        link.submit();
    }
}

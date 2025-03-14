package io.github.mjcro.toybox.templates.bindings;

import io.github.mjcro.toybox.swing.hint.Hints;
import io.github.mjcro.toybox.swing.prefab.ToyBoxDataLink;
import io.github.mjcro.toybox.swing.linking.ComponentDataLink;

import javax.swing.*;
import java.lang.reflect.Field;

public class BooleanBinding extends AbstractJPanelContainerBinding {
    private final ComponentDataLink<JCheckBox, Boolean> link;

    public BooleanBinding(Object target, Field field) {
        super(target, field);
        this.link = ToyBoxDataLink.linkBooleanField(
                new JCheckBox(getLabelName()),
                field,
                target,
                Hints.onAction(this::fireSubmit)
        );
        super.add(this.link.getComponent());
    }

    @Override
    public void setEnabled(boolean enabled) {
        link.getComponent().setEnabled(enabled);
    }

    @Override
    public void applyCurrentValue() {
        link.submit();
    }
}

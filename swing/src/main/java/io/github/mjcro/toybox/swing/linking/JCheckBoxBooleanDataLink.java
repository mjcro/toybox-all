package io.github.mjcro.toybox.swing.linking;

import javax.swing.*;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public class JCheckBoxBooleanDataLink implements ComponentDataLink<JCheckBox, Boolean> {
    private final JCheckBox component;
    private final Consumer<Optional<Boolean>> onSubmit;

    public JCheckBoxBooleanDataLink(JCheckBox component, Consumer<Optional<Boolean>> onSubmit) {
        this.component = Objects.requireNonNull(component, "component");
        this.onSubmit = onSubmit;
    }

    @Override
    public JCheckBox getComponent() {
        return component;
    }

    @Override
    public void setValue(Boolean value) {
        getComponent().setSelected(value != null && value);
    }

    @Override
    public Optional<Boolean> getValue() {
        return Optional.of(component.isSelected());
    }

    @Override
    public void submit() {
        if (onSubmit != null) {
            onSubmit.accept(getValue());
        }
    }
}

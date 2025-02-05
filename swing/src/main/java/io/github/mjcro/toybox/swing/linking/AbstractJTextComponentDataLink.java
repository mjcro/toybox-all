package io.github.mjcro.toybox.swing.linking;

import javax.swing.text.JTextComponent;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class AbstractJTextComponentDataLink<C extends JTextComponent, V> implements ComponentDataLink<C, V> {
    private final C component;
    private final Consumer<Optional<V>> onSubmit;

    public AbstractJTextComponentDataLink(C component, Consumer<Optional<V>> onSubmit) {
        this.component = Objects.requireNonNull(component, "component");
        this.onSubmit = onSubmit;
    }

    protected String valueToString(V value) {
        return value == null ? null : value.toString();
    }

    protected abstract V stringToValue(String s);

    @Override
    public final C getComponent() {
        return component;
    }

    @Override
    public final void setValue(V value) {
        getComponent().setText(valueToString(value));
    }

    @Override
    public final Optional<V> getValue() {
        return Optional.ofNullable(component.getText()).map(this::stringToValue);
    }

    @Override
    public void submit() {
        if (onSubmit != null) {
            onSubmit.accept(getValue());
        }
    }
}

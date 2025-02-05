package io.github.mjcro.toybox.swing.linking;

import javax.swing.text.JTextComponent;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public class JTextComponentStringDataLink<C extends JTextComponent> extends AbstractJTextComponentDataLink<C, String> {
    private final UnaryOperator<String> transform;

    public JTextComponentStringDataLink(C component, UnaryOperator<String> transform, Consumer<Optional<String>> onSubmit) {
        super(component, onSubmit);
        this.transform = transform;
    }

    @Override
    protected String stringToValue(String s) {
        return transform != null && s != null ? transform.apply(s) : s;
    }
}

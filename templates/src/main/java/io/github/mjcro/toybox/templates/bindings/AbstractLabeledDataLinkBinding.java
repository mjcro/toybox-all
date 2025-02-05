package io.github.mjcro.toybox.templates.bindings;

import io.github.mjcro.toybox.swing.linking.ComponentDataLink;

import javax.swing.*;
import java.lang.reflect.Field;

public abstract class AbstractLabeledDataLinkBinding<C extends JComponent, V> extends AbstractLabeledBinding {
    private ComponentDataLink<C, V> link;

    public AbstractLabeledDataLinkBinding(Object target, Field field) {
        super(target, field);
    }

    protected abstract ComponentDataLink<C, V> createLink();

    @Override
    protected JComponent createEditor() {
        link = createLink();
        return link.getComponent();
    }

    @Override
    public final void setEnabled(boolean enabled) {
        link.setEnabled(enabled);
    }

    @Override
    public final void applyCurrentValue() {
        link.submit();
    }
}

package io.github.mjcro.toybox.templates.bindings;

import io.github.mjcro.toybox.swing.hint.Hints;
import io.github.mjcro.toybox.swing.prefab.ToyBoxLabels;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;

public abstract class AbstractLabeledBinding extends AbstractJPanelContainerBinding {
    public AbstractLabeledBinding(Object target, Field field) {
        super(target, field);
        initComponents();
    }

    private void initComponents() {
        JLabel label = ToyBoxLabels.create(getLabelName());
        Hints.PADDING_NORMAL.apply(label);
        super.add(label, BorderLayout.LINE_START);
        super.add(createEditor(), BorderLayout.CENTER);
    }

    protected abstract JComponent createEditor();
}

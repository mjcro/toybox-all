package io.github.mjcro.toybox.templates.bindings;

import io.github.mjcro.toybox.swing.Styles;
import io.github.mjcro.toybox.swing.factories.LabelsFactory;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;

public abstract class AbstractLabeledBinding extends AbstractJPanelContainerBinding {
    public AbstractLabeledBinding(Object target, Field field) {
        super(target, field);
        initComponents();
    }

    private void initComponents() {
        JLabel label = LabelsFactory.create(getLabelName());
        Styles.PADDING_NORMAL.apply(label);
        super.add(label, BorderLayout.LINE_START);
        super.add(createEditor(), BorderLayout.CENTER);
    }

    protected abstract JComponent createEditor();
}

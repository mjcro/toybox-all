package io.github.mjcro.toybox.templates.bindings;

import io.github.mjcro.toybox.swing.hint.Hints;
import io.github.mjcro.toybox.swing.prefab.ToyBoxLabels;
import org.jspecify.annotations.NonNull;

import javax.swing.JComponent;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.lang.reflect.Field;

/**
 * Abstract binding that prepends a label to an editor component
 * laid out using {@link BorderLayout}.
 */
public abstract class AbstractLabeledBinding extends AbstractJPanelContainerBinding {
    /**
     * Creates a new labeled binding for the given target and field.
     *
     * @param target the object containing the field
     * @param field  the annotated field
     */
    public AbstractLabeledBinding(@NonNull Object target, @NonNull Field field) {
        super(target, field);
        initComponents();
    }

    private void initComponents() {
        JLabel label = ToyBoxLabels.create(getLabelName());
        Hints.PADDING_NORMAL.apply(label);
        super.add(label, BorderLayout.LINE_START);
        super.add(createEditor(), BorderLayout.CENTER);
    }

    /**
     * Creates the editor component placed in the center of the layout.
     *
     * @return the editor component
     */
    protected abstract @NonNull JComponent createEditor();
}

package io.github.mjcro.toybox.templates.bindings;

import io.github.mjcro.toybox.templates.Binding;
import io.github.mjcro.toybox.templates.Databind;
import lombok.NonNull;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;

public abstract class AbstractJPanelContainerBinding extends JPanel implements Binding {
    protected final Object target;
    protected final Field field;
    protected final Databind annotation;
    private Runnable onSubmit;

    public AbstractJPanelContainerBinding(@NonNull Object target, @NonNull Field field) {
        this.target = target;
        this.field = field;
        this.annotation = field.getAnnotation(Databind.class);

        super.setLayout(new BorderLayout());
    }

    protected String getLabelName() {
        return annotation.name().isEmpty() ? field.getName() : annotation.name();
    }

    protected void fireSubmit() {
        final Runnable r = onSubmit;
        if (r != null) {
            SwingUtilities.invokeLater(r);
        }
    }

    @Override
    public void setSubmitListener(Runnable callback) {
        onSubmit = callback;
    }

    @Override
    public final Component getComponent() {
        return this;
    }

    @Override
    public String toString() {
        return "[" + getClass().getSimpleName() + " " + getLabelName() + "]";
    }

    @FunctionalInterface
    protected interface RawValueExtractor<T> {
        T get();
    }
}

package io.github.mjcro.toybox.templates.bindings;

import io.github.mjcro.toybox.templates.Binding;
import io.github.mjcro.toybox.templates.Databind;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Component;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Optional;

/**
 * Abstract base class for bindings that display their UI inside a {@link JPanel}
 * container using {@link BorderLayout}.
 */
public abstract class AbstractJPanelContainerBinding extends JPanel implements Binding {

    /** The target object whose field this binding controls. */
    protected final @NonNull Object target;
    /** The reflected field on the target object. */
    protected final @NonNull Field field;
    /** The {@link Databind} annotation on the field. */
    protected final @NonNull Databind annotation;
    private @Nullable Runnable onSubmit;

    /**
     * Creates a new binding for the given target object and field.
     *
     * @param target the object containing the field
     * @param field  the annotated field
     */
    public AbstractJPanelContainerBinding(@NonNull Object target, @NonNull Field field) {
        this.target = Objects.requireNonNull(target, "target");
        this.field = Objects.requireNonNull(field, "field");
        this.annotation = field.getAnnotation(Databind.class);

        super.setLayout(new BorderLayout());
    }

    /**
     * Returns the display label name derived from the annotation or field name.
     *
     * @return the label name
     */
    protected @NonNull String getLabelName() {
        return annotation.name().isEmpty() ? field.getName() : annotation.name();
    }

    @Override
    public @NonNull Optional<@NonNull String> getGroup() {
        return Optional.ofNullable(annotation.group()).filter($ -> !$.isEmpty());
    }

    /**
     * Fires the submit listener callback on the EDT, if one is set.
     */
    protected void fireSubmit() {
        final Runnable r = onSubmit;
        if (r != null) {
            SwingUtilities.invokeLater(r);
        }
    }

    @Override
    public void setSubmitListener(@Nullable Runnable callback) {
        onSubmit = callback;
    }

    @Override
    public final @NonNull Component getComponent() {
        return this;
    }

    @Override
    public @NonNull String toString() {
        return "[" + getClass().getSimpleName() + " " + getLabelName() + "]";
    }

    /**
     * Functional interface for extracting a raw value from a UI component.
     *
     * @param <T> the value type
     */
    @FunctionalInterface
    protected interface RawValueExtractor<T> {

        /**
         * Extracts the current raw value.
         *
         * @return the extracted value
         */
        T get();
    }
}

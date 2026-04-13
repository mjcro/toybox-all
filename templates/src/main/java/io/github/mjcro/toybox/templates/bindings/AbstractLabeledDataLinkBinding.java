package io.github.mjcro.toybox.templates.bindings;

import io.github.mjcro.toybox.swing.linking.ComponentDataLink;
import org.jspecify.annotations.NonNull;

import javax.swing.JComponent;
import java.lang.reflect.Field;

/**
 * Abstract labeled binding backed by a {@link ComponentDataLink} for
 * two-way data binding between a Swing component and an object field.
 *
 * @param <C> the Swing component type
 * @param <V> the value type
 */
public abstract class AbstractLabeledDataLinkBinding<C extends JComponent, V> extends AbstractLabeledBinding {
    private @NonNull ComponentDataLink<C, V> link;

    /**
     * Creates a new data-link binding for the given target and field.
     *
     * @param target the object containing the field
     * @param field  the annotated field
     */
    @SuppressWarnings("NullAway.Init") // link is initialized via super() -> initComponents() -> createLink()
    public AbstractLabeledDataLinkBinding(@NonNull Object target, @NonNull Field field) {
        super(target, field);
    }

    /**
     * Creates the component data link used by this binding.
     *
     * @return the component data link
     */
    protected abstract @NonNull ComponentDataLink<C, V> createLink();

    /**
     * {@inheritDoc}
     */
    @Override
    protected @NonNull JComponent createEditor() {
        link = createLink();
        return link.getComponent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setEnabled(boolean enabled) {
        link.setEnabled(enabled);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void applyCurrentValue() {
        link.submit();
    }
}

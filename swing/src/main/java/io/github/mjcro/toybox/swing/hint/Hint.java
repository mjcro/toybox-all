package io.github.mjcro.toybox.swing.hint;

import org.jspecify.annotations.NonNull;

import javax.swing.JComponent;

/**
 * Defines a Java Swing {@link JComponent} hint -- something that can be used
 * to decorate, assign listeners, or change the state of a component.
 *
 * @param <T> the type of component this hint applies to
 */
@FunctionalInterface
public interface Hint<T extends JComponent> {
    /**
     * Applies all given hints to the component.
     *
     * @param component component to apply hints on
     * @param hints     hints to apply
     * @param <T>       component type
     */
    @SafeVarargs
    static <T extends JComponent> void applyAll(@NonNull T component, @NonNull Hint<? super T> @NonNull ... hints) {
        for (final Hint<? super T> style : hints) {
            style.apply(component);
        }
    }

    /**
     * Combines multiple hints into a single one.
     *
     * @param hints hints to combine
     * @param <T>   component type
     * @return combined hint
     */
    @SafeVarargs
    static <T extends JComponent> @NonNull Hint<@NonNull T> combine(@NonNull Hint<? super T> @NonNull ... hints) {
        return c -> {
            if (hints != null) {
                for (final Hint<? super T> s : hints) {
                    s.apply(c);
                }
            }
        };
    }

    /**
     * Applies this hint to the given component.
     *
     * @param component component to apply the hint on
     */
    void apply(@NonNull T component);

    /**
     * Applies this hint to the given component and returns it.
     *
     * @param component component to apply the hint on
     * @return the same component
     */
    default @NonNull T wrap(@NonNull T component) {
        apply(component);
        return component;
    }
}

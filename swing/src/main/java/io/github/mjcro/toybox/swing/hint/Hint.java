package io.github.mjcro.toybox.swing.hint;

import javax.swing.*;

/**
 * Defines Java Swing JComponent hint - something that can be used
 * to decorate, assign listeners or change state of {@link JComponent}.
 *
 * @param <T>
 */
@FunctionalInterface
public interface Hint<T extends JComponent> {
    /**
     * Applies all given hints to component.
     *
     * @param component Component to apply hints on.
     * @param hints     Hints to apply.
     * @param <T>       Component type.
     */
    @SafeVarargs
    static <T extends JComponent> void applyAll(T component, Hint<? super T>... hints) {
        for (Hint<? super T> style : hints) {
            style.apply(component);
        }
    }

    /**
     * Combines multiple hints into single one.
     *
     * @param hints Hints to combine.
     * @param <T>   Component type.
     * @return Hint.
     */
    @SafeVarargs
    static <T extends JComponent> Hint<T> combine(Hint<? super T>... hints) {
        return c -> {
            if (hints != null) {
                for (Hint<? super T> s : hints) {
                    s.apply(c);
                }
            }
        };
    }

    /**
     * Applies hint on given component.
     *
     * @param component Component to apply hint on.
     */
    void apply(T component);

    /**
     * Applies hint on given component and returns it.
     *
     * @param component Component to apply hint on.
     * @return Component.
     */
    default T wrap(T component) {
        apply(component);
        return component;
    }
}

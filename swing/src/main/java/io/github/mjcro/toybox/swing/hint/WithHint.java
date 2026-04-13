package io.github.mjcro.toybox.swing.hint;

import org.jspecify.annotations.Nullable;

import javax.swing.JComponent;

/**
 * Defines data objects that contain a {@link Hint}.
 *
 * @param <T> the type of component the hint applies to
 */
public interface WithHint<T extends JComponent> {
    /**
     * Returns the hint instance, or {@code null} if none is set.
     *
     * @return hint instance, may be {@code null}
     */
    @Nullable Hint<? super T> getHint();
}

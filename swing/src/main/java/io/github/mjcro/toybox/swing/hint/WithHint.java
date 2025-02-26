package io.github.mjcro.toybox.swing.hint;

import javax.swing.*;

/**
 * Defines data objects containing {@link Hint}.
 */
public interface WithHint<T extends JComponent> {
    /**
     * @return Hint instance.
     */
    Hint<? super T> getHint();
}

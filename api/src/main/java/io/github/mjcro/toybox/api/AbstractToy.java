package io.github.mjcro.toybox.api;

import javax.swing.*;

/**
 * Defines minimal renderable toy that has only builder function
 * and label.
 * <p>
 * Direct implementors of this interface cannot be registered and
 * cannot use full window manage functionality like persistence.
 */
public interface AbstractToy extends Labeled {
    /**
     * Builds Java Swing panel.
     *
     * @param context ToyBox context.
     * @return JPanel to render.
     */
    JPanel build(Context context);
}

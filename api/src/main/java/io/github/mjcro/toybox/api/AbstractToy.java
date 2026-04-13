package io.github.mjcro.toybox.api;

import org.jspecify.annotations.NonNull;

import javax.swing.JPanel;

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
    @NonNull JPanel build(@NonNull Context context);
}

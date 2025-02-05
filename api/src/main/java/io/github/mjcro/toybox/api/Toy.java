package io.github.mjcro.toybox.api;

import javax.swing.*;
import java.util.List;
import java.util.Optional;

public interface Toy extends AbstractToy {
    /**
     * Returns path how this toy can be located.
     *
     * @return Path.
     */
    List<Menu> getPath();

    /**
     * Sets whenever toy is considered persistent and should
     * be cached after first instantiation.
     *
     * @return True if toy is persistent.
     */
    default boolean isPersistent() {
        return false;
    }

    /**
     * @return Toy version.
     */
    default Optional<String> getVersion() {
        return Optional.empty();
    }

    /**
     * Builds Java Swing panel.
     *
     * @param context ToyBox context.
     * @return JPanel to render.
     */
    JPanel build(Context context);
}
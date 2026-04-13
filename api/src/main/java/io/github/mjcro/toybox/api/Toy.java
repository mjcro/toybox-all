package io.github.mjcro.toybox.api;

import org.jspecify.annotations.NonNull;

import javax.swing.JPanel;
import java.util.List;
import java.util.Optional;

/**
 * Represents a pluggable feature panel in the ToyBox application.
 *
 * <p>Each toy provides a menu path for navigation, an optional version,
 * and a factory method to build its Swing UI panel.</p>
 */
public interface Toy extends AbstractToy {
    /**
     * Returns the navigation path used to locate this toy in the menu tree.
     *
     * @return ordered list of menu entries forming the path, never {@code null}
     */
    @NonNull List<@NonNull Menu> getPath();

    /**
     * Indicates whether this toy is persistent and should be cached
     * after its first instantiation.
     *
     * @return {@code true} if the toy panel should be reused across activations
     */
    default boolean isPersistent() {
        return false;
    }

    /**
     * Returns the version of this toy, if available.
     *
     * @return an {@link Optional} containing the version string, or empty
     */
    default @NonNull Optional<@NonNull String> getVersion() {
        return Optional.empty();
    }

    /**
     * Builds and returns the Swing panel for this toy.
     *
     * @param context the current ToyBox context
     * @return the constructed panel, never {@code null}
     */
    @NonNull JPanel build(@NonNull Context context);
}

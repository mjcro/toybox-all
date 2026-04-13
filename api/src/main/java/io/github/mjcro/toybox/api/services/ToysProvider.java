package io.github.mjcro.toybox.api.services;

import io.github.mjcro.toybox.api.Toy;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * Service Provider Interface for supplying a batch of {@link Toy} instances.
 *
 * <p>Implementations are discovered via {@link java.util.ServiceLoader}
 * and allow registering multiple toys at once.</p>
 */
public interface ToysProvider {
    /**
     * Returns the list of toys provided by this provider.
     *
     * @return a list of toys, never {@code null}
     */
    @NonNull List<@NonNull Toy> getToys();
}

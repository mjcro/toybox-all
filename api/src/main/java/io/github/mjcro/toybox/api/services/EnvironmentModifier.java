package io.github.mjcro.toybox.api.services;

import io.github.mjcro.toybox.api.Environment;
import org.jspecify.annotations.NonNull;

/**
 * Service Provider Interface for modifying the application {@link Environment} at startup.
 *
 * <p>Implementations are discovered via {@link java.util.ServiceLoader} and invoked
 * during application initialization to register toys, listeners, or other resources.</p>
 */
public interface EnvironmentModifier {
    /**
     * Modifies the given environment, typically by registering toys or listeners.
     *
     * @param environment the mutable environment to modify
     */
    void modify(@NonNull Environment environment);
}

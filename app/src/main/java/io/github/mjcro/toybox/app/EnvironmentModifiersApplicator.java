package io.github.mjcro.toybox.app;

import io.github.mjcro.toybox.api.Environment;
import io.github.mjcro.toybox.api.services.EnvironmentModifier;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.util.ServiceLoader;

/**
 * Spring component that discovers and applies all {@link EnvironmentModifier}
 * implementations found via {@link ServiceLoader} at startup.
 */
@Component
public class EnvironmentModifiersApplicator {
    /**
     * Constructs the applicator and immediately applies all discovered modifiers
     * to the given environment.
     *
     * @param environment the application environment to modify
     */
    public EnvironmentModifiersApplicator(@NonNull Environment environment) {
        for (EnvironmentModifier modifier : ServiceLoader.load(EnvironmentModifier.class)) {
            modifier.modify(environment);
        }
    }
}

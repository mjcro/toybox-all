package io.github.mjcro.toybox.app;

import io.github.mjcro.toybox.api.Environment;
import io.github.mjcro.toybox.api.services.EnvironmentModifier;
import org.springframework.stereotype.Component;

import java.util.ServiceLoader;

@Component
public class EnvironmentModifiersApplicator {
    public EnvironmentModifiersApplicator(Environment environment) {
        for (EnvironmentModifier modifier : ServiceLoader.load(EnvironmentModifier.class)) {
            modifier.modify(environment);
        }
    }
}

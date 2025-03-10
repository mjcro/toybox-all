package io.github.mjcro.toybox.app;

import java.util.Optional;

public interface VariablesStorage extends Iterable<String> {
    /**
     * @param name Variable name.
     * @return ToyBox environment variable, if any.
     */
    Optional<String> getVariable(String name);

    /**
     * Sets ToyBox environment variable.
     *
     * @param name  Variable name.
     * @param value Variable value.
     */
    void setVariable(String name, String value);
}

package io.github.mjcro.toybox.app.settings;

import io.github.mjcro.toybox.api.Setting;
import org.jspecify.annotations.NonNull;

/**
 * Base class for ToyBox internal settings, providing a fixed namespace.
 */
public abstract class ToyBoxAbstractSetting implements Setting {
    /**
     * Returns the fixed namespace for all ToyBox settings.
     *
     * @return the namespace string {@code "ToyBox"}
     */
    @Override
    public final @NonNull String getNamespace() {
        return "ToyBox";
    }

    @Override
    public @NonNull String toString() {
        return getClass().getSimpleName() + " = " + getValue();
    }
}

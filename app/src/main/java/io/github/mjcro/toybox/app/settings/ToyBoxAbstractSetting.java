package io.github.mjcro.toybox.app.settings;

import io.github.mjcro.toybox.api.Setting;

public abstract class ToyBoxAbstractSetting implements Setting {
    @Override
    public final String getNamespace() {
        return "ToyBox";
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " = " + getValue();
    }
}

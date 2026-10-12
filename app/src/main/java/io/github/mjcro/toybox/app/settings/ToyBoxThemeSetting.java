package io.github.mjcro.toybox.app.settings;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Setting that stores the selected Look and Feel class name.
 */
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,  // use fields
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE
)
public class ToyBoxThemeSetting extends ToyBoxAbstractSetting {
    @JsonProperty("value")
    private final @NonNull String value;

    /**
     * Constructs a setting with the given Look and Feel class name.
     *
     * @param value the Look and Feel class name
     */
    @JsonCreator
    public ToyBoxThemeSetting(@JsonProperty("value") @NonNull String value) {
        this.value = Objects.requireNonNull(value);
    }

    @Override
    public @NonNull String getName() {
        return "Theme";
    }

    @Override
    public @NonNull String getValue() {
        return value;
    }

    @Override
    public boolean isSensitive() {
        return false;
    }
}

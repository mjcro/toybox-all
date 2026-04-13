package io.github.mjcro.toybox.app.settings;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.util.Objects;

/**
 * Setting that stores the ToyBox working directory path.
 */
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,  // use fields
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE
)
public class ToyBoxWorkingDirSetting extends ToyBoxAbstractSetting {
    @JsonProperty("value")
    private final @NonNull String value;

    /**
     * Constructs a setting with the given directory path string.
     *
     * @param value the absolute directory path
     */
    @JsonCreator
    public ToyBoxWorkingDirSetting(@JsonProperty("value") @NonNull String value) {
        this.value = Objects.requireNonNull(value);
    }

    /**
     * Constructs a setting from a {@link File}, using its absolute path.
     *
     * @param file the directory file
     */
    public ToyBoxWorkingDirSetting(@NonNull File file) {
        this(file.getAbsolutePath());
    }

    @Override
    public @NonNull String getName() {
        return "Working Directory";
    }

    @Override
    public @NonNull String getValue() {
        return value;
    }
}

package io.github.mjcro.toybox.app.settings;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.File;
import java.util.Objects;

@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,  // use fields
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE
)
public class ToyBoxWorkingDirSetting extends ToyBoxAbstractSetting {
    @JsonProperty("value")
    private final String value;

    @JsonCreator
    public ToyBoxWorkingDirSetting(@JsonProperty("value") String value) {
        this.value = Objects.requireNonNull(value);
    }

    public ToyBoxWorkingDirSetting(File file) {
        this(file.getAbsolutePath());
    }

    @Override
    public String getName() {
        return "Working Directory";
    }

    @Override
    public String getValue() {
        return value;
    }
}

package io.github.mjcro.toybox.app.settings;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.NonNull;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Setting that records the creation timestamp of the settings file.
 */
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,  // use fields
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE
)
public class ToyBoxSettingFileCreatedSetting extends ToyBoxAbstractSetting {
    @JsonProperty("value")
    private final long timestamp;

    /**
     * Constructs a setting with the given epoch-second timestamp.
     *
     * @param timestamp the creation time as epoch seconds
     */
    @JsonCreator
    public ToyBoxSettingFileCreatedSetting(@JsonProperty("value") long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Constructs a setting using the current time.
     */
    public ToyBoxSettingFileCreatedSetting() {
        this(Instant.now().getEpochSecond());
    }

    @Override
    public @NonNull String getName() {
        return "Setting file creation time";
    }

    /**
     * Returns the creation timestamp as an {@link Instant}.
     *
     * @return the creation instant
     */
    @Override
    public @NonNull Instant getValue() {
        return Instant.ofEpochSecond(timestamp);
    }

    /**
     * Returns a human-readable UTC-formatted display string.
     *
     * @return the formatted creation time with UTC suffix
     */
    @Override
    public @NonNull String getDisplayValue() {
        return DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
                .withZone(ZoneOffset.UTC)
                .format(getValue()) + " UTC";
    }
}

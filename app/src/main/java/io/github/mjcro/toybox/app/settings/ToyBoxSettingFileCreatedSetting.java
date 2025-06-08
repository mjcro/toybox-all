package io.github.mjcro.toybox.app.settings;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,  // use fields
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE
)
public class ToyBoxSettingFileCreatedSetting extends ToyBoxAbstractSetting {
    @JsonProperty("value")
    private final long timestamp;

    @JsonCreator
    public ToyBoxSettingFileCreatedSetting(@JsonProperty("value") long timestamp) {
        this.timestamp = timestamp;
    }

    public ToyBoxSettingFileCreatedSetting() {
        this(Instant.now().getEpochSecond());
    }

    @Override
    public String getName() {
        return "Setting file creation time";
    }

    @Override
    public Instant getValue() {
        return Instant.ofEpochSecond(timestamp);
    }

    @Override
    public String getDisplayValue() {
        return DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
                .withZone(ZoneOffset.UTC)
                .format(getValue()) + " UTC";
    }
}

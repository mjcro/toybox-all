package io.github.mjcro.toybox.swing.linking;

import com.github.lgooddatepicker.components.DateTimePicker;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Data link binding a {@link DateTimePicker} component to a {@link LocalDateTime} value.
 */
public class DateTimePickerLocalDateTimeDataLink implements ComponentDataLink<DateTimePicker, LocalDateTime> {
    private final @NonNull DateTimePicker component;
    private final @Nullable Consumer<@NonNull Optional<@NonNull LocalDateTime>> onSubmit;

    /**
     * Creates a new data link for the given date-time picker.
     *
     * @param component The date-time picker component to bind.
     * @param onSubmit  Optional callback invoked on submit with the current value.
     */
    public DateTimePickerLocalDateTimeDataLink(
            @NonNull DateTimePicker component,
            @Nullable Consumer<@NonNull Optional<@NonNull LocalDateTime>> onSubmit
    ) {
        this.component = Objects.requireNonNull(component, "component");
        this.onSubmit = onSubmit;
    }

    @Override
    public @NonNull DateTimePicker getComponent() {
        return component;
    }

    @Override
    public void submit() {
        if (onSubmit != null) {
            onSubmit.accept(getValue());
        }
    }

    @Override
    public void setValue(@Nullable LocalDateTime value) {
        component.setDateTimeStrict(value);
    }

    @Override
    public @NonNull Optional<@NonNull LocalDateTime> getValue() {
        return Optional.ofNullable(component.getDateTimeStrict());
    }
}

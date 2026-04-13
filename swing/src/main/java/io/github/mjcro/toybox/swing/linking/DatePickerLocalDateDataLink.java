package io.github.mjcro.toybox.swing.linking;

import com.github.lgooddatepicker.components.DatePicker;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * A {@link ComponentDataLink} binding a {@link DatePicker} to a {@link LocalDate} value.
 */
public class DatePickerLocalDateDataLink implements ComponentDataLink<DatePicker, LocalDate> {
    private final @NonNull DatePicker component;
    private final @Nullable Consumer<@NonNull Optional<@Nullable LocalDate>> onSubmit;

    /**
     * Creates a new data link for the given date picker.
     *
     * @param component date picker component, must not be {@code null}
     * @param onSubmit  callback invoked on submit, may be {@code null}
     */
    public DatePickerLocalDateDataLink(@NonNull DatePicker component, @Nullable Consumer<@NonNull Optional<@Nullable LocalDate>> onSubmit) {
        this.component = Objects.requireNonNull(component, "component");
        this.onSubmit = onSubmit;
    }

    @Override
    public @NonNull DatePicker getComponent() {
        return component;
    }

    @Override
    public void submit() {
        if (onSubmit != null) {
            onSubmit.accept(getValue());
        }
    }

    @Override
    public void setValue(@Nullable LocalDate value) {
        component.setDate(value);
    }

    @Override
    public @NonNull Optional<@Nullable LocalDate> getValue() {
        return Optional.ofNullable(component.getDate());
    }
}

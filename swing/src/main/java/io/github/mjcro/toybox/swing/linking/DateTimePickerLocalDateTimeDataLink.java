package io.github.mjcro.toybox.swing.linking;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DateTimePicker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public class DateTimePickerLocalDateTimeDataLink implements ComponentDataLink<DateTimePicker, LocalDateTime> {
    private final DateTimePicker component;
    private final Consumer<Optional<LocalDateTime>> onSubmit;

    public DateTimePickerLocalDateTimeDataLink(DateTimePicker component, Consumer<Optional<LocalDateTime>> onSubmit) {
        this.component = Objects.requireNonNull(component, "component");
        this.onSubmit = onSubmit;
    }

    @Override
    public DateTimePicker getComponent() {
        return component;
    }

    @Override
    public void submit() {
        if (onSubmit != null) {
            onSubmit.accept(getValue());
        }
    }

    @Override
    public void setValue(LocalDateTime value) {
        component.setDateTimeStrict(value);
    }

    @Override
    public Optional<LocalDateTime> getValue() {
        return Optional.ofNullable(component.getDateTimeStrict());
    }
}

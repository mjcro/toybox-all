package io.github.mjcro.toybox.swing.linking;

import com.github.lgooddatepicker.components.DatePicker;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public class DatePickerLocalDateDataLink implements ComponentDataLink<DatePicker, LocalDate> {
    private final DatePicker component;
    private final Consumer<Optional<LocalDate>> onSubmit;

    public DatePickerLocalDateDataLink(DatePicker component, Consumer<Optional<LocalDate>> onSubmit) {
        this.component = Objects.requireNonNull(component, "component");
        this.onSubmit = onSubmit;
    }

    @Override
    public DatePicker getComponent() {
        return component;
    }

    @Override
    public void submit() {
        if (onSubmit != null) {
            onSubmit.accept(getValue());
        }
    }

    @Override
    public void setValue(LocalDate value) {
        component.setDate(value);
    }

    @Override
    public Optional<LocalDate> getValue() {
        return Optional.ofNullable(component.getDate());
    }
}

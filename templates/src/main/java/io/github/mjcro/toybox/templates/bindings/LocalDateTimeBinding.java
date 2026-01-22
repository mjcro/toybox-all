package io.github.mjcro.toybox.templates.bindings;

import com.github.lgooddatepicker.components.DateTimePicker;
import io.github.mjcro.toybox.swing.linking.ComponentDataLink;
import io.github.mjcro.toybox.swing.prefab.ToyBoxDataLink;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Locale;

public class LocalDateTimeBinding extends AbstractLabeledDataLinkBinding<DateTimePicker, LocalDateTime> {
    public LocalDateTimeBinding(Object target, Field field) {
        super(target, field);
    }

    @Override
    protected ComponentDataLink<DateTimePicker, LocalDateTime> createLink() {
        DateTimePicker component = new DateTimePicker();
        component.getDatePicker().getSettings().setAllowKeyboardEditing(false);
        component.getDatePicker().getSettings().setLocale(Locale.ROOT);
        component.getDatePicker().getSettings().setFormatForDatesCommonEra("yyyy-MM-dd");
        return ToyBoxDataLink.linkLocalDateTimeField(
                component,
                field,
                target
        );
    }
}
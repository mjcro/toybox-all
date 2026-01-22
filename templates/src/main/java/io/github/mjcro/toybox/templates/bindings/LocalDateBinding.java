package io.github.mjcro.toybox.templates.bindings;

import com.github.lgooddatepicker.components.DatePicker;
import io.github.mjcro.toybox.swing.linking.ComponentDataLink;
import io.github.mjcro.toybox.swing.prefab.ToyBoxDataLink;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Locale;

public class LocalDateBinding extends AbstractLabeledDataLinkBinding<DatePicker, LocalDate> {
    public LocalDateBinding(Object target, Field field) {
        super(target, field);
    }

    @Override
    protected ComponentDataLink<DatePicker, LocalDate> createLink() {
        DatePicker component = new DatePicker();
        component.getSettings().setAllowKeyboardEditing(false);
        component.getSettings().setLocale(Locale.ROOT);
        component.getSettings().setFormatForDatesCommonEra("yyyy-MM-dd");
        return ToyBoxDataLink.linkLocalDateField(
                component,
                field,
                target
        );
    }
}

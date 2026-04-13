package io.github.mjcro.toybox.templates.bindings;

import com.github.lgooddatepicker.components.DateTimePicker;
import io.github.mjcro.toybox.swing.linking.ComponentDataLink;
import io.github.mjcro.toybox.swing.prefab.ToyBoxDataLink;
import org.jspecify.annotations.NonNull;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Locale;

/**
 * Binding for {@link LocalDateTime} fields, rendered as a date-time picker.
 */
public class LocalDateTimeBinding extends AbstractLabeledDataLinkBinding<DateTimePicker, LocalDateTime> {
    /**
     * Creates a new local-date-time binding for the given target and field.
     *
     * @param target the object containing the field
     * @param field  the annotated field
     */
    public LocalDateTimeBinding(@NonNull Object target, @NonNull Field field) {
        super(target, field);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected @NonNull ComponentDataLink<@NonNull DateTimePicker, @NonNull LocalDateTime> createLink() {
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

package io.github.mjcro.toybox.templates.bindings;

import com.github.lgooddatepicker.components.DatePicker;
import io.github.mjcro.toybox.swing.linking.ComponentDataLink;
import io.github.mjcro.toybox.swing.prefab.ToyBoxDataLink;
import org.jspecify.annotations.NonNull;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Locale;

/**
 * Binding for {@link LocalDate} fields, rendered as a date picker.
 */
public class LocalDateBinding extends AbstractLabeledDataLinkBinding<DatePicker, LocalDate> {
    /**
     * Creates a new local-date binding for the given target and field.
     *
     * @param target the object containing the field
     * @param field  the annotated field
     */
    public LocalDateBinding(@NonNull Object target, @NonNull Field field) {
        super(target, field);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected @NonNull ComponentDataLink<@NonNull DatePicker, @NonNull LocalDate> createLink() {
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

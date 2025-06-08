package io.github.mjcro.toybox.templates.bindings;

import io.github.mjcro.interfaces.Decorator;
import io.github.mjcro.toybox.api.Environment;
import io.github.mjcro.toybox.swing.widgets.FileChooserInput;
import lombok.NonNull;

import javax.swing.filechooser.FileFilter;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class FileBinding extends AbstractJPanelContainerBinding {
    private final FileChooserInput fileChooserInput;

    public FileBinding(
            @NonNull Environment environment,
            @NonNull Object target,
            @NonNull Field field
    ) {
        super(target, field);

        // Reading options
        FileFilter[] fileFilters = new FileFilter[0];
        Class<?> options = annotation.options();
        if (options != null && options != Void.class) {
            fileFilters = readOptions(options);
        }

        this.fileChooserInput = new FileChooserInput(environment, "Choose file", this::fireSubmit, fileFilters);
        super.add(this.fileChooserInput);
    }

    private FileFilter[] readOptions(Class<?> options) {
        if (options.isEnum()) {
            Object[] constants = options.getEnumConstants();
            ArrayList<FileFilter> filters = new ArrayList<>();
            for (Object c : constants) {
                if (c instanceof Decorator<?>) {
                    c = ((Decorator<?>) c).getDecorated();
                }
                if (c instanceof FileFilter) {
                    filters.add((FileFilter) c);
                }
            }

            if (!filters.isEmpty()) {
                return filters.toArray(new FileFilter[0]);
            }
        }

        return new FileFilter[0];
    }

    @Override
    public void setEnabled(boolean enabled) {
        fileChooserInput.setEnabled(enabled);
    }

    @Override
    public void applyCurrentValue() throws IllegalAccessException {
        field.set(target, fileChooserInput.getFile().orElse(null));
    }
}

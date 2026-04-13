package io.github.mjcro.toybox.templates.example;

import io.github.mjcro.interfaces.Decorator;
import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Label;
import io.github.mjcro.toybox.api.Menu;
import io.github.mjcro.toybox.templates.AbstractStringTemplateToy;
import io.github.mjcro.toybox.templates.Databind;
import io.github.mjcro.toybox.templates.StringProducer;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Example string template demonstrating {@link File} field bindings
 * with optional file-type filtering.
 */
public class FilesExampleStringTemplate extends AbstractStringTemplateToy {
    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull List<@NonNull Menu> getPath() {
        ArrayList<Menu> path = new ArrayList<>();
        path.add(Menu.TOYBOX_MENU);
        path.add(Menu.TOYBOX_DEVELOPMENT_MENU);
        path.add(Menu.TOYBOX_EXAMPLES_SUBMENU);
        return path;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull Label getLabel() {
        return Label.ofName("Files Template");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected @NonNull StringProducer getDataObject(@NonNull Context context) {
        return new Data();
    }

    /**
     * Data template holding file fields for the example.
     */
    private static class Data implements StringProducer {
        @Databind(name = "Any file")
        private @Nullable File f1;
        @Databind(name = "CSV file", options = CSVFiles.class)
        private @Nullable File f2;

        /**
         * {@inheritDoc}
         */
        @Override
        public void produce(@NonNull StringBuilder sb) {
            sb.append("Any file: ").append(f1).append("\n");
            sb.append("CSV file: ").append(f2).append("\n");
        }
    }

    /**
     * File filter decorator for CSV files.
     */
    public enum CSVFiles implements Decorator<FileFilter> {
        CSV(new FileNameExtensionFilter("CSV files", "csv"));

        private final @NonNull FileFilter filter;

        CSVFiles(@NonNull FileFilter f) {
            this.filter = f;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public @NonNull FileFilter getDecorated() {
            return filter;
        }
    }
}

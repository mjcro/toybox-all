package io.github.mjcro.toybox.templates.example;

import io.github.mjcro.interfaces.Decorator;
import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Label;
import io.github.mjcro.toybox.api.Menu;
import io.github.mjcro.toybox.templates.AbstractStringTemplateToy;
import io.github.mjcro.toybox.templates.Databind;
import io.github.mjcro.toybox.templates.StringProducer;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FilesExampleStringTemplate extends AbstractStringTemplateToy {
    @Override
    public List<Menu> getPath() {
        ArrayList<Menu> path = new ArrayList<>();
        path.add(Menu.TOYBOX_MENU);
        path.add(Menu.TOYBOX_DEVELOPMENT_MENU);
        path.add(Menu.TOYBOX_EXAMPLES_SUBMENU);
        return path;
    }

    @Override
    public Label getLabel() {
        return Label.ofName("Files Template");
    }

    @Override
    protected StringProducer getDataObject(final Context context) {
        return new Data();
    }

    private static class Data implements StringProducer {
        @Databind(name = "Any file")
        private File f1;
        @Databind(name = "CSV file", options = CSVFiles.class)
        private File f2;

        @Override
        public void produce(StringBuilder sb) {
            sb.append("Any file: ").append(f1).append("\n");
            sb.append("CSV file: ").append(f2).append("\n");
        }
    }

    public enum CSVFiles implements Decorator<FileFilter> {
        CSV(new FileNameExtensionFilter("CSV files", "csv"));

        private final FileFilter filter;

        CSVFiles(FileFilter f) {
            this.filter = f;
        }


        @Override
        public FileFilter getDecorated() {
            return filter;
        }
    }
}

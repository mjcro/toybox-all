package io.github.mjcro.toybox.templates.example;

import io.github.mjcro.interfaces.tuples.OptionalPair;
import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Label;
import io.github.mjcro.toybox.api.Menu;
import io.github.mjcro.toybox.templates.AbstractStringTemplateToy;
import io.github.mjcro.toybox.templates.Databind;
import io.github.mjcro.toybox.templates.EnumerationValue;
import io.github.mjcro.toybox.templates.StringProducer;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ExampleStringTemplate extends AbstractStringTemplateToy {
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
        return Label.ofName("String Template");
    }

    @Override
    protected StringProducer getDataObject(Context context) {
        return new Data();
    }

    private static class Data extends AbstractTemplateHelper {
        @Databind(name = "ID")
        private long id;
        @Databind(name = "Parent")
        private Long parentId;
        @Databind(name = "Amount")
        private BigDecimal amount;
        @Databind()
        private String name = "Some text";
        @Databind
        private boolean enabled;
        @Databind(name = "Enumeration")
        private FooBar x;
        @Databind(name = "Custom", enumerationProvider = CustomEnumerator.class)
        private long y;
        @Databind(name = "Long list")
        private long[] longs = new long[]{9, -3};
        @Databind(name = "Strings list")
        private String[] strings = new String[]{"hello", "world"};
        @Databind(name = "Some file")
        private File file;
    }

    private enum FooBar {
        FOO, BAR, BAZ
    }

    private static class CustomEnumerator extends ArrayList<OptionalPair<?, Label>> {
        public CustomEnumerator() {
            add(new EnumerationValue<>(3L, "The one"));
            add(new EnumerationValue<>(5L, "Second"));
        }
    }
}

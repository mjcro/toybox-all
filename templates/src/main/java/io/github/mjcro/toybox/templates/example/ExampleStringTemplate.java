package io.github.mjcro.toybox.templates.example;

import io.github.mjcro.interfaces.tuples.OptionalPair;
import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Label;
import io.github.mjcro.toybox.api.Menu;
import io.github.mjcro.toybox.templates.AbstractStringTemplateToy;
import io.github.mjcro.toybox.templates.Databind;
import io.github.mjcro.toybox.templates.EnumerationValue;
import io.github.mjcro.toybox.templates.StringProducer;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Legacy example string template showcasing various field types.
 *
 * @deprecated replaced by more focused per-type example templates
 */
@Deprecated
public class ExampleStringTemplate extends AbstractStringTemplateToy {
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
        return Label.ofName("String Template");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected @NonNull StringProducer getDataObject(@NonNull Context context) {
        return new Data();
    }

    /**
     * Data template holding mixed-type fields for the legacy example.
     */
    private static class Data extends AbstractTemplateHelper {
        @Databind(name = "ID")
        private long id;
        @Databind(name = "Parent")
        private @Nullable Long parentId;
        @Databind(name = "Amount")
        private @Nullable BigDecimal amount;
        @Databind()
        private @NonNull String name = "Some text";
        @Databind
        private boolean enabled;
        @Databind(name = "Enumeration")
        private @Nullable FooBar x;
        @Databind(name = "Custom", enumerationProvider = CustomEnumerator.class)
        private long y;
        @Databind(name = "Long list")
        private long[] longs = new long[]{9, -3};
        @Databind(name = "Strings list")
        private @NonNull String[] strings = new String[]{"hello", "world"};
        @Databind(name = "Some file")
        private @Nullable File file;
    }

    private enum FooBar {
        FOO, BAR, BAZ
    }

    /**
     * Custom enumeration provider for the legacy example.
     */
    private static class CustomEnumerator extends ArrayList<OptionalPair<?, Label>> {
        /**
         * Populates the enumerator with predefined values.
         */
        public CustomEnumerator() {
            add(new EnumerationValue<>(3L, "The one"));
            add(new EnumerationValue<>(5L, "Second"));
        }
    }
}

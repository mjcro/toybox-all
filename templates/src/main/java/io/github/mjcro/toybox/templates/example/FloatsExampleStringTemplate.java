package io.github.mjcro.toybox.templates.example;

import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Label;
import io.github.mjcro.toybox.api.Menu;
import io.github.mjcro.toybox.templates.AbstractStringTemplateToy;
import io.github.mjcro.toybox.templates.Databind;
import io.github.mjcro.toybox.templates.StringProducer;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Example string template demonstrating floating-point and
 * {@link BigDecimal} field bindings.
 */
public class FloatsExampleStringTemplate extends AbstractStringTemplateToy {
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
        return Label.ofName("Floats Template");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected @NonNull StringProducer getDataObject(@NonNull Context context) {
        return new Template();
    }

    /**
     * Data template holding floating-point fields for the example.
     */
    private static class Template extends AbstractTemplateHelper {
        @Databind(name = "float")
        private float f1;
        @Databind(name = "float preset")
        private float f2 = 0.32f;
        @Databind(name = "Float")
        private @Nullable Float f3;
        @Databind(name = "Float preset")
        private @NonNull Float f4 = -8.4322f;

        @Databind(name = "double")
        private double d1;
        @Databind(name = "double preset")
        private double d2 = 65.876245242;
        @Databind(name = "Double")
        private @Nullable Double d3;
        @Databind(name = "Double preset")
        private @NonNull Double d4 = -0.2349236545462373;


        @Databind(name = "BigDecimal")
        private @Nullable BigDecimal bd1;
        @Databind(name = "BigDecimal preset")
        private @NonNull BigDecimal bd2 = new BigDecimal("-23984235661561454.1981236152351623416");
    }
}

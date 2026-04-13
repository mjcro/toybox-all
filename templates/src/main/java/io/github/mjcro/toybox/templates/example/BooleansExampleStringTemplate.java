package io.github.mjcro.toybox.templates.example;

import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Label;
import io.github.mjcro.toybox.api.Menu;
import io.github.mjcro.toybox.templates.AbstractStringTemplateToy;
import io.github.mjcro.toybox.templates.Databind;
import io.github.mjcro.toybox.templates.StringProducer;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Example string template demonstrating boolean and {@link Boolean} field bindings.
 */
public class BooleansExampleStringTemplate extends AbstractStringTemplateToy {
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
        return Label.ofName("Booleans Template");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected @NonNull StringProducer getDataObject(@NonNull Context context) {
        return new Template();
    }

    /**
     * Data template holding boolean fields for the example.
     */
    private static class Template extends AbstractTemplateHelper {
        @Databind(name = "bool")
        private boolean b1;
        @Databind(name = "bool preset", group = "Preset values")
        private boolean b2 = true;
        @Databind(name = "Boolean")
        private @Nullable Boolean b3;
        @Databind(name = "Boolean preset", group = "Preset values")
        private @NonNull Boolean b4 = Boolean.TRUE;
    }
}

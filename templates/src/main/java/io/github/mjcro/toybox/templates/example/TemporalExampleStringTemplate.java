package io.github.mjcro.toybox.templates.example;

import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Label;
import io.github.mjcro.toybox.api.Menu;
import io.github.mjcro.toybox.templates.AbstractStringTemplateToy;
import io.github.mjcro.toybox.templates.Databind;
import io.github.mjcro.toybox.templates.StringProducer;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Example string template demonstrating {@link LocalDate} and
 * {@link LocalDateTime} field bindings.
 */
public class TemporalExampleStringTemplate extends AbstractStringTemplateToy {
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
        return Label.ofName("Temporal Template");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected @NonNull StringProducer getDataObject(@NonNull Context context) {
        return new Data();
    }

    /**
     * Data template holding temporal fields for the example.
     */
    private static class Data implements StringProducer {
        @Databind(name = "Local Date")
        private @Nullable LocalDate localDate;

        @Databind(name = "Preset Local Date")
        private @NonNull LocalDate presetLocalDate = LocalDate.now();

        @Databind(name = "Local DateTime")
        private @Nullable LocalDateTime localDateTime;

        @Databind(name = "Preset Local DateTime")
        private @NonNull LocalDateTime presetLocalDateTime = LocalDateTime.now();

        /**
         * {@inheritDoc}
         */
        @Override
        public void produce(@NonNull StringBuilder sb) {
            sb.append("Local Date: ").append(localDate).append("\n");
            sb.append("Preset Local Date: ").append(presetLocalDate).append("\n");
            sb.append("Local DateTime: ").append(localDateTime).append("\n");
            sb.append("Preset Local DateTime: ").append(presetLocalDateTime).append("\n");
        }
    }
}

package io.github.mjcro.toybox.templates.example;

import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Label;
import io.github.mjcro.toybox.api.Menu;
import io.github.mjcro.toybox.templates.AbstractStringTemplateToy;
import io.github.mjcro.toybox.templates.Databind;
import io.github.mjcro.toybox.templates.StringProducer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TemporalExampleStringTemplate extends AbstractStringTemplateToy  {
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
        return Label.ofName("Temporal Template");
    }

    @Override
    protected StringProducer getDataObject(final Context context) {
        return new Data();
    }

    private static class Data implements StringProducer {
        @Databind(name = "Local Date")
        private LocalDate localDate;

        @Databind(name = "Preset Local Date")
        private LocalDate presetLocalDate = LocalDate.now();

        @Databind(name = "Local DateTime")
        private LocalDateTime localDateTime;

        @Databind(name = "Preset Local DateTime")
        private LocalDateTime presetLocalDateTime = LocalDateTime.now();

        @Override
        public void produce(StringBuilder sb) {
            sb.append("Local Date: ").append(localDate).append("\n");
            sb.append("Preset Local Date: ").append(presetLocalDate).append("\n");
            sb.append("Local DateTime: ").append(localDateTime).append("\n");
            sb.append("Preset Local DateTime: ").append(presetLocalDateTime).append("\n");
        }
    }
}

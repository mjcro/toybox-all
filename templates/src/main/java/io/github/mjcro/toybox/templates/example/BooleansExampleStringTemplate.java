package io.github.mjcro.toybox.templates.example;

import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Label;
import io.github.mjcro.toybox.api.Menu;
import io.github.mjcro.toybox.templates.AbstractStringTemplateToy;
import io.github.mjcro.toybox.templates.Databind;
import io.github.mjcro.toybox.templates.StringProducer;

import java.util.ArrayList;
import java.util.List;

public class BooleansExampleStringTemplate extends AbstractStringTemplateToy {
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
        return Label.ofName("Booleans Template");
    }

    @Override
    protected StringProducer getDataObject(Context context) {
        return new Template();
    }

    private static class Template extends AbstractTemplateHelper {
        @Databind(name = "bool")
        private boolean b1;
        @Databind(name = "bool preset")
        private boolean b2 = true;
        @Databind(name = "Boolean")
        private Boolean b3;
        @Databind(name = "Boolean preset")
        private Boolean b4 = Boolean.TRUE;
    }
}

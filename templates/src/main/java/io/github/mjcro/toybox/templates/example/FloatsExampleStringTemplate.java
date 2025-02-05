package io.github.mjcro.toybox.templates.example;

import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Label;
import io.github.mjcro.toybox.api.Menu;
import io.github.mjcro.toybox.templates.AbstractStringTemplateToy;
import io.github.mjcro.toybox.templates.Databind;
import io.github.mjcro.toybox.templates.StringProducer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class FloatsExampleStringTemplate extends AbstractStringTemplateToy {
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
        return Label.ofName("Floats Template");
    }

    @Override
    protected StringProducer getDataObject(Context context) {
        return new Template();
    }

    private static class Template extends AbstractTemplateHelper {
        @Databind(name = "float")
        private float f1;
        @Databind(name = "float preset")
        private float f2 = 0.32f;
        @Databind(name = "Float")
        private Float f3;
        @Databind(name = "Float preset")
        private Float f4 = -8.4322f;

        @Databind(name = "double")
        private double d1;
        @Databind(name = "double preset")
        private double d2 = 65.876245242;
        @Databind(name = "Double")
        private Double d3;
        @Databind(name = "Double preset")
        private Double d4 = -0.2349236545462373;


        @Databind(name = "BigDecimal")
        private BigDecimal bd1;
        @Databind(name = "BigDecimal preset")
        private BigDecimal bd2 = new BigDecimal("-23984235661561454.1981236152351623416");
    }
}
package io.github.mjcro.toybox.templates.example;

import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Label;
import io.github.mjcro.toybox.api.Menu;
import io.github.mjcro.toybox.templates.AbstractStringTemplateToy;
import io.github.mjcro.toybox.templates.Databind;
import io.github.mjcro.toybox.templates.StringProducer;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class IntegersExampleStringTemplate extends AbstractStringTemplateToy {
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
        return Label.ofName("Integers Template");
    }

    @Override
    protected StringProducer getDataObject(Context context) {
        return new Template();
    }

    private static class Template extends AbstractTemplateHelper {
        @Databind(name = "byte")
        private byte b1;
        @Databind(name = "byte preset")
        private byte b2 = 22;
        @Databind(name = "Byte")
        private Byte b3;
        @Databind(name = "Byte preset")
        private Byte b4 = 92;

        @Databind(name = "short")
        private short s1;
        @Databind(name = "byte preset")
        private short s2 = -1812;
        @Databind(name = "Short")
        private Short s3;
        @Databind(name = "Short preset")
        private Short s4 = 621;

        @Databind(name = "int")
        private int i1;
        @Databind(name = "int preset")
        private int i2 = 897944665;
        @Databind(name = "Integer")
        private Integer i3;
        @Databind(name = "Integer preset")
        private Integer i4 = -234796615;

        @Databind(name = "long")
        private long l1;
        @Databind(name = "long preset")
        private long l2 = 23498273942525L;
        @Databind(name = "Long")
        private Long l3;
        @Databind(name = "Long preset")
        private Long l4 = -9267635514162762L;

        @Databind(name = "BigInteger")
        private BigInteger bi1;
        @Databind(name = "BigInteger preset")
        private BigInteger bi2 = new BigInteger("5983465837493874922034627364237542783645");
    }
}
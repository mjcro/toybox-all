package io.github.mjcro.toybox.templates.example;

import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Label;
import io.github.mjcro.toybox.api.Menu;
import io.github.mjcro.toybox.templates.AbstractStringTemplateToy;
import io.github.mjcro.toybox.templates.Databind;
import io.github.mjcro.toybox.templates.StringProducer;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Example string template demonstrating integer-type field bindings
 * including primitives, boxed types, and {@link BigInteger}.
 */
public class IntegersExampleStringTemplate extends AbstractStringTemplateToy {
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
        return Label.ofName("Integers Template");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected @NonNull StringProducer getDataObject(@NonNull Context context) {
        return new Template();
    }

    /**
     * Data template holding integer fields for the example.
     */
    private static class Template extends AbstractTemplateHelper {
        @Databind(name = "byte")
        private byte b1;
        @Databind(name = "byte preset")
        private byte b2 = 22;
        @Databind(name = "Byte")
        private @Nullable Byte b3;
        @Databind(name = "Byte preset")
        private @NonNull Byte b4 = 92;

        @Databind(name = "short")
        private short s1;
        @Databind(name = "byte preset")
        private short s2 = -1812;
        @Databind(name = "Short")
        private @Nullable Short s3;
        @Databind(name = "Short preset")
        private @NonNull Short s4 = 621;

        @Databind(name = "int")
        private int i1;
        @Databind(name = "int preset")
        private int i2 = 897944665;
        @Databind(name = "Integer")
        private @Nullable Integer i3;
        @Databind(name = "Integer preset")
        private @NonNull Integer i4 = -234796615;

        @Databind(name = "long")
        private long l1;
        @Databind(name = "long preset")
        private long l2 = 23498273942525L;
        @Databind(name = "Long")
        private @Nullable Long l3;
        @Databind(name = "Long preset")
        private @NonNull Long l4 = -9267635514162762L;

        @Databind(name = "BigInteger")
        private @Nullable BigInteger bi1;
        @Databind(name = "BigInteger preset")
        private @NonNull BigInteger bi2 = new BigInteger("5983465837493874922034627364237542783645");
    }
}

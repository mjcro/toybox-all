package io.github.mjcro.toybox.templates.example;

import io.github.mjcro.toybox.templates.Databind;
import io.github.mjcro.toybox.templates.StringProducer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

abstract class AbstractTemplateHelper implements StringProducer {
    @Override
    public void produce(StringBuilder sb) throws Exception {
        ArrayList<String> names = new ArrayList<>();
        ArrayList<Object> values = new ArrayList<>();
        for (Field field : this.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(Databind.class)) {
                continue;
            }
            field.setAccessible(true);

            Databind databind = field.getAnnotation(Databind.class);
            String name = databind.name().isEmpty()
                    ? field.getName()
                    : databind.name();

            names.add(name);
            values.add(field.get(this));
        }

        int max = names.stream().mapToInt(String::length).max().orElse(1);
        String fmt = "%-" + max + "s : %s\n";

        sb.append("Provided data:\n");
        for (int i = 0; i < names.size(); i++) {
            Object value = values.get(i);
            if (value == null) {
                value = "<null>";
            } else if (value instanceof Collection<?>) {
                value = "Collection [" + ((Collection<?>) value).stream().map(Object::toString).collect(Collectors.joining(",")) + "]";
            } else if (value instanceof String[]) {
                value = "String [" + String.join(",", (String[]) value) + "]";
            } else if (value instanceof long[]) {
                value = "long [" + Arrays.stream((long[]) value).boxed().map(Object::toString).collect(Collectors.joining(",")) + "]";
            }
            sb.append(String.format(Locale.ROOT, fmt, names.get(i), value));
        }
    }

    @Override
    public Optional<String> getInitialString() {
        return Optional.of("Change values and hit \"Apply\" button");
    }
}

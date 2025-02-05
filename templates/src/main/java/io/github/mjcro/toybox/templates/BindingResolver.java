package io.github.mjcro.toybox.templates;

import io.github.mjcro.interfaces.tuples.OptionalPair;
import io.github.mjcro.toybox.api.Environment;
import io.github.mjcro.toybox.api.Label;
import io.github.mjcro.toybox.templates.bindings.BooleanBinding;
import io.github.mjcro.toybox.templates.bindings.EnumerationBinding;
import io.github.mjcro.toybox.templates.bindings.FileBinding;
import io.github.mjcro.toybox.templates.bindings.LongArrayBinding;
import io.github.mjcro.toybox.templates.bindings.NumberBinding;
import io.github.mjcro.toybox.templates.bindings.StringArrayCsvBinding;
import io.github.mjcro.toybox.templates.bindings.StringBinding;
import lombok.NonNull;

import java.io.File;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class BindingResolver {
    public List<Binding> getBindings(Environment environment, @NonNull Object obj) {
        Class<?> clazz = obj.getClass();
        List<Field> fields = new ArrayList<>();
        while (clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                Databind anno = field.getAnnotation(Databind.class);
                if (anno != null) {
                    field.setAccessible(true);
                    fields.add(field);
                }
            }
            clazz = clazz.getSuperclass();
        }

        List<Binding> out = new ArrayList<>();
        for (Field field : fields) {
            Class<?> type = field.getType();
            AnnotatedType annotatedType = field.getAnnotatedType();
            out.add(getBinding(environment, obj, field, type, annotatedType));
        }

        return out;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Binding getBinding(Environment environment, Object obj, Field field, Class<?> fieldType, AnnotatedType annotatedType) {
        if (field.getAnnotation(Databind.class).enumerationProvider() != VoidEnumerationValues.class) {
            try {
                Constructor<? extends Iterable<OptionalPair<?, Label>>> ctor = field.getAnnotation(Databind.class).enumerationProvider().getConstructor();
                ctor.setAccessible(true);
                return new EnumerationBinding(obj, field, ctor.newInstance());
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }
        if (fieldType == String.class) {
            return new StringBinding(obj, field);
        }
        if (
                fieldType == Byte.class || fieldType == byte.class
                        || fieldType == Short.class || fieldType == short.class
                        || fieldType == Integer.class || fieldType == int.class
                        || fieldType == Long.class || fieldType == long.class
                        || fieldType == BigInteger.class
        ) {
            return new NumberBinding(obj, field);
        }
        if (
                fieldType == Float.class || fieldType == float.class
                        || fieldType == Double.class || fieldType == double.class
                        || fieldType == BigDecimal.class
        ) {
            return new NumberBinding(obj, field);
        }
        if (fieldType == boolean.class || fieldType == Boolean.class) {
            return new BooleanBinding(obj, field);
        }
        if (Enum.class.isAssignableFrom(fieldType)) {
            return EnumerationBinding.ofEnum(obj, field, (Class<? extends Enum>) fieldType);
        }
        if (fieldType == long[].class) {
            return new LongArrayBinding(obj, field);
        }
        if (fieldType == String[].class) {
            return new StringArrayCsvBinding(obj, field);
        }
        if (fieldType == File.class) {
            return new FileBinding(environment, obj, field);
        }

        throw new RuntimeException("Unsupported");
    }
}

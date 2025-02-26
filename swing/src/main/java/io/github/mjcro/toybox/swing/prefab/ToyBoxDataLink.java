package io.github.mjcro.toybox.swing.prefab;

import io.github.mjcro.toybox.swing.hint.Hint;
import io.github.mjcro.toybox.swing.linking.ComponentDataLink;
import io.github.mjcro.toybox.swing.linking.JCheckBoxBooleanDataLink;
import io.github.mjcro.toybox.swing.linking.JTextComponentNumberDataLink;
import io.github.mjcro.toybox.swing.linking.JTextComponentStringDataLink;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.lang.reflect.Field;
import java.util.Optional;
import java.util.function.UnaryOperator;

public class ToyBoxDataLink {
    @SafeVarargs
    public static ComponentDataLink<JCheckBox, Boolean> linkBooleanField(
            JCheckBox component,
            Field f,
            Object target,
            Hint<? super JCheckBox>... hints
    ) {
        f.setAccessible(true);
        JCheckBoxBooleanDataLink link = new JCheckBoxBooleanDataLink(component, b -> {
            try {
                if (f.getType().isPrimitive()) {
                    f.setBoolean(target, b.orElse(false));
                } else {
                    f.set(target, b.orElse(null));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });

        Hint.applyAll(component, hints);
        try {
            Optional.ofNullable(f.get(target))
                    .map($ -> (Boolean) $)
                    .ifPresent(link::setValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return link;
    }

    @SafeVarargs
    public static <C extends JTextComponent> ComponentDataLink<C, String> linkString(
            C component,
            String initial,
            UnaryOperator<String> transform,
            Hint<? super C>... hints
    ) {
        JTextComponentStringDataLink<C> link = new JTextComponentStringDataLink<>(component, transform, null);
        Hint.applyAll(component, hints);
        Optional.ofNullable(initial).ifPresent(link::setValue);
        return link;
    }

    @SafeVarargs
    public static <C extends JTextComponent> ComponentDataLink<C, String> linkStringField(
            C component,
            Field f,
            Object target,
            UnaryOperator<String> transform,
            Hint<? super C>... hints
    ) {
        f.setAccessible(true);
        JTextComponentStringDataLink<C> link = new JTextComponentStringDataLink<>(component, transform, s -> {
            try {
                f.set(target, s.orElse(null));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });

        Hint.applyAll(component, hints);

        try {
            Optional.ofNullable(f.get(target)).map(Object::toString).ifPresent(link::setValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return link;
    }

    @SafeVarargs
    public static <C extends JTextComponent, V extends Number> ComponentDataLink<C, V> linkNumberField(
            C component,
            Field f,
            Object target,
            Hint<? super C>... hints
    ) {
        f.setAccessible(true);
        @SuppressWarnings("unchecked") Class<V> clazz = (Class<V>) f.getType();
        JTextComponentNumberDataLink<C, V> link = new JTextComponentNumberDataLink<>(component, clazz, v -> {
            try {
                if (v.isEmpty() && clazz.isPrimitive()) {
                    f.set(target, 0);
                } else {
                    f.set(target, v.orElse(null));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });

        Hint.applyAll(component, hints);

        try {
            Optional.ofNullable(f.get(target)).map($ -> (V) $).ifPresent(link::setValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return link;
    }

    private ToyBoxDataLink() {
    }
}

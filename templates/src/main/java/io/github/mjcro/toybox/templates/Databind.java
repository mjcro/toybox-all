package io.github.mjcro.toybox.templates;

import io.github.mjcro.interfaces.tuples.OptionalPair;
import io.github.mjcro.toybox.api.Label;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation indicating that field should be editable
 * on template panel.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Databind {
    /**
     * @return Name to display on template panel. If empty, field name will be taken.
     */
    String name() default "";

    /**
     * @return If true, space trimming wherever possible will be applied.
     */
    boolean trim() default true;

    /**
     * @return Class providing enumeration values.
     */
    Class<? extends Iterable<OptionalPair<?, Label>>> enumerationProvider() default VoidEnumerationValues.class;
}

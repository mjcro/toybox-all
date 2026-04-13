package io.github.mjcro.toybox.templates;

import io.github.mjcro.interfaces.tuples.OptionalPair;
import io.github.mjcro.toybox.api.Label;
import org.jspecify.annotations.NonNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation indicating that a field should be editable on a template panel.
 * Fields annotated with this are discovered by {@link BindingResolver} and
 * rendered as interactive Swing components.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Databind {
    /**
     * Returns the display name for this field on the template panel.
     * When empty, the field name is used instead.
     *
     * @return the display name, or empty string to use the field name
     */
    @NonNull String name() default "";

    /**
     * Returns the group name this field belongs to.
     *
     * @return the group name, or empty string for the default group
     */
    @NonNull String group() default "";

    /**
     * Returns the display order for this binding. Lower values are displayed first.
     *
     * @return the sort order
     */
    int order() default 0;

    /**
     * Returns whether space trimming should be applied wherever possible.
     *
     * @return {@code true} to trim whitespace
     */
    boolean trim() default true;

    /**
     * Returns the class providing enumeration values for this field.
     *
     * @return the enumeration provider class
     */
    @NonNull Class<? extends Iterable<OptionalPair<?, Label>>> enumerationProvider() default VoidEnumerationValues.class;

    /**
     * Returns the options class associated with this field.
     *
     * @return the options class, or {@code Void.class} if none
     */
    @NonNull Class<?> options() default Void.class;
}

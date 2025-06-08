package io.github.mjcro.toybox.api;

/**
 * Defines application setting.
 * Each setting must be concrete class, name and namespace are for
 * information purposes only.
 */
public interface Setting {
    /**
     * @return Setting namespace.
     */
    String getNamespace();

    /**
     * @return Setting name.
     */
    String getName();

    /**
     * @return Setting value.
     */
    Object getValue();

    /**
     * Prepares value for rendering in settings viewer.
     * Can and must mask sensitive data.
     *
     * @return Value to display in settings viewer toys.
     */
    default String getDisplayValue() {
        Object v = getValue();
        return v == null ? "null" : v.toString();
    }
}

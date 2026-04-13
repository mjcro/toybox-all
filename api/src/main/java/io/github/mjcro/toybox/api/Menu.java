package io.github.mjcro.toybox.api;

import io.github.mjcro.interfaces.ints.WithOrder;
import org.jspecify.annotations.NonNull;

/**
 * Represents a menu entry in the ToyBox navigation hierarchy.
 * <p>
 * Predefined constants define the top-level system menus. Factory methods
 * create text-only, icon-text, or toy-backed menu items.
 */
public interface Menu extends Labeled, WithOrder {
    @NonNull Menu
            FILE_MENU = new SystemMenu(-1, "file", "File", null),
            TEMPLATES_MENU = new SystemMenu(100, "toybox://templates", "Templates", null),
            TOYBOX_MENU = new SystemMenu(Integer.MAX_VALUE, "toybox://main", "ToyBox", null),
            TOYBOX_DEVELOPMENT_MENU = new SystemMenu(-1, "toybox://main/dev", "Development", "fam://cup"),
            TOYBOX_BASIC_TOOLS_MENU = new SystemMenu(1_000_000, "toybox://main/basictools", "Basic tools", null),
            TOYBOX_BASIC_TOOLS_CRYPTO_SUBMENU = new SystemMenu(0, "toybox://main/basictools/crypto", "Cryptography", null),
            TOYBOX_EXAMPLES_SUBMENU = new SystemMenu(-1, "toybox://main/dev/examples", "Examples", null);

    /**
     * Creates a text-only menu item with the given display string.
     *
     * @param s Display text.
     * @return Menu instance.
     */
    static @NonNull Menu text(@NonNull String s) {
        return LabelMenu.ofName(s);
    }

    /**
     * Creates a menu item with an icon and display name.
     *
     * @param icon Icon URI.
     * @param name Display name.
     * @return Menu instance.
     */
    static @NonNull Menu iconText(@NonNull String icon, @NonNull String name) {
        return LabelMenu.ofIconAndName(icon, name);
    }

    /**
     * Creates a menu item that shows the given toy with the specified display order.
     *
     * @param order   Display order.
     * @param context ToyBox context used to show the toy.
     * @param toy     Toy to show when the menu item is activated.
     * @return Menu instance.
     */
    static @NonNull Menu toy(int order, @NonNull Context context, @NonNull Toy toy) {
        Label label = toy.getLabel();
        return new ActionMenu(
                order,
                Action.of(label, () -> context.show(toy, null))
        );
    }

    /**
     * Creates a menu item that shows the given toy, deriving display order from the toy label.
     *
     * @param context ToyBox context used to show the toy.
     * @param toy     Toy to show when the menu item is activated.
     * @return Menu instance.
     */
    static @NonNull Menu toy(@NonNull Context context, @NonNull Toy toy) {
        Label label = toy.getLabel();
        return new ActionMenu(
                label instanceof WithOrder ? ((WithOrder) label).getOrder() : 0,
                Action.of(label, () -> context.show(toy, null))
        );
    }
}

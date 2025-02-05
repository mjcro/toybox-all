package io.github.mjcro.toybox.api;

import io.github.mjcro.interfaces.ints.WithOrder;

public interface Menu extends Labeled, WithOrder {
    Menu
            FILE_MENU = new SystemMenu(-1, "file", "File", null),
            TEMPLATES_MENU = new SystemMenu(100, "toybox://templates", "Templates", null),
            TOYBOX_MENU = new SystemMenu(Integer.MAX_VALUE, "toybox://main", "Toybox", null),
            TOYBOX_DEVELOPMENT_MENU = new SystemMenu(Integer.MAX_VALUE - 1, "toybox://main/dev", "Development", null),
            TOYBOX_BASIC_TOOLS_SUBMENU = new SystemMenu(0, "toybox://main/basictools", "Basic tools", null),
            TOYBOX_EXAMPLES_SUBMENU = new SystemMenu(0, "toybox://main/dev/examples", "Examples", null);

    static Menu text(String s) {
        return new TextMenu(0, s);
    }

    static Menu toy(int order, Context context, Toy toy) {
        Label label = toy.getLabel();
        return new ActionMenu(
                order,
                Action.of(label, () -> context.show(toy, null))
        );
    }

    static Menu toy(Context context, Toy toy) {
        Label label = toy.getLabel();
        return new ActionMenu(
                label instanceof WithOrder ? ((WithOrder) label).getOrder() : 0,
                Action.of(label, () -> context.show(toy, null))
        );
    }
}

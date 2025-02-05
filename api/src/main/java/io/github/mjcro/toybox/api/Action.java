package io.github.mjcro.toybox.api;

/**
 * Defines abstract action.
 * <p>
 * This action can be rendered as menu item, button or other component
 * depending on application needs and placement context.
 */
public interface Action extends Runnable, Labeled {
    /**
     * Constructs new action instance using given label and action.
     *
     * @param label  Action label.
     * @param action Action runnable.
     * @return Action.
     */
    static Action of(Label label, Runnable action) {
        return new ActionImpl(label, action);
    }

    /**
     * Constructs new action instance using given name and action.
     * Constructed action will have no icon or other styling, just name.
     *
     * @param name   Action name.
     * @param action Action runnable.
     * @return Action.
     */
    static Action ofName(String name, Runnable action) {
        return of(Label.ofName(name), action);
    }

    /**
     * Constructs new action instance using given name, style type and action.
     *
     * @param name   Action name.
     * @param style  Action style.
     * @param action Action runnable.
     * @return Action.
     */
    static Action ofNameAndStyle(String name, String style, Runnable action) {
        return of(Label.ofNameStyle(name, style), action);
    }
}

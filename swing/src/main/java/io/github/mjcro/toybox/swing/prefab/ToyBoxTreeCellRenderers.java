package io.github.mjcro.toybox.swing.prefab;

import io.github.mjcro.interfaces.Decorator;
import io.github.mjcro.interfaces.enums.WithType;
import io.github.mjcro.toybox.swing.hint.Hint;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import java.util.Map;

public class ToyBoxTreeCellRenderers {
    private static DefaultTreeCellRenderer simpleTextAndIcon;

    static {
        reinitialize();
    }

    /**
     * Re-initializes all static components.
     * Can be useful on UI/LaF change.
     */
    public static void reinitialize() {
        simpleTextAndIcon = new DefaultTreeCellRenderer();
    }

    /**
     * Constructs new non-shared instance of {@link DefaultTreeCellRenderer}
     * configured using given hints.
     *
     * @param hints Hints to use for renderer.
     * @return Renderer.
     */
    @SafeVarargs
    public static DefaultTreeCellRenderer createHinted(Hint<? super DefaultTreeCellRenderer>... hints) {
        DefaultTreeCellRenderer r = new DefaultTreeCellRenderer();
        for (Hint<? super DefaultTreeCellRenderer> hint : hints) {
            hint.apply(r);
        }
        return r;
    }

    /**
     * Builds cell renderer dispatcher that chooses corresponding
     * renderer according to cell value type.
     *
     * @param map Renderers configuration map.
     * @return Renderer dispatcher.
     */
    public static <T extends Enum<T>, X extends WithType<T>> TreeCellRenderer typeSelector(Map<T, TreeCellRenderer> map) {
        DefaultTreeCellRenderer fallback = new DefaultTreeCellRenderer();
        return (t, value, s, e, l, r, f) -> {
            if (value instanceof DefaultMutableTreeNode) {
                value = ((DefaultMutableTreeNode) value).getUserObject();
            }
            if (value instanceof WithType<?>) {
                T type = (T) ((WithType<?>) value).getType();
                if (value instanceof Decorator<?>) {
                    value = ((Decorator<?>) value).getDecorated();
                }

                TreeCellRenderer render = map.get(type);
                if (render != null) {
                    return render.getTreeCellRendererComponent(t, value, s, e, l, r, f);
                }
            }
            return fallback;
        };
    }

    /**
     * Constructs new non-shared instance of {@link DefaultTreeCellRenderer}
     * with predefined icon.
     *
     * @param icon Icon to use.
     * @return Renderer.
     */
    public static DefaultTreeCellRenderer createWithIcon(Icon icon) {
        DefaultTreeCellRenderer r = new DefaultTreeCellRenderer();
        r.setOpenIcon(icon);
        r.setClosedIcon(icon);
        r.setLeafIcon(icon);
        return r;
    }

    /**
     * Constructs new non-shared instance of {@link DefaultTreeCellRenderer}
     * with predefined icon.
     *
     * @param icon Icon to use.
     * @return Renderer.
     */
    public static DefaultTreeCellRenderer createWithIcon(String icon) {
        return createWithIcon(ToyBoxIcons.get(icon).orElse(null));
    }

    /**
     * Returns shared renderer with given icon and text.
     *
     * @param icon Icon to show.
     * @param text Text to show.
     * @return Renderer.
     */
    public static TreeCellRenderer iconText(Icon icon, String text) {
        simpleTextAndIcon.setOpenIcon(icon);
        simpleTextAndIcon.setClosedIcon(icon);
        simpleTextAndIcon.setLeafIcon(icon);
        simpleTextAndIcon.setText(text);
        return simpleTextAndIcon;
    }

    /**
     * Returns shared renderer with given icon and text.
     *
     * @param iconUri Icon to show.
     * @param text    Text to show.
     * @return Renderer.
     */
    public static TreeCellRenderer iconText(String iconUri, String text) {
        Icon icon = iconUri != null ? ToyBoxIcons.get(iconUri).orElse(null) : null;
        return iconText(icon, text);
    }
}

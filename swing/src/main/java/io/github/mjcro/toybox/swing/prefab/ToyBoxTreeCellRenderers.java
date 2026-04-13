package io.github.mjcro.toybox.swing.prefab;

import io.github.mjcro.interfaces.Decorator;
import io.github.mjcro.interfaces.enums.WithType;
import io.github.mjcro.toybox.swing.hint.Hint;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import java.util.Map;

/**
 * Factory methods for creating and configuring {@link TreeCellRenderer} instances.
 */
public class ToyBoxTreeCellRenderers {
    private static @NonNull DefaultTreeCellRenderer simpleTextAndIcon = new DefaultTreeCellRenderer();

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
    public static @NonNull DefaultTreeCellRenderer createHinted(@NonNull Hint<? super DefaultTreeCellRenderer>... hints) {
        final DefaultTreeCellRenderer r = new DefaultTreeCellRenderer();
        for (final Hint<? super DefaultTreeCellRenderer> hint : hints) {
            hint.apply(r);
        }
        return r;
    }

    /**
     * Builds cell renderer dispatcher that chooses corresponding
     * renderer according to cell value type.
     *
     * @param map Renderers configuration map.
     * @param <T> The enum type used as key.
     * @param <X> The value type implementing {@link WithType}.
     * @return Renderer dispatcher.
     */
    public static <T extends Enum<T>, X extends WithType<T>> @NonNull TreeCellRenderer typeSelector(@NonNull Map<@NonNull T, @NonNull TreeCellRenderer> map) {
        final DefaultTreeCellRenderer fallback = new DefaultTreeCellRenderer();
        return (t, value, s, e, l, r, f) -> {
            if (value instanceof DefaultMutableTreeNode) {
                value = ((DefaultMutableTreeNode) value).getUserObject();
            }
            if (value instanceof WithType<?>) {
                @SuppressWarnings("unchecked") final T type = (T) ((WithType<?>) value).getType();
                if (value instanceof Decorator<?>) {
                    value = ((Decorator<?>) value).getDecorated();
                }

                final TreeCellRenderer render = map.get(type);
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
    public static @NonNull DefaultTreeCellRenderer createWithIcon(@Nullable Icon icon) {
        final DefaultTreeCellRenderer r = new DefaultTreeCellRenderer();
        r.setOpenIcon(icon);
        r.setClosedIcon(icon);
        r.setLeafIcon(icon);
        return r;
    }

    /**
     * Constructs new non-shared instance of {@link DefaultTreeCellRenderer}
     * with predefined icon resolved by URI.
     *
     * @param icon Icon URI to use.
     * @return Renderer.
     */
    public static @NonNull DefaultTreeCellRenderer createWithIcon(@NonNull String icon) {
        return createWithIcon(ToyBoxIcons.get(icon).orElse(null));
    }

    /**
     * Returns shared renderer with given icon and text.
     *
     * @param icon Icon to show.
     * @param text Text to show.
     * @return Renderer.
     */
    public static @NonNull TreeCellRenderer iconText(@Nullable Icon icon, @Nullable String text) {
        simpleTextAndIcon.setOpenIcon(icon);
        simpleTextAndIcon.setClosedIcon(icon);
        simpleTextAndIcon.setLeafIcon(icon);
        simpleTextAndIcon.setText(text);
        return simpleTextAndIcon;
    }

    /**
     * Returns shared renderer with given icon URI and text.
     *
     * @param iconUri Icon URI to show, or null.
     * @param text    Text to show.
     * @return Renderer.
     */
    public static @NonNull TreeCellRenderer iconText(@Nullable String iconUri, @Nullable String text) {
        final Icon icon = iconUri != null ? ToyBoxIcons.get(iconUri).orElse(null) : null;
        return iconText(icon, text);
    }
}

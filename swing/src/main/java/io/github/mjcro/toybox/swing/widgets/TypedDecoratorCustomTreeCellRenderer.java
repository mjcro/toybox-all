package io.github.mjcro.toybox.swing.widgets;

import io.github.mjcro.toybox.swing.TypedDecorator;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;
import java.awt.Component;
import java.util.EnumMap;
import java.util.Objects;

/**
 * Tree cell renderer that delegates rendering to type-specific renderers
 * based on {@link TypedDecorator} node values.
 *
 * @param <T> the enum type used to distinguish decorator categories
 */
public class TypedDecoratorCustomTreeCellRenderer<T extends Enum<T>> extends CustomTreeCellRenderer {
    private final @NonNull EnumMap<@NonNull T, @NonNull TreeCellRenderer> renderers;

    /**
     * Creates a new renderer with the given enum-to-renderer mapping.
     *
     * @param renderers mapping from enum types to their respective tree cell renderers
     */
    public TypedDecoratorCustomTreeCellRenderer(@NonNull EnumMap<@NonNull T, @NonNull TreeCellRenderer> renderers) {
        this.renderers = Objects.requireNonNull(renderers);
    }

    @Override
    public @NonNull Component getTreeCellRendererComponent(
            @NonNull JTree tree,
            @Nullable Object value,
            boolean selected,
            boolean expanded,
            boolean leaf,
            int row,
            boolean hasFocus
    ) {
        if (value instanceof TypedDecorator<?, ?>) {
            @SuppressWarnings("unchecked")
            final TypedDecorator<T, ?> typedDecorator = (TypedDecorator<T, ?>) value;
            final TreeCellRenderer r = renderers.get(typedDecorator.getType());
            if (r != null) {
                return r.getTreeCellRendererComponent(tree, typedDecorator.getDecorated(), selected, expanded, leaf, row, hasFocus);
            }
        }
        return super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
    }
}

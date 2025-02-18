package io.github.mjcro.toybox.swing.widgets;

import io.github.mjcro.toybox.swing.Hint;
import io.github.mjcro.toybox.swing.TypedDecorator;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;
import java.util.EnumMap;
import java.util.Objects;

public class TypedDecoratorCustomTreeCellRenderer<T extends Enum<T>> extends CustomTreeCellRenderer {
    private final EnumMap<T, TreeCellRenderer> renderers;

    public static TreeCellRenderer hinted(Hint<? super DefaultTreeCellRenderer>... hints) {
        DefaultTreeCellRenderer r = new DefaultTreeCellRenderer();
        for (Hint<? super DefaultTreeCellRenderer> hint : hints) {
            hint.apply(r);
        }
        return r;
    }

    public TypedDecoratorCustomTreeCellRenderer(EnumMap<T, TreeCellRenderer> renderers) {
        this.renderers = Objects.requireNonNull(renderers);
    }

    @Override
    public Component getTreeCellRendererComponent(
            JTree tree,
            Object value,
            boolean selected,
            boolean expanded,
            boolean leaf,
            int row,
            boolean hasFocus
    ) {
        if (value instanceof TypedDecorator<?, ?>) {
            TypedDecorator<T, ?> typedDecorator = (TypedDecorator<T, ?>) value;
            TreeCellRenderer r = renderers.get(typedDecorator.getType());
            if (r != null) {
                return r.getTreeCellRendererComponent(tree, typedDecorator.getDecorated(), selected, expanded, leaf, row, hasFocus);
            }
        }
        return super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
    }
}

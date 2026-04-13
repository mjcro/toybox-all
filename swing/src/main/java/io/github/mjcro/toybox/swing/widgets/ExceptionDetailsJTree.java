package io.github.mjcro.toybox.swing.widgets;

import io.github.mjcro.toybox.swing.hint.Hints;
import io.github.mjcro.toybox.swing.prefab.ToyBoxIcons;
import io.github.mjcro.toybox.swing.prefab.ToyBoxLabels;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.crypto.AEADBadTagException;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;

/**
 * Tree component that renders an exception chain with stack traces,
 * class information, and contextual hints.
 */
public class ExceptionDetailsJTree extends JTree {

    /**
     * Creates a tree displaying the given exception chain.
     *
     * @param e the exception to display, or {@code null} for an empty tree
     */
    public ExceptionDetailsJTree(@Nullable Throwable e) {
        setCellRenderer(new Renderer());
        setRootVisible(false);
        setException(e);
    }

    /**
     * Creates an empty exception details tree.
     */
    public ExceptionDetailsJTree() {
        this(null);
    }

    /**
     * Sets or clears the exception displayed in this tree.
     *
     * @param e the exception to display, or {@code null} to clear
     */
    public void setException(@Nullable Throwable e) {
        if (e == null) {
            setModel(null);
            return;
        }

        final DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        attach(root, e);
        setModel(new DefaultTreeModel(root));
        expandRow(0);
    }

    /**
     * Recursively attaches an exception and its cause chain to the tree.
     *
     * @param node the parent tree node
     * @param e    the exception to attach
     */
    private void attach(@NonNull DefaultMutableTreeNode node, @NonNull Throwable e) {
        final DefaultMutableTreeNode n = new DefaultMutableTreeNode();
        node.add(n);
        node = n;

        node.setUserObject(e);
        final Throwable cause = e.getCause();
        if (cause != null && cause != e) {
            attach(node, cause);
        }
        node.add(new DefaultMutableTreeNode(e.getClass()));

        attachHintsRecursively(node, e);

        // Adding stack trace
        final StackTraceElement[] stackTrace = e.getStackTrace();
        if (stackTrace != null && stackTrace.length > 0) {
            for (final StackTraceElement element : stackTrace) {
                node.add(new DefaultMutableTreeNode(element));
            }
        }
    }

    /**
     * Recursively inspects the exception cause chain and attaches user-friendly hints.
     *
     * @param node the parent tree node
     * @param e    the exception to inspect, or {@code null}
     */
    private void attachHintsRecursively(@NonNull DefaultMutableTreeNode node, @Nullable Throwable e) {
        if (e == null) {
            return;
        }
        final Throwable cause = e.getCause();
        if (cause != null && cause != e) {
            attachHintsRecursively(node, cause);
        }

        // Suggesting hint
        final Class<?> clazz = e.getClass();
        if (clazz == NumberFormatException.class) {
            node.add(new DefaultMutableTreeNode(new ExceptionHint("Possible problem with number parsing")));
        } else if (clazz == RuntimeException.class) {
            node.add(new DefaultMutableTreeNode(new ExceptionHint("General runtime exception")));
        } else if (clazz == AEADBadTagException.class) {
            node.add(new DefaultMutableTreeNode(new ExceptionHint("Incorrect password given or corrupted data")));
        }
    }

    /**
     * Tree cell renderer for exception detail nodes, handling throwables,
     * class names, stack trace elements, and hints.
     */
    private static class Renderer extends CustomTreeCellRenderer {
        private final @NonNull JLabel rootLabel = ToyBoxLabels.create();
        private final @NonNull JLabel messageLabel = ToyBoxLabels.create();
        private final @NonNull JLabel classLabel = ToyBoxLabels.create();
        private final @NonNull JLabel hintLabel = ToyBoxLabels.create();
        private final @NonNull StackPanel stackLabel = new StackPanel();

        Renderer() {
            ToyBoxIcons.get("fam://bug").ifPresent(messageLabel::setIcon);
            ToyBoxIcons.get("fam://tag").ifPresent(classLabel::setIcon);
            ToyBoxIcons.get("fam://lightbulb").ifPresent(hintLabel::setIcon);

            Hints.RENDERER_JTREE_PADDING.apply(messageLabel);
            Hints.RENDERER_JTREE_PADDING.apply(classLabel);
            Hints.RENDERER_JTREE_PADDING.apply(stackLabel);
            Hints.RENDERER_JTREE_PADDING.apply(hintLabel);

            Hints.BOLD.apply(messageLabel);
            Hints.TEXT_MONOSPACED.apply(classLabel);
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
            if (value instanceof DefaultMutableTreeNode) {
                value = ((DefaultMutableTreeNode) value).getUserObject();
            }

            final Color color = selected ? colorSelectedFg : colorNormalFg;

            if (value instanceof Throwable) {
                final Throwable t = (Throwable) value;
                @Nullable String message = t.getMessage();
                if (message == null) {
                    message = t.getClass().getName();
                }
                messageLabel.setText(message);
                messageLabel.setForeground(color);
                return messageLabel;
            }
            if (value instanceof Class<?>) {
                classLabel.setText(((Class<?>) value).getName());
                classLabel.setForeground(color);
                return classLabel;
            }
            if (value instanceof StackTraceElement) {
                stackLabel.set((StackTraceElement) value);
                stackLabel.setForegroundColor(color);
                return stackLabel;
            }
            if (value instanceof ExceptionHint) {
                hintLabel.setText(((ExceptionHint) value).string);
                return hintLabel;
            }

            return rootLabel;
        }
    }

    /**
     * Holder for a user-friendly hint message associated with an exception.
     */
    private static class ExceptionHint {
        private final @NonNull String string;

        private ExceptionHint(@NonNull String string) {
            this.string = string;
        }
    }

    /**
     * Panel that renders a single stack trace element with class name, file name, and line number.
     */
    private static class StackPanel extends JPanel {
        private final @NonNull JLabel className = ToyBoxLabels.create();
        private final @NonNull JLabel fileName = ToyBoxLabels.create();
        private final @NonNull JLabel line = ToyBoxLabels.create();

        private final @Nullable Icon traceIcon;
        private final @Nullable Icon traceLambdaIcon;
        private final @Nullable Icon traceToyBoxIcon;
        private final @Nullable Icon traceJavaCoreIcon;

        StackPanel() {
            super(new FlowLayout(FlowLayout.LEFT, 0, 0));
            setOpaque(false);
            add(className);
            add(fileName);
            add(line);

            Hints.ITALIC.apply(className);
            fileName.setBorder(new EmptyBorder(0, 5, 0, 5));

            this.traceIcon = ToyBoxIcons.get("fam://bullet_yellow").orElse(null);
            this.traceLambdaIcon = ToyBoxIcons.get("fam://bullet_blue").orElse(null);
            this.traceToyBoxIcon = ToyBoxIcons.get("fam://bullet_pink").orElse(null);
            this.traceJavaCoreIcon = ToyBoxIcons.get("fam://bullet_white").orElse(null);
        }

        /**
         * Sets the foreground color for all sub-labels.
         *
         * @param color the color to apply
         */
        void setForegroundColor(@NonNull Color color) {
            className.setForeground(color);
            fileName.setForeground(color);
            line.setForeground(color);
        }

        /**
         * Populates this panel from the given stack trace element.
         *
         * @param e the stack trace element to display
         */
        void set(@NonNull StackTraceElement e) {
            final String cn = e.getClassName();
            if (cn.startsWith("java")) {
                className.setIcon(traceJavaCoreIcon);
            } else if (cn.startsWith("io.github.mjcro.toybox")) {
                className.setIcon(traceToyBoxIcon);
            } else if (cn.contains("$")) {
                className.setIcon(traceLambdaIcon);
            } else {
                className.setIcon(traceIcon);
            }

            className.setText(cn);
            fileName.setText(e.getFileName());
            final int lineNumber = e.getLineNumber();
            line.setText(lineNumber > 0 ? String.valueOf(lineNumber) : null);
        }
    }
}

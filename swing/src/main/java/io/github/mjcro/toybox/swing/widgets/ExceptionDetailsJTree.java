package io.github.mjcro.toybox.swing.widgets;

import io.github.mjcro.toybox.swing.Icons;
import io.github.mjcro.toybox.swing.Styles;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;

public class ExceptionDetailsJTree extends JTree {

    public ExceptionDetailsJTree(Throwable e) {
        setCellRenderer(new Renderer());
        setRootVisible(false);
        setException(e);
    }

    public ExceptionDetailsJTree() {
        this(null);
    }

    public void setException(Throwable e) {
        if (e == null) {
            setModel(null);
            return;
        }

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        attach(root, e);
        setModel(new DefaultTreeModel(root));
        expandRow(0);
    }

    private void attach(DefaultMutableTreeNode node, Throwable e) {
        DefaultMutableTreeNode n = new DefaultMutableTreeNode();
        node.add(n);
        node = n;

        node.setUserObject(e);
        Throwable cause = e.getCause();
        if (cause != null && cause != e) {
            attach(node, cause);
        }
        node.add(new DefaultMutableTreeNode(e.getClass()));

        // Suggesting hint
        Class<?> clazz = e.getClass();
        if (clazz == NumberFormatException.class) {
            node.add(new DefaultMutableTreeNode(new ExceptionHint("Possible problem with number parsing")));
        } else if (clazz == RuntimeException.class) {
            node.add(new DefaultMutableTreeNode(new ExceptionHint("General runtime exception")));
        }

        // Adding stack trace
        StackTraceElement[] stackTrace = e.getStackTrace();
        if (stackTrace != null && stackTrace.length > 0) {
            for (StackTraceElement element : stackTrace) {
                node.add(new DefaultMutableTreeNode(element));
            }
        }
    }

    private static class Renderer extends CustomTreeCellRenderer {
        private final JLabel rootLabel = new JLabel();
        private final JLabel messageLabel = new JLabel();
        private final JLabel classLabel = new JLabel();
        private final JLabel hintLabel = new JLabel();
        private final StackPanel stackLabel = new StackPanel();

        Renderer() {
            Icons.get("fam://bug").ifPresent(messageLabel::setIcon);
            Icons.get("fam://tag").ifPresent(classLabel::setIcon);
            Icons.get("fam://lightbulb").ifPresent(hintLabel::setIcon);

            Styles.RENDERER_JTREE_PADDING.apply(messageLabel);
            Styles.RENDERER_JTREE_PADDING.apply(classLabel);
            Styles.RENDERER_JTREE_PADDING.apply(stackLabel);
            Styles.RENDERER_JTREE_PADDING.apply(hintLabel);

            Styles.BOLD.apply(messageLabel);
            Styles.TEXT_MONOSPACED.apply(classLabel);
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
            if (value instanceof DefaultMutableTreeNode) {
                value = ((DefaultMutableTreeNode) value).getUserObject();
            }

            Color color = selected ? colorSelectedFg : colorNormalFg;

            if (value instanceof Throwable) {
                Throwable t = (Throwable) value;
                String message = t.getMessage();
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

    private static class ExceptionHint {
        private final String string;

        private ExceptionHint(String string) {
            this.string = string;
        }
    }

    private static class StackPanel extends JPanel {
        private final JLabel className = new JLabel();
        private final JLabel fileName = new JLabel();
        private final JLabel line = new JLabel();

        private final Icon traceIcon;
        private final Icon traceLambdaIcon;
        private final Icon traceToyboxIcon;
        private final Icon traceJavaCoreIcon;

        StackPanel() {
            super(new FlowLayout(FlowLayout.LEFT, 0, 0));
            setOpaque(false);
            add(className);
            add(fileName);
            add(line);

            Styles.ITALIC.apply(className);
            fileName.setBorder(new EmptyBorder(0, 5, 0, 5));

            this.traceIcon = Icons.get("fam://bullet_yellow").orElse(null);
            this.traceLambdaIcon = Icons.get("fam://bullet_blue").orElse(null);
            this.traceToyboxIcon = Icons.get("fam://bullet_pink").orElse(null);
            this.traceJavaCoreIcon = Icons.get("fam://bullet_white").orElse(null);
        }

        void setForegroundColor(Color color) {
            className.setForeground(color);
            fileName.setForeground(color);
            line.setForeground(color);
        }

        void set(StackTraceElement e) {
            String cn = e.getClassName();
            if (cn.startsWith("java")) {
                className.setIcon(traceJavaCoreIcon);
            } else if (cn.startsWith("io.github.mjcro.toybox")) {
                className.setIcon(traceToyboxIcon);
            } else if (cn.contains("$")) {
                className.setIcon(traceLambdaIcon);
            } else {
                className.setIcon(traceIcon);
            }

            className.setText(cn);
            fileName.setText(e.getFileName());
            int lineNumber = e.getLineNumber();
            line.setText(lineNumber > 0 ? String.valueOf(lineNumber) : null);
        }
    }
}

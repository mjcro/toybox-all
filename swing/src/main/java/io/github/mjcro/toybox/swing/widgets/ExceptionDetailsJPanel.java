package io.github.mjcro.toybox.swing.widgets;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;

/**
 * Panel that displays detailed exception information in a scrollable tree view.
 */
public class ExceptionDetailsJPanel extends JPanel {
    private final @NonNull ExceptionDetailsJTree view;

    /**
     * Opens a new window displaying the exception details.
     *
     * @param e the exception to display
     */
    public static void newWindow(@NonNull Throwable e) {
        final JFrame frame = new JFrame();
        frame.setTitle("Exception details: " + e.getMessage());
        frame.getContentPane().add(new ExceptionDetailsJPanel(e));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setSize(800, 800);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Creates a panel showing details for the given exception.
     *
     * @param e the exception to display, or {@code null} for an empty view
     */
    public ExceptionDetailsJPanel(@Nullable Throwable e) {
        super(new BorderLayout());
        view = new ExceptionDetailsJTree(e);
        add(new JScrollPane(view));
    }

    /**
     * Creates an empty exception details panel.
     */
    public ExceptionDetailsJPanel() {
        this(null);
    }

    /**
     * Replaces the displayed exception.
     *
     * @param e the exception to display, or {@code null} to clear
     */
    public void setException(@Nullable Throwable e) {
        view.setException(e);
    }
}

package io.github.mjcro.toybox.swing.widgets.panels;

import io.github.mjcro.toybox.swing.Components;
import io.github.mjcro.toybox.swing.prefab.ToyBoxButtons;
import io.github.mjcro.toybox.swing.prefab.ToyBoxLabels;
import io.github.mjcro.toybox.swing.widgets.ExceptionDetailsJPanel;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;

/**
 * A compact notification panel that displays either a success message
 * or an exception with dismiss and details buttons.
 */
public class ShortInformationPanel extends JPanel {
    private final @NonNull JLabel messageLabel = ToyBoxLabels.create("");
    private final @NonNull JButton dismissButton = ToyBoxButtons.create("Dismiss", this::onDismissButtonClick);
    private final @NonNull JButton detailsButton = ToyBoxButtons.create("Details", this::onDetailsButtonClick);

    private final @NonNull Color successBg = new Color(178, 225, 208);
    private final @NonNull Color errorBg = new Color(225, 178, 209);

    private @Nullable Throwable exception;

    /**
     * Creates a new panel in the hidden (none) state.
     */
    public ShortInformationPanel() {
        super(new BorderLayout());

        super.setBorder(new EmptyBorder(2, 10, 2, 10));
        super.add(messageLabel, BorderLayout.CENTER);

        final JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        buttons.add(detailsButton);
        buttons.add(dismissButton);
        super.add(buttons, BorderLayout.LINE_END);

        setNone();
    }

    /**
     * Hides the panel and clears its content.
     */
    public void setNone() {
        messageLabel.setText("");
        messageLabel.setVisible(false);
        detailsButton.setVisible(false);
        dismissButton.setVisible(false);
        setVisible(false);
    }

    /**
     * Displays a success message. If the message is {@code null} or blank,
     * the panel is hidden instead.
     *
     * @param message the success message to display, or {@code null} to hide
     */
    public void setSuccess(@Nullable String message) {
        if (message == null || message.isBlank()) {
            setNone();
            return;
        }

        setBackground(successBg);

        messageLabel.setText(message);
        messageLabel.setVisible(true);
        detailsButton.setVisible(false);
        dismissButton.setVisible(true);
        setVisible(true);
    }

    /**
     * Displays an error from the given exception. If the cause is {@code null},
     * the panel is hidden instead.
     *
     * @param cause the exception to display, or {@code null} to hide
     */
    public void setException(@Nullable Throwable cause) {
        this.exception = cause;

        if (cause == null) {
            setNone();
            return;
        }

        setBackground(errorBg);

        messageLabel.setText(cause.getMessage());
        messageLabel.setVisible(true);
        detailsButton.setVisible(true);
        dismissButton.setVisible(true);
        setVisible(true);
    }

    /**
     * Handles the dismiss button click by hiding the panel.
     *
     * @param e the action event
     */
    public void onDismissButtonClick(@NonNull ActionEvent e) {
        setNone();
    }

    /**
     * Handles the details button click by opening the exception details window.
     *
     * @param e the action event
     */
    public void onDetailsButtonClick(@NonNull ActionEvent e) {
        if (this.exception != null) {
            ExceptionDetailsJPanel.newWindow(this.exception);
        }
    }

    /**
     * Demo entry point for testing the short information panel.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(@NonNull String[] args) {
        final ShortInformationPanel p = new ShortInformationPanel();
        Components.showLine(p);
        p.setSuccess("Hello");
        p.setException(new RuntimeException("XXX"));
    }
}

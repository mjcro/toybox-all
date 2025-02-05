package io.github.mjcro.toybox.swing.widgets.panels;

import io.github.mjcro.toybox.swing.Components;
import io.github.mjcro.toybox.swing.factories.ButtonsFactory;
import io.github.mjcro.toybox.swing.factories.LabelsFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ShortInformationPanel extends JPanel {
    private final JLabel messageLabel = LabelsFactory.create("");
    private final JButton dismissButton = ButtonsFactory.create("Dismiss", this::onDismissButtonClick);
    private final JButton detailsButton = ButtonsFactory.create("Details");

    private final Color successBg = new Color(178, 225, 208);
    private final Color errorBg = new Color(225, 178, 209);

    public ShortInformationPanel() {
        super(new BorderLayout());

        super.setBorder(new EmptyBorder(2, 10, 2, 10));
        super.add(messageLabel, BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        buttons.add(detailsButton);
        buttons.add(dismissButton);
        super.add(buttons, BorderLayout.LINE_END);

        setNone();
    }

    public void setNone() {
        messageLabel.setText("");
        messageLabel.setVisible(false);
        detailsButton.setVisible(false);
        dismissButton.setVisible(false);
        setVisible(false);
    }

    public void setSuccess(String message) {
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

    public void setException(Throwable cause) {
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

    public void onDismissButtonClick(ActionEvent e) {
        setNone();
    }

    public static void main(String[] args) {
        ShortInformationPanel p = new ShortInformationPanel();
        Components.showLine(p);
        p.setSuccess("Hello");
        p.setException(new RuntimeException("XXX"));
    }
}

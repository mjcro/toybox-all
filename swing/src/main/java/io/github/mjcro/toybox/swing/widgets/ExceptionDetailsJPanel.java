package io.github.mjcro.toybox.swing.widgets;

import javax.swing.*;
import java.awt.*;

public class ExceptionDetailsJPanel extends JPanel {
    private final ExceptionDetailsJTree view;

    public static void newWindow(Throwable e) {
        JFrame frame = new JFrame();
        frame.setTitle("Exception details: " + e.getMessage());
        frame.getContentPane().add(new ExceptionDetailsJPanel(e));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setSize(800, 800);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public ExceptionDetailsJPanel(Throwable e) {
        super(new BorderLayout());
        view = new ExceptionDetailsJTree(e);
        add(new JScrollPane(view));
    }

    public ExceptionDetailsJPanel() {
        this(null);
    }

    public void setException(Throwable e) {
        view.setException(e);
    }
}

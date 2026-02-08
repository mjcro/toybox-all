package io.github.mjcro.toybox.swing.widgets;

import javax.swing.*;
import java.awt.*;

public abstract class MultiViewBasicPanel extends JPanel {
    public static final String
            CARD_EMPTY = "empty",
            CARD_LABEL = "label",
            CARD_EXCEPTION = "exception";

    private final LabelOnlyJPanel labelOnlyJPanel = new LabelOnlyJPanel();
    private final ExceptionDetailsJPanel exceptionDetailsJPanel = new ExceptionDetailsJPanel();

    public MultiViewBasicPanel() {
        super(new CardLayout());

        add(new JPanel(), CARD_EMPTY);
        add(labelOnlyJPanel, CARD_LABEL);
        add(exceptionDetailsJPanel, CARD_EXCEPTION);
    }

    public void setSelectedCard(String card) {
        ((CardLayout) getLayout()).show(this, card);
    }

    public void setViewEmpty() {
        setSelectedCard(CARD_EMPTY);
    }

    public void setViewLabel(String value) {
        labelOnlyJPanel.label.setText(value);
        setSelectedCard(CARD_LABEL);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        labelOnlyJPanel.setEnabled(enabled);
        exceptionDetailsJPanel.setEnabled(enabled);
    }

    public void setViewException(Throwable t) {
        if (t == null) {
            setViewEmpty();
        } else {
            exceptionDetailsJPanel.setException(t);
            setSelectedCard(CARD_EXCEPTION);
        }
    }

    private static final class LabelOnlyJPanel extends JPanel {
        private final JLabel label = new JLabel();

        LabelOnlyJPanel() {
            add(label);
        }
    }
}
